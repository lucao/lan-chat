package br.gov.dataprev.lanchat;

import br.gov.dataprev.lanchat.service.ChatController;
import br.gov.dataprev.lanchat.ui.LoginView;
import javafx.application.Application;
import javafx.stage.Stage;

public class MainApp extends Application {

    private final ChatController controller = new ChatController();

    @Override
    public void start(Stage primaryStage) {
        LoginView.show(primaryStage, controller);
    }

    @Override
    public void stop() {
        controller.stop();
    }

    public static void main(String[] args) {
        launch(args);
    }
}
