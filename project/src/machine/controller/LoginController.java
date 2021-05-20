package machine.controller;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;

public class LoginController{

    public TextField id_box;
    public TextField pw_box;
    public Button login;
    public Button signup;
    public Button back;

    @FXML
    public void loginBtn(ActionEvent event) { }
    @FXML
    public void backBtn(ActionEvent event){
        MainController.exit_stage(signup);
    }
    @FXML
    public void signupBtn(ActionEvent event) {
        MainController.exit_stage(signup);
        MainController.new_stage("signupUI", "Sign up");
    }
}
