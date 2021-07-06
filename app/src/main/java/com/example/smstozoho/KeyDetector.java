package com.example.smstozoho;

import android.accessibilityservice.AccessibilityService;
import android.util.Log;
import android.view.KeyEvent;
import android.view.accessibility.AccessibilityEvent;
import android.view.accessibility.AccessibilityNodeInfo;

public class KeyDetector extends AccessibilityService {

    @Override
    public boolean onKeyEvent(KeyEvent event) {

        if (event.getKeyCode() == KeyEvent.KEYCODE_POWER) {
            Log.d("Test", "Long press!");
            //flag = false;
            //flag2 = true;
            return true;
        }
        Log.d("TAG","Key pressed via accessibility is: "+event.getKeyCode());
        //This allows the key pressed to function normally after it has been used by your app.
        return super.onKeyEvent(event);
    }

    @Override
    public void onAccessibilityEvent(AccessibilityEvent accessibilityEvent) {

    }

    @Override
    public void onInterrupt() {

    }
}
