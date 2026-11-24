package com.example.safebrowserlock;

import android.content.*;
import android.graphics.Color;
import android.net.Uri;
import android.os.*;
import android.provider.Settings;
import android.view.*;
import android.widget.*;
import androidx.appcompat.app.AppCompatActivity;

public class MainActivity extends AppCompatActivity {
    private static final long LOCK_MS = 120L * 60L * 1000L;
    private TextView timerText, statusText;
    private Button permBtn;
    private Handler handler = new Handler();

    @Override
    protected void onCreate(Bundle b) {
        super.onCreate(b);
        LinearLayout l = new LinearLayout(this);
        l.setOrientation(LinearLayout.VERTICAL);
        l.setGravity(Gravity.CENTER);
        l.setBackgroundColor(Color.parseColor("#1e272e"));
        l.setPadding(40, 40, 40, 40);

        TextView ic = new TextView(this);
        ic.setText("LOCK");
        ic.setTextSize(40);
        ic.setTextColor(Color.WHITE);
        ic.setGravity(Gravity.CENTER);
        l.addView(ic);

        statusText = new TextView(this);
        statusText.setTextColor(Color.parseColor("#ff4757"));
        statusText.setTextSize(20);
        statusText.setGravity(Gravity.CENTER);
        l.addView(statusText);

        timerText = new TextView(this);
        timerText.setTextColor(Color.parseColor("#2ed573"));
        timerText.setTextSize(42);
        timerText.setGravity(Gravity.CENTER);
        l.addView(timerText);

        permBtn = new Button(this);
        permBtn.setText("Accessibility Permission ON Karein");
        permBtn.setOnClickListener(v -> startActivity(new Intent(Settings.ACTION_ACCESSIBILITY_SETTINGS)));
        l.addView(permBtn);

        setContentView(l);
        handleTrigger(getIntent());
        startClock();
    }

    @Override
    protected void onNewIntent(Intent i) {
        super.onNewIntent(i);
        handleTrigger(i);
    }

    private void handleTrigger(Intent i) {
        if (i != null && i.getData() != null) {
            Uri d = i.getData();
            if ("safelock".equals(d.getScheme()) && "trigger120".equals(d.getHost())) {
                SharedPreferences p = getSharedPreferences("SafeLockPrefs", MODE_PRIVATE);
                if (p.getLong("lock_until", 0) <= System.currentTimeMillis()) {
                    p.edit().putLong("lock_until", System.currentTimeMillis() + LOCK_MS).apply();
                }
            }
        }
    }

    private void startClock() {
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                SharedPreferences p = getSharedPreferences("SafeLockPrefs", MODE_PRIVATE);
                long rem = p.getLong("lock_until", 0) - System.currentTimeMillis();
                if (rem > 0) {
                    statusText.setText("FIREFOX LOCKED");
                    timerText.setText(String.format("%02d:%02d", rem / 60000, (rem % 60000) / 1000));
                    permBtn.setVisibility(View.GONE);
                } else {
                    statusText.setText("READY (UNLOCKED)");
                    timerText.setText("00:00");
                    permBtn.setVisibility(View.VISIBLE);
                }
                handler.postDelayed(this, 1000);
            }
        }, 200);
    }

    @Override
    public void onBackPressed() {
        if (getSharedPreferences("SafeLockPrefs", MODE_PRIVATE).getLong("lock_until", 0) <= System.currentTimeMillis()) {
            super.onBackPressed();
        }
    }
}
