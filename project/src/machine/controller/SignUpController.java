package machine.controller;

import Server.Main;
import javafx.event.ActionEvent;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.net.URL;
import java.util.Objects;
import java.util.ResourceBundle;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class SignUpController implements Initializable {
    public Button back;
    public Button signup;
    public Button confirm;
    public TextField id;
    public TextField pw;

    public void confirmBtn(ActionEvent actionEvent) throws Exception {
        String filePath = Objects.requireNonNull(SignUpController.class.getResource("")).getPath() + "../data/user.txt";
        File file = new File(filePath);
        String user;
        String ID;

        String inputID = id.getText();
        String inputPW = pw.getText();
        try {
            FileReader fileReader = new FileReader(file);
            BufferedReader bufferedReader = new BufferedReader(fileReader);

            if (inputID.isEmpty()) {
                MainController.pop_up("아이디를 입력하세요");
                return;
            }
            if (inputPW.isEmpty()) {
                MainController.pop_up("비밀번호를 입력하세요");
                return;
            }
            while ((user = bufferedReader.readLine()) != null) {
                ID = user.split(" ")[0];
                if (inputID.equals(ID)) {
                    MainController.pop_up("이미 존재하는 아이디입니다.");
                    id.setText("");
                    return;
                }
            }
            MainController.pop_up("사용 가능한 아이디입니다.");
            signup.setDisable(false);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
    }

    public void signupBtn(ActionEvent actionEvent) throws Exception {
        String ID = id.getText();
        String PW = pw.getText();

        if (check(PW)) {
            MainController.writeFile("user.txt", ID + " " + PW);
            MainController.exit_stage(signup);
            MainController.new_stage("loginUI", "login");
        } else {
            MainController.pop_up("사용 할 수 없는 비밀번호입니다.");
            pw.setText("");
            pw.setStyle("-fx-border-color: #000000;");
        }
    }

    private boolean check(String password) {
        Pattern pattern = Pattern.compile("([a-zA-Z0-9].*[!,@,#,$,%,^,&,*,?,_,~])|([!,@,#,$,%,^,&,*,?,_,~].*[a-zA-Z0-9])");
        Matcher matcher = pattern.matcher(password);

        return matcher.find() && (password.length() >= 8);
    }

    public void backBtn(ActionEvent actionEvent) {
        MainController.exit_stage(signup);
        MainController.new_stage("loginUI", "login");
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        signup.setDisable(true);
    }
}
