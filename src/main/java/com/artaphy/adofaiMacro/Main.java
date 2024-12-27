package com.artaphy.adofaiMacro;

import com.artaphy.adofaiMacro.lib.Adofai;
import com.artaphy.adofaiMacro.manager.KeyDetect;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import org.jnativehook.GlobalScreen;
import org.jnativehook.NativeHookException;

import java.util.logging.Level;
import java.util.logging.Logger;

public class Main extends Application {
    public static boolean isFirst = false;
    public static boolean isSecond = false;

    @Override
    public void start(Stage primaryStage) throws Exception {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/main.fxml"));
        Parent root = loader.load();
        primaryStage.setTitle("ADOFAI宏");
        primaryStage.setScene(new Scene(root));
        
        // 注册键盘监听器
        GlobalScreen.addNativeKeyListener(new KeyDetect());
        
        // 在窗口关闭时清理资源
        primaryStage.setOnCloseRequest(event -> {
            try {
                GlobalScreen.unregisterNativeHook();
                if (Adofai.thread != null && !Adofai.thread.isInterrupted()) {
                    Adofai.thread.interrupt();
                }
            } catch (NativeHookException e) {
                e.printStackTrace();
            }
        });
        
        primaryStage.show();
    }

    public static void main(String[] args) {
        try {
            // 禁用JNativeHook的日志输出
            Logger logger = Logger.getLogger(GlobalScreen.class.getPackage().getName());
            logger.setLevel(Level.OFF);
            logger.setUseParentHandlers(false);
            
            GlobalScreen.registerNativeHook();
        } catch (NativeHookException e) {
            e.printStackTrace();
        }
        launch(args);
    }
}


