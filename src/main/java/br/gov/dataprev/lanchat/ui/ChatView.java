package br.gov.dataprev.lanchat.ui;

import br.gov.dataprev.lanchat.model.Message;
import br.gov.dataprev.lanchat.model.Peer;
import br.gov.dataprev.lanchat.service.ChatController;
import javafx.application.Platform;
import javafx.collections.MapChangeListener;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.input.KeyCode;
import javafx.scene.layout.*;
import javafx.scene.text.Text;
import javafx.stage.FileChooser;
import javafx.stage.Stage;

import java.awt.Desktop;
import java.io.File;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;

public class ChatView {

    private static final DateTimeFormatter TIME_FMT = DateTimeFormatter.ofPattern("HH:mm");
    private static final DateTimeFormatter FULL_FMT = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
    private static final String DARK_BG = "-fx-background-color: #1e1e2e;";
    private static final String PANEL_BG = "-fx-background-color: #181825;";

    public static void show(Stage stage, ChatController controller) {
        // ── Peer list ────────────────────────────────────────────────────────
        ListView<Peer> peerList = new ListView<>(controller.getPeers());
        peerList.setPrefWidth(180);
        peerList.setStyle(PANEL_BG + " -fx-border-color: #313244; -fx-border-width: 0 1 0 0;");
        peerList.setCellFactory(lv -> new ListCell<>() {
            @Override protected void updateItem(Peer p, boolean empty) {
                super.updateItem(p, empty);
                if (empty || p == null) { setGraphic(null); setStyle(""); return; }
                int unread = controller.getUnread(p.getNickname());
                Label name = new Label("● " + p.getNickname());
                name.setStyle("-fx-text-fill: #a6e3a1; -fx-font-size: 13px;");
                HBox cell = new HBox(name);
                cell.setAlignment(Pos.CENTER_LEFT);
                if (unread > 0) {
                    Label badge = new Label(String.valueOf(unread));
                    badge.setStyle("-fx-background-color: #f38ba8; -fx-text-fill: #1e1e2e; -fx-font-size: 10px; -fx-font-weight: bold; -fx-background-radius: 8; -fx-padding: 1 5;");
                    HBox.setHgrow(name, Priority.ALWAYS);
                    cell.getChildren().add(badge);
                }
                setGraphic(cell);
                setStyle("-fx-background-color: transparent;");
            }
        });

        // Refresh peer list cells when unread counts change
        controller.getUnreadCounts().addListener((MapChangeListener<String, Integer>) c ->
            Platform.runLater(peerList::refresh));

        // Also refresh when peers list changes (new peer appears)
        controller.getPeers().addListener((javafx.collections.ListChangeListener<Peer>) c ->
            Platform.runLater(peerList::refresh));

        Label noSelection = new Label("Select a peer to chat");
        noSelection.setStyle("-fx-text-fill: #6c7086; -fx-font-size: 14px;");
        StackPane chatArea = new StackPane(noSelection);
        chatArea.setStyle(DARK_BG);

        peerList.getSelectionModel().selectedItemProperty().addListener((obs, old, peer) -> {
            if (peer != null) {
                controller.clearUnread(peer.getNickname());
                chatArea.getChildren().setAll(buildConversation(stage, controller, peer));
            }
        });

        // Refresh chat panel when peer disconnects
        controller.getPeers().addListener((javafx.collections.ListChangeListener<Peer>) c -> {
            Peer selected = peerList.getSelectionModel().getSelectedItem();
            if (selected != null && !controller.getPeers().contains(selected))
                Platform.runLater(() -> chatArea.getChildren().setAll(noSelection));
        });

        Label myLabel = new Label("You: " + controller.getNickname());
        myLabel.setStyle("-fx-text-fill: #89b4fa; -fx-font-size: 12px; -fx-padding: 8 8 4 8;");

        VBox leftPanel = new VBox(myLabel, peerList);
        VBox.setVgrow(peerList, Priority.ALWAYS);
        leftPanel.setStyle(PANEL_BG);

        HBox root = new HBox(leftPanel, chatArea);
        HBox.setHgrow(chatArea, Priority.ALWAYS);

        stage.setScene(new Scene(root, 820, 560));
        stage.setTitle("LAN Chat – " + controller.getNickname());
        stage.setOnCloseRequest(e -> controller.stop());

        // ── Title flash when window is not focused and a new message arrives ─
        String baseTitle = "LAN Chat – " + controller.getNickname();
        AtomicBoolean flashing = new AtomicBoolean(false);
        ScheduledExecutorService flasher = Executors.newSingleThreadScheduledExecutor(r -> {
            Thread t = new Thread(r, "title-flasher");
            t.setDaemon(true);
            return t;
        });
        controller.getUnreadCounts().addListener((MapChangeListener<String, Integer>) change -> {
            if (change.wasAdded() && change.getValueAdded() > 0 && !stage.isFocused() && flashing.compareAndSet(false, true)) {
                @SuppressWarnings("unchecked")
                java.util.concurrent.ScheduledFuture<?>[] futureHolder = new java.util.concurrent.ScheduledFuture[1];
                futureHolder[0] = flasher.scheduleAtFixedRate(new Runnable() {
                    boolean toggle = false;
                    int ticks = 0;
                    @Override public void run() {
                        boolean anyUnread = controller.getUnreadCounts().values().stream().anyMatch(v -> v > 0);
                        if (stage.isFocused() || !anyUnread || ticks++ > 20) {
                            Platform.runLater(() -> stage.setTitle(baseTitle));
                            flashing.set(false);
                            futureHolder[0].cancel(false);
                            return;
                        }
                        toggle = !toggle;
                        String t = toggle ? "💬 New message! – " + controller.getNickname() : baseTitle;
                        Platform.runLater(() -> stage.setTitle(t));
                    }
                }, 0, 800, TimeUnit.MILLISECONDS);
            }
        });
        stage.focusedProperty().addListener((obs, wasFocused, isFocused) -> {
            if (isFocused) Platform.runLater(() -> stage.setTitle(baseTitle));
        });
        stage.setOnCloseRequest(e -> { flasher.shutdownNow(); controller.stop(); });

        stage.show();
    }

