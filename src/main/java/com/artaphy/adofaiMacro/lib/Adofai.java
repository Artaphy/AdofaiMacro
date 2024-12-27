package com.artaphy.adofaiMacro.lib;

import com.artaphy.adofaiMacro.clasz.JudgementManager;
import com.artaphy.adofaiMacro.clasz.KeyCooldown;
import com.artaphy.adofaiMacro.clasz.PressInfo;
import com.artaphy.adofaiMacro.manager.KeyDetect;
import javafx.scene.control.Label;
import org.json.simple.parser.ParseException;

import java.util.*;
import java.awt.*;
import java.util.List;

import static com.artaphy.adofaiMacro.Main.isFirst;
import static com.artaphy.adofaiMacro.Main.isSecond;

public class Adofai {
    public boolean isCancel = false;
    public static Thread thread;
    // 校准时间
    public long time = 0;
    private List<PressInfo> delayList;
    private Label isOn;
    private KeyCooldown keyCooldown;
    private JudgementManager judgementManager;
    private double targetAccuracy = 98.5;  // 默认精准度

    public Adofai(String path, Label isOn) throws ParseException {
        isCancel = false;
        this.isOn = isOn;
        this.keyCooldown = new KeyCooldown();
        delayList = new LoadMap(path).delays;
        this.judgementManager = new JudgementManager(delayList.size(), targetAccuracy);
    }

    public void cancel(){
        isCancel = true;
        isOn.setVisible(false);
        keyCooldown.reset();
        if(thread!=null) if (!thread.isInterrupted()) thread.interrupt();
    }

    public void start() throws AWTException {
        isCancel = false;
        isOn.setVisible(true);
        final Robot robot = new Robot();
        keyCooldown.reset();

        thread = new Thread(() -> {
            Iterator<PressInfo> pressInterator = delayList.iterator();

            long nowTime, prevTime = System.nanoTime();
            int prev = 0, now, delay;
            PressInfo press = pressInterator.next();

            if (isFirst){
                press.delay -= 150000000;
                isFirst = false;
                isSecond = true;
            }
            else if (isSecond) {
                press.delay += 150000000;
                isSecond = false;
            }

            while (true) {
                if (isCancel) break;
                nowTime = System.nanoTime() - time;
                
                if (nowTime - prevTime >= press.delay) {
                    // 更新时间基准点
                    prevTime += press.delay;

                    // 默认按键松开延迟
                    delay = 55;

                    if (pressInterator.hasNext()) {
                        press = pressInterator.next();
                    } else {
                        isOn.setVisible(false);
                        break;
                    }

                    PressInfo finalPress = press;
                    now = (int) (finalPress.delay / 1000000);

                    if (!finalPress.getIsAuto()){
                        int nextKey = keyCooldown.getNextAvailableKey(nowTime);
                        if (nextKey != -1) {
                            finalPress.key = nextKey;
                            // 先松开按键
                            robot.keyRelease(finalPress.key);
                            // 确保有足够的间隔
                            try {
                                Thread.sleep(1);
                            } catch (InterruptedException e) {
                                break;
                            }
                            // 按下按键
                            robot.keyPress(finalPress.key);
                        }
                    }

                    // 计算按键松开延迟
                    if (press.getHoldDelay() != 0) {
                        // 如果是长按，使用长按时间
                        delay = press.getHoldDelay();
                    } else {
                        // 根据BPM动态调整按键时长
                        if (now < 55) {
                            // 对于快速连打，使用更短的按键时长
                            delay = Math.max(5, now / 2);
                        } else {
                            // 对于普通速度，使用固定时长
                            delay = 55;
                        }
                    }

                    try {
                        Timer timer = new Timer();
                        TimerTask timerTask = new TimerTask() {
                            @Override
                            public void run() {
                                robot.keyRelease(finalPress.key);
                                timer.cancel();
                                timer.purge();
                            }
                        };
                        timer.schedule(timerTask, delay);
                    } catch (Exception ignored) {
                    }

                    prev = (int) (finalPress.delay / 1000000);
                } else {
                    // 添加短暂休眠以减少CPU使用
                    try {
                        Thread.sleep(1);
                    } catch (InterruptedException e) {
                        break;
                    }
                }
            }
            thread.interrupt();
            KeyDetect.canCancel = false;
        });
        thread.start();
    }
k
    public long getCurrentCooldownTime() {
        return keyCooldown != null ? keyCooldown.getCooldownTimeMs() : 120;
    }

    public void updateCooldownTime(long milliseconds) {
        if (keyCooldown != null) {
            keyCooldown.setCooldownTime(milliseconds);
        }
    }
    
    // 判定设置相关方法
    public double getTargetAccuracy() {
        return targetAccuracy;
    }
    
    public boolean isAllowEarlyPerfect() { return judgementManager != null && judgementManager.isAllowEarlyPerfect(); }
    public boolean isAllowLatePerfect() { return judgementManager != null && judgementManager.isAllowLatePerfect(); }
    public boolean isAllowEarly() { return judgementManager != null && judgementManager.isAllowEarly(); }
    public boolean isAllowLate() { return judgementManager != null && judgementManager.isAllowLate(); }
    public boolean isAllowExtremeEarly() { return judgementManager != null && judgementManager.isAllowExtremeEarly(); }
    public boolean isAllowExtremeLate() { return judgementManager != null && judgementManager.isAllowExtremeLate(); }
    
    public void updateJudgementSettings(
            double targetAccuracy,
            boolean allowEarlyPerfect,
            boolean allowLatePerfect,
            boolean allowEarly,
            boolean allowLate,
            boolean allowExtremeEarly,
            boolean allowExtremeLate) {
        this.targetAccuracy = targetAccuracy;
        if (judgementManager != null) {
            judgementManager = new JudgementManager(delayList.size(), targetAccuracy);
            judgementManager.setAllowEarlyPerfect(allowEarlyPerfect);
            judgementManager.setAllowLatePerfect(allowLatePerfect);
            judgementManager.setAllowEarly(allowEarly);
            judgementManager.setAllowLate(allowLate);
            judgementManager.setAllowExtremeEarly(allowExtremeEarly);
            judgementManager.setAllowExtremeLate(allowExtremeLate);
        }
    }
}