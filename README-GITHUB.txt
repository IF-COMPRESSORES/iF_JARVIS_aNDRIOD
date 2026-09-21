IF JARVIS — ANDROID 0.5

ARQUITETURA ATUAL
- Interface principal empacotada em HTML dentro do app.
- WebView carrega jarvis.html via WebViewAssetLoader.
- Supabase é usado para autenticação e dados do sistema IF.
- Reconhecimento de voz usa o serviço nativo do Android via RecognizerIntent.
- Respostas por voz usam TextToSpeech nativo do Android em pt-BR.
- O app possui fallback por teclado.

BUILD
- Java 17.
- Android compileSdk/targetSdk 36.
- Gradle 8.13.
- O GitHub Actions compila :app:assembleDebug e publica o APK como artifact.

IMPORTANTE
- Vosk não faz mais parte da arquitetura 0.5.
- Não existe escuta contínua em segundo plano nesta versão.
- O usuário toca no microfone para iniciar o reconhecimento nativo do Android.
- A autenticação deve usar uma conta real existente no mesmo projeto Supabase do sistema IF.

PRÓXIMOS PASSOS
1. estabilizar autenticação e recuperação de acesso;
2. validar leitura e gravação real no Supabase;
3. fechar fluxo completo de manutenção/estoque no aparelho;
4. somente depois avaliar wake word/assistente em segundo plano, caso ainda seja necessário.
