package com.ifcompressores.jarvis;

import android.service.voice.VoiceInteractionService;
import android.util.Log;

public class JarvisVoiceInteractionService extends VoiceInteractionService {
    private static final String TAG = "IFJarvisVoiceService";

    @Override
    public void onReady() {
        super.onReady();
        Log.i(TAG, "IF Jarvis ativo como VoiceInteractionService.");
    }

    @Override
    public void onShutdown() {
        Log.i(TAG, "IF Jarvis VoiceInteractionService encerrado.");
        super.onShutdown();
    }
}
