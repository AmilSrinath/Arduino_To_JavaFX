package lk.ijse;

import com.fazecast.jSerialComm.SerialPort;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.stage.Stage;

import java.net.URL;
import java.util.ResourceBundle;
import java.util.Scanner;

public class FormController extends Application implements Initializable {

    public ComboBox<String> jComboBox1;
    public Button jButton1;
    public TextField jTextField1;

    public SerialPort serialPort1;

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        ObservableList<String> Allport = FXCollections.observableArrayList();
        SerialPort [] allAvaiableComPorts = SerialPort.getCommPorts();
        for(SerialPort port:allAvaiableComPorts){
            Allport.add(port.getSystemPortName());
        }
        jComboBox1.setItems(Allport);
    }

    @Override
    public void start(Stage stage) throws Exception {
        Parent root = FXMLLoader.load(getClass().getResource("/view/Form.fxml"));
        Scene scene = new Scene(root);
        stage.setScene(scene);
        stage.show();
    }

    public void jButton1OnAction(ActionEvent actionEvent) {
        if (jComboBox1.getSelectionModel().getSelectedItem() != null){
            if (jButton1.getText().equals("CONNECT")) {
                try {
                    SerialPort []allAvailableComPorts = SerialPort.getCommPorts();
                    serialPort1 = allAvailableComPorts[jComboBox1.getSelectionModel().getSelectedIndex()];

                    serialPort1.openPort();
                    if(serialPort1.openPort()){
                        jButton1.setText("DISCONNECT");
                    }
                } catch (ArrayIndexOutOfBoundsException e) {
                    e.printStackTrace();
                }
            }else if (jButton1.getText().equals("DISCONNECT")) {
                serialPort1.closePort();
            }

            try {
                serialPort1.setComPortTimeouts(SerialPort.TIMEOUT_SCANNER, 0, 0);
                Thread threadCP = new Thread(){
                    @Override
                    public void run(){
                        Scanner scanner1 = new Scanner(serialPort1.getInputStream());
                        while (scanner1.hasNextLine()) {
                            String line = scanner1.next();

                            Platform.runLater(() -> {
                                jTextField1.setText(line);
                            });
                        }
                        scanner1.close();
                    }
                };
                threadCP.start();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }else {
            new Alert(Alert.AlertType.ERROR,"Please Select Item!!!").show();
        }
    }

    public static void main(String[] args) {
        launch(args);
    }
}
