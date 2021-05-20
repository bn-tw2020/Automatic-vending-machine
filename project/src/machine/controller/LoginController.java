package machine.controller;

import Server.Main;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.util.Objects;

public class LoginController{

    public TextField id;
    public TextField pw;
    public Button login;
    public Button signup;
    public Button back;

    String userId, userPw;

    @FXML
    public void loginBtn(ActionEvent event) {
        String ID = id.getText();
        String PW = pw.getText();

        String filePath = Objects.requireNonNull(LoginController.class.getResource("")).getPath() + "../data/user.txt";
        File file = new File(filePath);

        try{
            FileReader fileReader = new FileReader(file);
            BufferedReader bufferedReader = new BufferedReader(fileReader);

            if(ID.isEmpty() || PW.isEmpty()) {
                MainController.pop_up("아이디 or 비밀번호를 입력하세요");
                return;
            }
            String temp;
            while((temp = bufferedReader.readLine()) != null) {
                if(temp.split(" ")[0].equals(ID) && temp.split(" ")[1].equals(PW)) {
                    userId = ID; userPw = PW;
                    MainController.exit_stage(login);
                    MainController.new_stage("adminUI", "admin Page");
                    return;
                }
            }
            MainController.pop_up("아이디 or 비밀번호를 틀립니다");


        } catch (Exception e) {
            e.printStackTrace();
        }
    }
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
