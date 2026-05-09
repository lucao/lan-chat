package br.gov.dataprev.lanchat.ui;

import br.gov.dataprev.lanchat.service.ChatController;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

public class LoginView {

    public static void show(Stage stage, ChatController controller) {
        Label title = new Label("LAN Chat");
        title.setStyle("-fx-font-size: 24px; -fx-font-weight: bold;");

        TextField nickField = new TextField();
        nickField.setPromptText("Enter your nickname");
        nickField.setMaxWidth(260);

        String saved = controller.getStorage().loadNickname();
        if (saved != null) nickField.setText(saved);

        Button btn = new Button("Join");
        btn.setDefaultButton(true);
        btn.setOnAction(e -> {
            String nick = nickField.getText().trim();
            if (nick.isEmpty()) return;
            try {
                controller.start(nick);
                ChatView.show(stage, controller);
            } catch (Exception ex) {
                new Alert(Alert.AlertType.ERROR, "Failed to start: " + ex.getMessage()).showAndWait();
            }
        });

        VBox root = new VBox(16, title, nickField, btn);
        root.setAlignment(Pos.CENTER);
        root.setPadding(new Insets(40));
        root.setStyle("-fx-background-color: #1e1e2e;");
        title.setStyle("-fx-font-size: 24px; -fx-font-weight: bold; -fx-text-fill: #cdd6f4;");
        nickField.setStyle("-fx-background-color: #313244; -fx-text-fill: #cdd6f4; -fx-prompt-text-fill: #6c7086; -fx-border-color: #45475a; -fx-border-radius: 4; -fx-background-radius: 4; -fx-padding: 8;");
        btn.setStyle("-fx-background-color: #89b4fa; -fx-text-fill: #1e1e2e; -fx-font-weight: bold; -fx-padding: 8 24; -fx-background-radius: 4; -fx-cursor: hand;");

        stage.setScene(new Scene(root, 400, 280));
        stage.setTitle("LAN Chat – Login");
        stage.show();
    }
}
