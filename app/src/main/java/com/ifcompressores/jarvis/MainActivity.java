package com.ifcompressores.jarvis;

import android.app.role.RoleManager;
import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.speech.RecognizerIntent;
import android.speech.tts.TextToSpeech;
import android.webkit.JavascriptInterface;
import android.webkit.WebChromeClient;
import android.webkit.WebResourceRequest;
import android.webkit.WebResourceResponse;
import android.webkit.WebView;
import android.webkit.WebViewClient;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.webkit.WebViewAssetLoader;

import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Locale;

public class MainActivity extends AppCompatActivity {
    private static final String PAGE = "https://appassets.androidplatform.net/assets/jarvis.html";

    private WebView webView;
    private TextToSpeech tts;
    private boolean ttsReady = false;
    private boolean recognizing = false;

    private final ActivityResultLauncher<Intent> voiceLauncher = registerForActivityResult(
        new ActivityResultContracts.StartActivityForResult(), result -> {
            recognizing = false;
            if (result.getResultCode() == RESULT_OK && result.getData() != null) {
                ArrayList<String> texts = result.getData().getStringArrayListExtra(RecognizerIntent.EXTRA_RESULTS);
                if (texts != null && !texts.isEmpty()) {
                    callJs("jarvisNativeResult", texts.get(0));
                    return;
                }
            }
            callJs("jarvisNativeStatus", "Captura cancelada. Toque para tentar novamente.");
        }
    );

    private final ActivityResultLauncher<Intent> assistantRoleLauncher = registerForActivityResult(
        new ActivityResultContracts.StartActivityForResult(), result -> {
            if (result.getResultCode() == RESULT_OK) {
                callJs("jarvisNativeStatus", "IF Jarvis definido como assistente do Android.");
            } else {
                callJs("jarvisNativeStatus", "IF Jarvis ainda não é o assistente padrão.");
            }
        }
    );

