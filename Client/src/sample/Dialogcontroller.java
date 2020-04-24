package sample;

import javafx.fxml.FXML;
import javafx.scene.control.TextField;

import java.awt.*;

public class Dialogcontroller {
    @FXML
    private TextField usernamefield;

    public String getName(){
        String name = new String();
        name = usernamefield.getText();
        System.out.println(name);
        return name;
    }
}
