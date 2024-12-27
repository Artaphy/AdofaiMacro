package com.artaphy.adofaiMacro.clasz;

import java.util.Random;
import java.util.LinkedList;
import java.util.Queue;

/**
 * Manages judgements and timing errors for more realistic human-like play
 */
public class JudgementManager {
    // 判定等级对应的角度范围
    public static final double PERFECT_RANGE = 30.0;
    public static final double EARLY_PERFECT_RANGE = 45.0;
    public static final double LATE_PERFECT_RANGE = 45.0;
    public static final double EARLY_RANGE = 60.0;
    public static final double LATE_RANGE = 60.0;
    
    // 判定权重
    public static final double PERFECT_WEIGHT = 1.0;
    public static final double EARLY_LATE_PERFECT_WEIGHT = 0.75;
    public static final double EARLY_LATE_WEIGHT = 0.40;
    public static final double EARLY_LATE_EXTREME_WEIGHT = 0.20;
    
    // 误差生成相关参数
    private final Random random = new Random();
    private double targetAccuracy;  // 目标精准度
    private int totalNotes;         // 总音符数
    private int processedNotes;     // 已处理音符数
    private double currentAccuracy; // 当前精准度
    
    // 允许的判定类型
    private boolean allowEarlyPerfect = true;
    private boolean allowLatePerfect = true;
    private boolean allowEarly = false;
    private boolean allowLate = false;
    private boolean allowExtremeEarly = false;
    private boolean allowExtremeLate = false;
    
    // 记录最近的判定，用于生成连续性的误差
    private Queue<Double> recentErrors = new LinkedList<>();
    private static final int ERROR_HISTORY_SIZE = 5;
    
    // 玩家状态模拟
    private double currentTendency = 0.0;  // 当前的早/晚倾向
    private double stabilityFactor = 1.0;  // 稳定性因子
    
    public JudgementManager(int totalNotes, double targetAccuracy) {
        this.totalNotes = totalNotes;
        this.targetAccuracy = targetAccuracy;
        this.processedNotes = 0;
        this.currentAccuracy = 100.0;
        
        // 根据目标精准度初始化稳定性因子
        this.stabilityFactor = calculateStabilityFactor(targetAccuracy);
    }
    
    private double calculateStabilityFactor(double accuracy) {
        // 精准度越高，稳定性越好
        return 1.0 - ((100.0 - accuracy) / 100.0);
    }
    
    /**
     * 生成下一个音符的时间误差（纳秒）
     */
    public long generateError(long noteInterval) {
        processedNotes++;
        
        // 计算这个音符应该出现的判定类型
        JudgementType judgement = determineJudgementType();
        
        // 生成考虑连续性的误差角度
        double errorAngle = generateErrorAngleForJudgement(judgement);
        
        // 更新玩家状态
        updatePlayerState(errorAngle);
        
        // 更新精准度
        updateAccuracy(errorAngle);
        
        // 将角度误差转换为时间误差（纳秒）
        return (long) (noteInterval * errorAngle / 180.0);
    }
    
    private enum JudgementType {
        PERFECT,
        EARLY_PERFECT,
        LATE_PERFECT,
        EARLY,
        LATE,
        EXTREME_EARLY,
        EXTREME_LATE
    }
    
    private JudgementType determineJudgementType() {
        double rand = random.nextDouble();
        double perfectThreshold = calculatePerfectProbability();
        
        if (rand < perfectThreshold) {
            return JudgementType.PERFECT;
        }
        
        // 分配剩余概率给其他判定
        rand = random.nextDouble();
        if (allowEarlyPerfect && rand < 0.4) return JudgementType.EARLY_PERFECT;
        if (allowLatePerfect && rand < 0.8) return JudgementType.LATE_PERFECT;
        if (allowEarly && rand < 0.9) return JudgementType.EARLY;
        if (allowLate && rand < 0.95) return JudgementType.LATE;
        if (allowExtremeEarly) return JudgementType.EXTREME_EARLY;
        if (allowExtremeLate) return JudgementType.EXTREME_LATE;
        
        return JudgementType.PERFECT; // 默认返回Perfect
    }
    