    @Override
    protected void onCreate(Bundle state) {
        super.onCreate(state);
        WindowCompat.setDecorFitsSystemWindows(getWindow(), false);
        setContentView(R.layout.activity_main);

        webView = findViewById(R.id.webView);

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.root_view), (view, insets) -> {
            androidx.core.graphics.Insets bars = insets.getInsets(
                WindowInsetsCompat.Type.systemBars() | WindowInsetsCompat.Type.ime()
            );
            view.setPadding(bars.left, bars.top, bars.right, bars.bottom);
            return WindowInsetsCompat.CONSUMED;
        });

        webView.getSettings().setJavaScriptEnabled(true);
        webView.getSettings().setDomStorageEnabled(true);
        webView.getSettings().setAllowFileAccess(false);
        webView.getSettings().setAllowContentAccess(false);

        final WebViewAssetLoader loader = new WebViewAssetLoader.Builder()
            .addPathHandler("/assets/", new WebViewAssetLoader.AssetsPathHandler(this))
            .build();

        webView.setWebViewClient(new WebViewClient() {
            @Override
            public WebResourceResponse shouldInterceptRequest(WebView view, WebResourceRequest request) {
                return loader.shouldInterceptRequest(request.getUrl());
            }

            @Override
            public boolean shouldOverrideUrlLoading(WebView view, WebResourceRequest request) {
                return !PAGE.equals(request.getUrl().toString());
            }
        });

        webView.setWebChromeClient(new WebChromeClient());
        webView.addJavascriptInterface(new VoiceBridge(), "AndroidVoice");

        tts = new TextToSpeech(this, status -> {
            if (status == TextToSpeech.SUCCESS) {
                int language = tts.setLanguage(new Locale("pt", "BR"));
                ttsReady = language != TextToSpeech.LANG_MISSING_DATA
                    && language != TextToSpeech.LANG_NOT_SUPPORTED;
            }
        });

        webView.loadUrl(PAGE);

        // Nesta versão de desenvolvimento queremos validar imediatamente se o
        // aparelho aceita o IF Jarvis como assistente global do Android.
        webView.postDelayed(this::requestAssistantRoleIfNeeded, 900);
    }

    private void requestAssistantRoleIfNeeded() {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.Q) {
            callJs("jarvisNativeStatus", "Este Android não suporta ROLE_ASSISTANT pelo RoleManager.");
            return;
        }

        RoleManager roleManager = getSystemService(RoleManager.class);
        if (roleManager == null || !roleManager.isRoleAvailable(RoleManager.ROLE_ASSISTANT)) {
            callJs("jarvisNativeStatus", "O papel de assistente não está disponível neste aparelho.");
            return;
        }

        if (roleManager.isRoleHeld(RoleManager.ROLE_ASSISTANT)) {
            callJs("jarvisNativeStatus", "IF Jarvis já é o assistente padrão do Android.");
            return;
        }

        assistantRoleLauncher.launch(
            roleManager.createRequestRoleIntent(RoleManager.ROLE_ASSISTANT)
        );
    }

    private void callJs(String function, String value) {
        if (webView == null || isFinishing() || isDestroyed()) return;
        webView.evaluateJavascript(
            "window." + function + " && window." + function + "(" + JSONObject.quote(value) + ");",
            null
        );
    }

    public final class VoiceBridge {
        @JavascriptInterface
        public void listen() {
            runOnUiThread(() -> {
                if (recognizing || isFinishing() || isDestroyed()) return;
                if (tts != null) tts.stop();

                Intent intent = new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH);
                intent.putExtra(
                    RecognizerIntent.EXTRA_LANGUAGE_MODEL,
                    RecognizerIntent.LANGUAGE_MODEL_FREE_FORM
                );
                intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE, "pt-BR");
                intent.putExtra(RecognizerIntent.EXTRA_MAX_RESULTS, 1);
                intent.putExtra(RecognizerIntent.EXTRA_PROMPT, "Fale para o IF Jarvis");

                try {
                    recognizing = true;
                    voiceLauncher.launch(intent);
                } catch (ActivityNotFoundException e) {
                    recognizing = false;
                    callJs(
                        "jarvisNativeStatus",
                        "Reconhecimento indisponível neste aparelho. Use o teclado."
                    );
                } catch (SecurityException e) {
                    recognizing = false;
                    callJs(
                        "jarvisNativeStatus",
                        "O serviço de voz não pôde abrir. Verifique suas permissões ou use o teclado."
                    );
                }
            });
        }

        @JavascriptInterface
        public void speak(String text) {
            runOnUiThread(() -> {
                if (isFinishing() || isDestroyed()) return;

                if (!ttsReady) {
                    callJs(
                        "jarvisNativeStatus",
                        "Voz pt-BR indisponível. A resposta está na tela."
                    );
                    return;
                }

                String spoken = text == null ? "" : text;
                int limit = TextToSpeech.getMaxSpeechInputLength();
                if (spoken.length() > limit) {
                    spoken = spoken.substring(0, limit - 50) + ". Leia o restante na tela.";
                }

                int status = tts.speak(
                    spoken,
                    TextToSpeech.QUEUE_FLUSH,
                    null,
                    "if-response"
                );

                if (status == TextToSpeech.ERROR) {
                    callJs(
                        "jarvisNativeStatus",
                        "Não foi possível reproduzir a voz. Leia a resposta."
                    );
                }
            });
        }

        @JavascriptInterface
        public void stopSpeaking() {
            runOnUiThread(() -> {
                if (tts != null) tts.stop();
            });
        }

        @JavascriptInterface
        public void requestAssistantRole() {
            runOnUiThread(MainActivity.this::requestAssistantRoleIfNeeded);
        }
    }

    @Override
    protected void onStop() {
        if (tts != null) tts.stop();
        super.onStop();
    }

    @Override
    protected void onDestroy() {
        if (tts != null) {
            tts.stop();
            tts.shutdown();
        }

        if (webView != null) {
            webView.removeJavascriptInterface("AndroidVoice");
            webView.destroy();
            webView = null;
        }

        super.onDestroy();
    }
}