    private static VBox buildConversation(Stage stage, ChatController controller, Peer peer) {
        // ── Message list ─────────────────────────────────────────────────────
        ListView<Message> msgList = new ListView<>(controller.getConversation(peer.getNickname()));
        msgList.setStyle(DARK_BG + " -fx-border-color: transparent;");
        msgList.setCellFactory(lv -> new MessageCell(controller, peer));
        msgList.setSelectionModel(new NoSelectionModel<>());

        // Auto-scroll
        controller.getConversation(peer.getNickname()).addListener(
            (javafx.collections.ListChangeListener<Message>) c ->
                Platform.runLater(() -> msgList.scrollTo(msgList.getItems().size() - 1)));

        // ── Input bar ────────────────────────────────────────────────────────
        TextArea input = new TextArea();
        input.setPromptText("Type a message… (Enter to send, Shift+Enter for newline)");
        input.setPrefRowCount(2);
        input.setWrapText(true);
        input.setStyle("-fx-background-color: #313244; -fx-text-fill: #cdd6f4; -fx-prompt-text-fill: #6c7086; -fx-border-color: transparent; -fx-background-radius: 6;");
        input.setOnKeyPressed(e -> {
            if (e.getCode() == KeyCode.ENTER && !e.isShiftDown()) {
                e.consume();
                String text = input.getText().trim();
                if (!text.isEmpty()) {
                    controller.sendText(peer, text);
                    input.clear();
                }
            }
        });

        Button sendBtn = new Button("Send");
        sendBtn.setStyle("-fx-background-color: #89b4fa; -fx-text-fill: #1e1e2e; -fx-font-weight: bold; -fx-background-radius: 6; -fx-cursor: hand;");
        sendBtn.setOnAction(e -> {
            String text = input.getText().trim();
            if (!text.isEmpty()) { controller.sendText(peer, text); input.clear(); }
        });

        Button fileBtn = new Button("📎");
        fileBtn.setStyle("-fx-background-color: #45475a; -fx-text-fill: #cdd6f4; -fx-background-radius: 6; -fx-cursor: hand;");
        fileBtn.setOnAction(e -> {
            FileChooser fc = new FileChooser();
            fc.setTitle("Select file to send");
            File file = fc.showOpenDialog(stage);
            if (file != null) controller.sendFile(peer, file);
        });

        HBox inputBar = new HBox(8, fileBtn, input, sendBtn);
        HBox.setHgrow(input, Priority.ALWAYS);
        inputBar.setAlignment(Pos.CENTER);
        inputBar.setPadding(new Insets(8));
        inputBar.setStyle("-fx-background-color: #181825; -fx-border-color: #313244; -fx-border-width: 1 0 0 0;");

        Label peerLabel = new Label("Chat with " + peer.getNickname());
        peerLabel.setStyle("-fx-text-fill: #cdd6f4; -fx-font-size: 14px; -fx-font-weight: bold; -fx-padding: 10 12;");
        HBox header = new HBox(peerLabel);
        header.setStyle("-fx-background-color: #181825; -fx-border-color: #313244; -fx-border-width: 0 0 1 0;");

        VBox conv = new VBox(header, msgList, inputBar);
        VBox.setVgrow(msgList, Priority.ALWAYS);
        return conv;
    }

    // ── Message bubble cell ───────────────────────────────────────────────────

    static class MessageCell extends ListCell<Message> {
        private final ChatController controller;
        private final Peer peer;

        MessageCell(ChatController controller, Peer peer) {
            this.controller = controller;
            this.peer = peer;
        }

