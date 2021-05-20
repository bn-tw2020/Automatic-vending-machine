package machine.controller;

import javafx.scene.control.TextField;


public class PopUpController{
    public TextField message;

    public void init(String msg) {
        message.setText(msg);
    }

}
