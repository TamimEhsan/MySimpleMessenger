package sample;

import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Dialog;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.layout.BorderPane;

import javax.swing.*;
import java.io.EOFException;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.InetAddress;
import java.net.Socket;
import java.util.Optional;

public class Controller {

    @FXML
    private TextField userText;
    @FXML
    private TextArea chatWindow;
    @FXML
    private BorderPane mainpanel;

    private ObjectOutputStream output;
    private ObjectInputStream input;
    private String message = "";
    private String serverIP = "127.0.0.1";
    private Socket connection;
    private Thread task;
   // private static String Clientname = "CLIENT";


    public void startRunning(){

        task = new Thread() {
            @Override
            public void run() {
                try{
                    connectToServer();
                    setupStreams();
                    whileChatting();
                } catch (EOFException e){
                    showMessage("\n Client Terminated Connection");
                } catch (IOException e){
                    e.printStackTrace();
                } finally {
                    closeCrap();
                }
            }
        };
        task.start();


    }

    // connect to server
    private void connectToServer() throws IOException{
        showMessage("\n Attempting connection .. ..\n");
        connection = new Socket(InetAddress.getByName(serverIP),6789);
        showMessage("Connected to: "+connection.getInetAddress().getHostAddress());
    }

    // set up streams
    private void setupStreams(){

        try {
            output = new ObjectOutputStream(connection.getOutputStream());
            output.flush();
        } catch (IOException e) {
            e.printStackTrace();
        }
        try {
            input = new ObjectInputStream(connection.getInputStream());
        } catch (IOException e) {
            e.printStackTrace();
        }
        showMessage("Streams are now Setup");
    }

    // Chatting live now
    private void whileChatting() throws IOException{
        ableToType(true);
        do{
            try{
                message = (String) input.readObject();
                showMessage("\n" + message);
            } catch (ClassNotFoundException | IOException e){
                showMessage("\n Object not found");
            }
        } while( !message.equals("SERVER - END") );

    }

    //close streams and sockets
    private void closeCrap(){
        showMessage("\n Closing .. ..");
        ableToType(false);
        try{
            input.close();
            output.close();
            connection.close();
        } catch (IOException e){
            e.printStackTrace();
        }
    }
    public void buttonreact(){
        String text = userText.getText();
        userText.setText("");
        sendMessage(text);
    }
    // send messages to server
    private void sendMessage(String message){
        try{
            output.writeObject("CLIENT - " + message);
            output.flush();
            showMessage("\nCLIENT - "+message);
        } catch (IOException e){
            String string = chatWindow.getText();
            string = string+"\nERROR";
            chatWindow.setText(string);
            e.printStackTrace();
        }
    }

    //updates chatWindow
    private void showMessage(final String text){
        Platform.runLater(new Runnable() {
            @Override
            public void run() {
                String string = chatWindow.getText();
                       string = string+text;
                        chatWindow.setText(string);
            }
        });
    }
    @FXML
    public void endConvo(){
        sendMessage("END");
        task.interrupt();
    }
    // Let the user type
    private void ableToType(final boolean tof){

        Platform.runLater(new Runnable() {
            @Override
            public void run() {
                userText.setEditable(tof);
            }
        });
    }

//    @FXML
//    public void showAddName(){
//        Dialog<ButtonType> dialog = new Dialog<ButtonType>();
//        dialog.initOwner(mainpanel.getScene().getWindow());
//        dialog.setTitle("Change mofo's name");
//        FXMLLoader fxmlLoader = new FXMLLoader();
//        fxmlLoader.setLocation(getClass().getResource("Dialog.fxml"));
//        try{
//            dialog.getDialogPane().setContent(fxmlLoader.load());
//        }catch (IOException e){
//            e.printStackTrace();
//        }
//        dialog.getDialogPane().getButtonTypes().add(ButtonType.OK);
//        dialog.getDialogPane().getButtonTypes().add(ButtonType.CANCEL);
//
//        Optional<ButtonType> result =dialog.showAndWait();
//
//        if( result.isPresent() && result.get() == ButtonType.OK ){
//            Dialogcontroller dialogcontroller = new Dialogcontroller();
//            String name = new String();
//            name = dialogcontroller.getName();
//            Clientname = name;
//        }
//    }
}
