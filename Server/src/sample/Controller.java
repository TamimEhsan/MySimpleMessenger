package sample;

import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;

import javax.swing.*;
import java.io.EOFException;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.ServerSocket;
import java.net.Socket;

public class Controller {

    @FXML
    private TextField userText;
    @FXML
    private TextArea chatWindow;


    private ServerSocket server;
    private Socket connection;
    private ObjectInputStream input;
    private ObjectOutputStream output;
    private Thread task;

    public void startRunning(){

        task = new Thread() {
            @Override
            public void run() {
                try{
                    server = new ServerSocket(6789,100);
                    try{
                        waitForConnection();
                        setupStreams();
                        whileChatting();
                    } catch (EOFException e){
                        showMessage("Server ended");
                    } finally {
                        closeCrap();
                    }
                } catch (IOException e){
                    e.printStackTrace();
                }
            }
        };
        task.start();


    }
    // wait for connection then display connection information
    public void waitForConnection() throws IOException{
        showMessage("Waiting for someone to connect...");
        connection = server.accept();
        showMessage("Now Connected to "+connection.getInetAddress().getHostAddress());
    }

    //get Stram to send and recieve data
    private void setupStreams() throws IOException{
        output = new ObjectOutputStream(connection.getOutputStream());
        output.flush();
        input = new ObjectInputStream(connection.getInputStream());
        showMessage("Streams are now Setup");
    }

    //  Chat covo
    private void whileChatting() throws IOException{
        String message = "You are now connected";
        sendMessage(message);
        ableToType(true);
        do{
            // have convo
            try{
                message = (String) input.readObject();
                showMessage("\n"+message);
            } catch (ClassNotFoundException | IOException e){
               // showMessage(" where's the message! can't show");
                break;
            }
        } while (!message.equals("CLIENT - END"));


    }

    // close streams and sockets
    private void closeCrap(){
        showMessage("\n Closing connections ... ... ...\n");
        ableToType(false);
        try{
            output.close();
            input.close();
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

    // send messages
    private void sendMessage(String message){
        try{
            output.writeObject("SERVER - "+message);
            output.flush();
            showMessage("\nSERVER - " + message);
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



}
