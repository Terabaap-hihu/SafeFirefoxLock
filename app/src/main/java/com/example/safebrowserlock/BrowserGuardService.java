package com.example.safebrowserlock;

import android.accessibilityservice.AccessibilityService;
import android.content.*;
import android.view.accessibility.AccessibilityEvent;
import java.util.*;

public class BrowserGuardService extends AccessibilityService {
    private static final List<String> B = Arrays.asList("org.mozilla.firefox", "org.mozilla.fenix");

    @Override
    public void onAccessibilityEvent(AccessibilityEvent e) {
        if (e.getPackageName() == null) return;
        if (getSharedPreferences("SafeLockPrefs", MODE_PRIVATE).getLong("lock_until", 0) > System.currentTimeMillis() && B.contains(e.getPackageName().toString())) {
            performGlobalAction(GLOBAL_ACTION_HOME);
            Intent i = new Intent(this, MainActivity.class);
            i.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP);
            startActivity(i);
        }
    }

    @Override
    public void onInterrupt() {}
}
