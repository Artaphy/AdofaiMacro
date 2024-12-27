package com.artaphy.adofaiMacro.manager;

import javafx.fxml.FXML;
import javafx.scene.control.CheckBox;
import javafx.scene.control.Spinner;
import javafx.stage.Stage;

public class SettingsController {
    @FXML
    private Spinner<Integer> cooldownSpinner;
    @FXML
    private Spinner<Double> accuracySpinner;
    @FXML
    private CheckBox allowEarlyPerfectCheck;
    @FXML
    private CheckBox allowLatePerfectCheck;
    @FXML
    private CheckBox allowEarlyCheck;
    @FXML
    private CheckBox allowLateCheck;
    @FXML
    private CheckBox allowExtremeEarlyCheck;
    @FXML
    private CheckBox allowExtremeLateCheck;
    
    private Stage stage;
    
    public void setStage(Stage stage) {
        this.stage = stage;
    }
    
    @FXML
    public void initialize() {
        if (Controller.adofai != null) {
            // 加载当前设置
            cooldownSpinner.getValueFactory().setValue(
                (int)Controller.adofai.getCurrentCooldownTime()
            );
            
            // 加载判定设置
            accuracySpinner.getValueFactory().setValue(
                Controller.adofai.getTargetAccuracy()
            );
            
            allowEarlyPerfectCheck.setSelected(Controller.adofai.isAllowEarlyPerfect());
            allowLatePerfectCheck.setSelected(Controller.adofai.isAllowLatePerfect());
            allowEarlyCheck.setSelected(Controller.adofai.isAllowEarly());
            allowLateCheck.setSelected(Controller.adofai.isAllowLate());
            allowExtremeEarlyCheck.setSelected(Controller.adofai.isAllowExtremeEarly());
            allowExtremeLateCheck.setSelected(Controller.adofai.isAllowExtremeLate());
        }
    }
    
    @FXML
    public void saveSettings() {
        if (Controller.adofai != null) {
            // 保存冷却时间设置
            Controller.adofai.updateCooldownTime(cooldownSpinner.getValue());
            
            // 保存判定设置
            Controller.adofai.updateJudgementSettings(
                accuracySpinner.getValue(),
                allowEarlyPerfectCheck.isSelected(),
                allowLatePerfectCheck.isSelected(),
                allowEarlyCheck.isSelected(),
                allowLateCheck.isSelected(),
                allowExtremeEarlyCheck.isSelected(),
                allowExtremeLateCheck.isSelected()
            );
        }
        stage.close();
    }
} 