package org.example.contraller;

import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.VBox;
import javafx.stage.FileChooser;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

public class ClientFormContraller {

    //public TextArea clientTextArea;
    public TextField lienttextfield;
    public VBox clientVbox;
    public ScrollPane scrollPane;

    private String message="";
    Socket socket;
    DataInputStream dataInputStream;
    DataOutputStream dataOutputStream;


    public void initialize(){
        clientVbox.heightProperty().addListener((observable, oldValue, newValue) ->
                scrollPane.setVvalue(1.0)); // bottom ekt ynn

        new Thread(() -> {
            try {
                appendMsg("\n server Side");
                socket = new Socket("localhost", 4000);

                dataInputStream = new DataInputStream(socket.getInputStream());
                dataOutputStream = new DataOutputStream(socket.getOutputStream());

                while (!message.equals("Disconnect")) {
                    message = dataInputStream.readUTF();

                    if (message.startsWith("IMG")) {
                        String imagePath = message.substring(3);
                        Platform.runLater(() -> {
                            displayOwnSide(imagePath);
                            appendMsg("Server: [Image Received]");
                        });
                    } else {
                        Platform.runLater(() -> appendMsg("Server: " + message));
                    }
                }
            } catch (IOException e) {
                Platform.runLater(() -> appendMsg("Error: Server not found or disconnected"));
                e.printStackTrace();
            }
        }).start();


    }

    private void appendMsg(String text) {
        Label label = new Label(text);
        clientVbox.getChildren().add(label);
    }

    public void sentonAction(ActionEvent actionEvent) throws IOException {
        String masssage = lienttextfield.getText();
        appendMsg("\n Client : " + masssage );
        dataOutputStream.writeUTF(masssage);
        dataOutputStream.flush();


        lienttextfield.clear();
    }

    public void ImageSentOnAction(ActionEvent actionEvent) {
        FileChooser fileChooser = new FileChooser();
        fileChooser.getExtensionFilters().add(
                new FileChooser.ExtensionFilter("Image Files", "*.png", "*.jpg", "*.jpeg")
        );

        File selectedFile = fileChooser.showOpenDialog(null);

        if (selectedFile != null) {
            try {
                 displayOwnSide(selectedFile.getPath());
                dataOutputStream.writeUTF("IMG" + selectedFile.getPath());
                dataOutputStream.flush();

                appendMsg("\nclient: Image Sent.....\n");
            } catch (IOException e) {
                Platform.runLater(() -> appendMsg("Error: Cant sent this image...."));
                e.printStackTrace();
            }
        }
    }

    private void displayOwnSide(String path) {
        try {
            File file = new File(path);
            Image image = new Image(file.toURI().toString());
            ImageView imageView = new ImageView(image);
            imageView.setFitWidth(150);
            imageView.setFitHeight(150);
            clientVbox.getChildren().add(imageView);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}

