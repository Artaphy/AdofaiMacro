package com.artaphy.adofaiMacro.manager;

import com.artaphy.adofaiMacro.lib.Adofai;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.stage.FileChooser;
import javafx.stage.Modality;
import javafx.stage.Stage;
import org.json.simple.parser.ParseException;

import java.io.File;
import java.io.IOException;

public class Controller {
    @FXML
    private Button button;
    @FXML
    private Label label;
    @FXML
    private Label lag;
    @FXML
    private Label on;
    @FXML
    private Button settingsButton;

    public static Adofai adofai;

    @FXML
    public void initialize() {
    }

    @FXML
    public void openSettings() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/settings.fxml"));
            Parent root = loader.load();
            
            Stage settingsStage = new Stage();
            settingsStage.initModality(Modality.APPLICATION_MODAL);
            settingsStage.setTitle("ADOFAI宏 设置");
            settingsStage.setScene(new Scene(root));
            
            SettingsController controller = loader.getController();
            controller.setStage(settingsStage);
            
            settingsStage.showAndWait();
        } catch (IOException e) {
            e.printStackTrace();
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("错误");
            alert.setHeaderText("无法打开设置窗口");
            alert.setContentText("发生错误：" + e.getMessage());
            alert.showAndWait();
        }
    }

    @FXML
    public void onButtonClick() {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("选择谱面文件");
        fileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("ADOFAI Files", "*.adofai"));
        File file = fileChooser.showOpenDialog(null);
        if (file != null) {
            try {
                adofai = new Adofai(file.getPath(), on);
                label.setText(file.getName());
            } catch (ParseException e) {
                e.printStackTrace();
            }
        }
    }
}
