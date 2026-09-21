package com.ifcompressores.jarvis;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.GradientDrawable;
import android.os.Bundle;
import android.service.voice.VoiceInteractionSession;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

public class JarvisVoiceSession extends VoiceInteractionSession {
    private final Context context;

    public JarvisVoiceSession(Context context) {
        super(context);
        this.context = context;
    }

    @Override
    public View onCreateContentView() {
        LinearLayout root = new LinearLayout(context);
        root.setOrientation(LinearLayout.VERTICAL);
        root.setGravity(Gravity.CENTER_HORIZONTAL);
        root.setPadding(dp(24), dp(24), dp(24), dp(24));

        GradientDrawable background = new GradientDrawable();
        background.setColor(Color.rgb(10, 12, 15));
        background.setCornerRadii(new float[]{dp(28), dp(28), dp(28), dp(28), 0, 0, 0, 0});
        root.setBackground(background);

        TextView title = new TextView(context);
        title.setText("IF Jarvis");
        title.setTextColor(Color.WHITE);
        title.setTextSize(24f);
        title.setGravity(Gravity.CENTER);
        root.addView(title, matchWrap());

        TextView status = new TextView(context);
        status.setText("Assistente do sistema ativo\nFundação 0.6 carregada");
        status.setTextColor(Color.rgb(170, 177, 187));
        status.setTextSize(15f);
        status.setGravity(Gravity.CENTER);
        LinearLayout.LayoutParams statusParams = matchWrap();
        statusParams.topMargin = dp(10);
        root.addView(status, statusParams);

        Button openApp = new Button(context);
        openApp.setText("Abrir App IF");
        LinearLayout.LayoutParams buttonParams = new LinearLayout.LayoutParams(
            ViewGroup.LayoutParams.MATCH_PARENT,
            ViewGroup.LayoutParams.WRAP_CONTENT
        );
        buttonParams.topMargin = dp(20);
        root.addView(openApp, buttonParams);
        openApp.setOnClickListener(v -> {
            Intent launch = context.getPackageManager().getLaunchIntentForPackage(context.getPackageName());
            if (launch != null) {
                launch.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP);
                context.startActivity(launch);
            }
            finish();
        });

        Button close = new Button(context);
        close.setText("Fechar");
        LinearLayout.LayoutParams closeParams = new LinearLayout.LayoutParams(
            ViewGroup.LayoutParams.MATCH_PARENT,
            ViewGroup.LayoutParams.WRAP_CONTENT
        );
        closeParams.topMargin = dp(8);
        root.addView(close, closeParams);
        close.setOnClickListener(v -> finish());

        return root;
    }

    @Override
    public void onShow(Bundle args, int showFlags) {
        super.onShow(args, showFlags);
    }

    private LinearLayout.LayoutParams matchWrap() {
        return new LinearLayout.LayoutParams(
            ViewGroup.LayoutParams.MATCH_PARENT,
            ViewGroup.LayoutParams.WRAP_CONTENT
        );
    }

    private int dp(int value) {
        float density = context.getResources().getDisplayMetrics().density;
        return Math.round(value * density);
    }
}
