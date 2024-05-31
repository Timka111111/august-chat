package kz.timka.client;


import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;
import java.net.URL;
import java.util.ResourceBundle;

public class Controller implements Initializable{

    @FXML
    private TextField msgField;

    @FXML
    private TextArea msgArea;
    private Socket socket;
    private DataInputStream in;
    private DataOutputStream out;


    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        try {
            socket = new Socket("localhost", 8189);
            in = new DataInputStream(socket.getInputStream());
            out = new DataOutputStream(socket.getOutputStream());
            new Thread(() -> {
                try {
                    while (true) {
                        String msg = in.readUTF();
                        msgArea.appendText(msg + "\n");
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                    closeConnection();
                }
            }).start();
        } catch (IOException e) {
            e.printStackTrace();
            showErrorAlert("Unable to connect to server");
        }
    }

    public void sendMsg() {
        try {
            out.writeUTF(msgField.getText());
            msgField.clear();
        } catch (IOException e) {
            Alert alert = new Alert(Alert.AlertType.ERROR, "Невозможно отправить сообщение");
            alert.showAndWait();
        }
    }

    private void showErrorAlert(String message) {
        Alert alert = new Alert(Alert.AlertType.ERROR, message);
        alert.showAndWait();
    }

    private void closeConnection() {
        try {
            if (socket != null && !socket.isClosed()) {
                socket.close();
            }
            if (in != null) {
                in.close();
            }
            if (out != null) {
                out.close();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
