package machine.controller;

import javafx.scene.control.TextField;


public class PopUpController{
    public TextField message;

    // popup 메시지 띄우기
    public void init(String msg) {
        message.setText(msg);
    }

}