    private double generateErrorAngleForJudgement(JudgementType type) {
        double baseError = 0.0;
        
        switch (type) {
            case PERFECT:
                baseError = random.nextDouble() * PERFECT_RANGE * (random.nextBoolean() ? 1 : -1);
                break;
            case EARLY_PERFECT:
                baseError = -PERFECT_RANGE - (random.nextDouble() * (EARLY_PERFECT_RANGE - PERFECT_RANGE));
                break;
            case LATE_PERFECT:
                baseError = PERFECT_RANGE + (random.nextDouble() * (LATE_PERFECT_RANGE - PERFECT_RANGE));
                break;
            case EARLY:
                baseError = -EARLY_PERFECT_RANGE - (random.nextDouble() * (EARLY_RANGE - EARLY_PERFECT_RANGE));
                break;
            case LATE:
                baseError = LATE_PERFECT_RANGE + (random.nextDouble() * (LATE_RANGE - LATE_PERFECT_RANGE));
                break;
            case EXTREME_EARLY:
                baseError = -EARLY_RANGE - (random.nextDouble() * 15.0); // 额外15度范围
                break;
            case EXTREME_LATE:
                baseError = LATE_RANGE + (random.nextDouble() * 15.0); // 额外15度范围
                break;
        }
        
        // 添加连续性影响
        baseError += currentTendency * (1.0 - stabilityFactor);
        
        // 添加微小的随机波动
        baseError += random.nextGaussian() * 2.0 * (1.0 - stabilityFactor);
        
        return baseError;
    }
    
    private void updatePlayerState(double errorAngle) {
        // 更新当前倾向
        currentTendency = currentTendency * 0.8 + errorAngle * 0.2;
        
        // 更新最近误差历史
        recentErrors.offer(errorAngle);
        if (recentErrors.size() > ERROR_HISTORY_SIZE) {
            recentErrors.poll();
        }
    }
    
    private double calculatePerfectProbability() {
        // 基于目标精准度计算Perfect的概率
        double baseProb = targetAccuracy / 100.0;
        
        // 根据当前精准度调整
        double accuracyDiff = targetAccuracy - currentAccuracy;
        baseProb += accuracyDiff * 0.01;
        
        // 确保概率在合理范围内
        return Math.max(0.5, Math.min(1.0, baseProb));
    }
    
    private void updateAccuracy(double errorAngle) {
        double weight;
        double absError = Math.abs(errorAngle);
        
        if (absError <= PERFECT_RANGE) {
            weight = PERFECT_WEIGHT;
        } else if (absError <= EARLY_PERFECT_RANGE) {
            weight = EARLY_LATE_PERFECT_WEIGHT;
        } else if (absError <= EARLY_RANGE) {
            weight = EARLY_LATE_WEIGHT;
        } else {
            weight = EARLY_LATE_EXTREME_WEIGHT;
        }
        
        currentAccuracy = ((currentAccuracy * (processedNotes - 1)) + (weight * 100)) / processedNotes;
    }
    
    // Getters and setters
    public void setAllowEarlyPerfect(boolean allow) { this.allowEarlyPerfect = allow; }
    public void setAllowLatePerfect(boolean allow) { this.allowLatePerfect = allow; }
    public void setAllowEarly(boolean allow) { this.allowEarly = allow; }
    public void setAllowLate(boolean allow) { this.allowLate = allow; }
    public void setAllowExtremeEarly(boolean allow) { this.allowExtremeEarly = allow; }
    public void setAllowExtremeLate(boolean allow) { this.allowExtremeLate = allow; }
    
    public double getCurrentAccuracy() { return currentAccuracy; }
    public boolean isAllowEarlyPerfect() { return allowEarlyPerfect; }
    public boolean isAllowLatePerfect() { return allowLatePerfect; }
    public boolean isAllowEarly() { return allowEarly; }
    public boolean isAllowLate() { return allowLate; }
    public boolean isAllowExtremeEarly() { return allowExtremeEarly; }
    public boolean isAllowExtremeLate() { return allowExtremeLate; }
} 