        @Override protected void updateItem(Message msg, boolean empty) {
            super.updateItem(msg, empty);
            if (empty || msg == null) { setGraphic(null); setStyle("-fx-background-color: transparent;"); return; }

            boolean out = msg.isOutgoing();
            String timeStr = msg.getSentAt() != null
                ? msg.getSentAt().atZone(ZoneId.systemDefault()).format(TIME_FMT) : "";
            String statusIcon = switch (msg.getStatus() == null ? Message.Status.SENT : msg.getStatus()) {
                case SENT -> "✓";
                case DELIVERED -> "✓✓";
                case READ -> "✓✓";
            };
            boolean isRead = msg.getStatus() == Message.Status.READ;

            // Bubble content
            String bubbleText = msg.getType() == Message.Type.FILE
                ? "📎 " + msg.getContent() + " (" + formatSize(msg.getFileSize()) + ")"
                : msg.getContent();

            Text textNode = new Text(bubbleText);
            textNode.setStyle("-fx-fill: #cdd6f4;");
            textNode.setWrappingWidth(320);

            Label meta = new Label(timeStr + (out ? " " + statusIcon : ""));
            meta.setStyle("-fx-text-fill: " + (isRead ? "#89b4fa" : "#6c7086") + "; -fx-font-size: 10px;");

            VBox bubble = new VBox(2, textNode, meta);
            bubble.setPadding(new Insets(8, 12, 8, 12));
            bubble.setMaxWidth(360);
            bubble.setStyle("-fx-background-color: " + (out ? "#313244" : "#1e3a5f") +
                "; -fx-background-radius: 12; -fx-cursor: hand;");

            // Click → show detail popup
            bubble.setOnMouseClicked(e -> showDetail(msg));

            // Click file to open
            if (msg.getType() == Message.Type.FILE && !msg.isOutgoing() && msg.getFilePath() != null) {
                Label openLink = new Label("Open file");
                openLink.setStyle("-fx-text-fill: #89b4fa; -fx-font-size: 10px; -fx-cursor: hand;");
                openLink.setOnMouseClicked(e -> {
                    e.consume();
                    try { Desktop.getDesktop().open(new File(msg.getFilePath())); } catch (Exception ex) { ex.printStackTrace(); }
                });
                bubble.getChildren().add(openLink);
            }

            HBox row = new HBox(bubble);
            row.setPadding(new Insets(3, 12, 3, 12));
            row.setAlignment(out ? Pos.CENTER_RIGHT : Pos.CENTER_LEFT);

            // Mark as read when rendered for incoming
            if (!out && msg.getStatus() != Message.Status.READ) {
                controller.markRead(peer, msg);
            }

            setGraphic(row);
            setStyle("-fx-background-color: transparent;");
        }

        private void showDetail(Message msg) {
            String sent = fmt(msg.getSentAt());
            String delivered = fmt(msg.getDeliveredAt());
            String read = fmt(msg.getReadAt());
            String status = msg.getStatus() != null ? msg.getStatus().name() : "SENT";

            Alert alert = new Alert(Alert.AlertType.INFORMATION);
            alert.setTitle("Message Details");
            alert.setHeaderText(null);
            alert.setContentText(
                "Status:     " + status + "\n" +
                "Sent:       " + sent + "\n" +
                "Delivered:  " + delivered + "\n" +
                "Read:       " + read
            );
            alert.showAndWait();
        }

        private String fmt(java.time.Instant instant) {
            if (instant == null) return "—";
            return instant.atZone(ZoneId.systemDefault()).format(FULL_FMT);
        }

        private String formatSize(long bytes) {
            if (bytes < 1024) return bytes + " B";
            if (bytes < 1024 * 1024) return String.format("%.1f KB", bytes / 1024.0);
            return String.format("%.1f MB", bytes / (1024.0 * 1024));
        }
    }

    // Prevents ListView from highlighting selected items
    static class NoSelectionModel<T> extends MultipleSelectionModel<T> {
        @Override public javafx.collections.ObservableList<Integer> getSelectedIndices() { return javafx.collections.FXCollections.emptyObservableList(); }
        @Override public javafx.collections.ObservableList<T> getSelectedItems() { return javafx.collections.FXCollections.emptyObservableList(); }
        @Override public void selectIndices(int i, int... is) {}
        @Override public void selectAll() {}
        @Override public void selectFirst() {}
        @Override public void selectLast() {}
        @Override public void clearAndSelect(int i) {}
        @Override public void select(int i) {}
        @Override public void select(T t) {}
        @Override public void clearSelection(int i) {}
        @Override public void clearSelection() {}
        @Override public boolean isSelected(int i) { return false; }
        @Override public boolean isEmpty() { return true; }
        @Override public void selectPrevious() {}
        @Override public void selectNext() {}
    }
}
