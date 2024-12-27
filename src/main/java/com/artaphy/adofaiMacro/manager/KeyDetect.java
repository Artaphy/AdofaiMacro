package com.artaphy.adofaiMacro.manager;

import org.jnativehook.keyboard.NativeKeyEvent;
import org.jnativehook.keyboard.NativeKeyListener;

import java.awt.*;

public class KeyDetect implements NativeKeyListener {
    public static boolean canCancel = false;

    @Override
    public void nativeKeyPressed(NativeKeyEvent e) {
        String key = NativeKeyEvent.getKeyText(e.getKeyCode());

        if (key.equalsIgnoreCase("Insert")) {
            if (Controller.adofai == null) return;
            try {
                if (canCancel) {
                    Controller.adofai.cancel();
                    canCancel = false;
                } else {
                    Controller.adofai.start();
                    canCancel = true;
                }
            } catch (AWTException awtException) {
                awtException.printStackTrace();
            }
        }
        // 左箭头
        if (e.getKeyCode() == 57419) {
            if (Controller.adofai == null) return;
            if (canCancel) {
                // -5ms
                Controller.adofai.time -= 5000000;
            }
        }
        // 右箭头
        if (e.getKeyCode() == 57421) {
            if (Controller.adofai == null) return;
            if (canCancel) {
                // +5ms
                Controller.adofai.time += 5000000;
            }
        }
    }

    @Override
    public void nativeKeyReleased(NativeKeyEvent e) {
        // 不需要处理按键释放事件
    }

    @Override
    public void nativeKeyTyped(NativeKeyEvent e) {
        // 不需要处理按键输入事件
    }
}



