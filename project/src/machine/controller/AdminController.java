package machine.controller;

import com.sun.org.apache.xpath.internal.operations.Bool;
import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.control.cell.TextFieldTreeTableCell;
import machine.model.Item;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.net.URL;
import java.util.Objects;
import java.util.ResourceBundle;

import static machine.controller.MainController.*;

public class AdminController implements Initializable {
    @FXML
    public TreeTableView<Item> tableView;
    @FXML
    public TreeTableColumn<Item, String> name;
    @FXML
    public TreeTableColumn<Item, String> price;
    @FXML
    public TreeTableColumn<Item, String> stock;
    @FXML
    public TreeTableColumn<Item, String> current;
    public Button FIX;
    public TextField money;
    public TextArea txtDisplay;
    public Button back;
    static String item1_stock;
    static String item2_stock;
    static String item3_stock;
    static String item4_stock;
    static String item5_stock;
    public TextField changeID;
    public TextField changePW;
    TreeItem<Item> item1, item2, item3, item4, item5, root;

    // 수금 하기
    public void getCoin(ActionEvent actionEvent) {
        int totalMoney = Integer.parseInt(money.getText());
        money.setText("");
        total = 0;

        System.out.println("10원 : " + change_10.size() + "50원 : " + change_50.size() + "100원 : " + change_100.size() + "500원 : " + change_500.size() + "1000원 : " + change_1000.size());
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        changeID.setText(LoginController.userId);
        changePW.setText(LoginController.userPw);
        money.setText(String.valueOf(total));

        item1 = new TreeItem<Item>(new Item(item1_name, String.valueOf(item1_price), String.valueOf(MainController.water_stock.size()), String.valueOf(item1_curr)));
        item2 = new TreeItem<Item>(new Item(item2_name, String.valueOf(item2_price), String.valueOf(MainController.coffee_stock.size()), String.valueOf(item2_curr)));
        item3 = new TreeItem<Item>(new Item(item3_name, String.valueOf(item3_price), String.valueOf(MainController.sports_drink_stock.size()), String.valueOf(item3_curr)));
        item4 = new TreeItem<Item>(new Item(item4_name, String.valueOf(item4_price), String.valueOf(MainController.premium_coffee_stock.size()), String.valueOf(item4_curr)));
        item5 = new TreeItem<Item>(new Item(item5_name, String.valueOf(item5_price), String.valueOf(MainController.soda_stock.size()), String.valueOf(item5_curr)));
        root = new TreeItem<Item>(new Item("Name", "0", "0", "0"));
        root.getChildren().setAll(item1, item2, item3, item4, item5);
        item1_stock = String.valueOf(water_stock.size()); item2_stock = String.valueOf(coffee_stock.size());
        item3_stock = String.valueOf(sports_drink_stock.size()); item4_stock = String.valueOf(premium_coffee_stock.size());
        item5_stock = String.valueOf(soda_stock.size());
        name.setCellValueFactory(param -> param.getValue().getValue().namePropertyProperty());
        price.setCellValueFactory(param -> param.getValue().getValue().pricePropertyProperty());
        stock.setCellValueFactory(param -> param.getValue().getValue().stockPropertyProperty());
        current.setCellValueFactory(param -> param.getValue().getValue().currentPropertyProperty());

        name.setCellFactory(param -> new TextFieldTreeTableCell<>());
        price.setCellFactory(param -> new TextFieldTreeTableCell<>());
        stock.setCellFactory(param -> new TextFieldTreeTableCell<>());
        current.setCellFactory(param -> new TextFieldTreeTableCell<>());

        name.setCellFactory(TextFieldTreeTableCell.forTreeTableColumn());
        price.setCellFactory(TextFieldTreeTableCell.forTreeTableColumn());
        stock.setCellFactory(TextFieldTreeTableCell.forTreeTableColumn());
        current.setCellFactory(TextFieldTreeTableCell.forTreeTableColumn());

        name.setOnEditCommit(event -> {
            TreeItem<Item> currentEditingItem = tableView.getTreeItem(event.getTreeTablePosition().getRow());
            currentEditingItem.getValue().setNameProperty(event.getNewValue());
            if(event.getTreeTablePosition().getRow() == 0) item1_name = event.getNewValue();
            if(event.getTreeTablePosition().getRow() == 1) item2_name = event.getNewValue();
            if(event.getTreeTablePosition().getRow() == 2) item3_name = event.getNewValue();
            if(event.getTreeTablePosition().getRow() == 3) item4_name = event.getNewValue();
            if(event.getTreeTablePosition().getRow() == 4) item5_name = event.getNewValue();
        });
        price.setOnEditCommit(event -> {
            TreeItem<Item> currentEditingItem = tableView.getTreeItem(event.getTreeTablePosition().getRow());
            currentEditingItem.getValue().setPriceProperty(event.getNewValue());
            if(event.getTreeTablePosition().getRow() == 0) item1_price = Integer.parseInt(event.getNewValue());
            if(event.getTreeTablePosition().getRow() == 1) item2_price = Integer.parseInt(event.getNewValue());
            if(event.getTreeTablePosition().getRow() == 2) item3_price = Integer.parseInt(event.getNewValue());
            if(event.getTreeTablePosition().getRow() == 3) item4_price = Integer.parseInt(event.getNewValue());
            if(event.getTreeTablePosition().getRow() == 4) item5_price = Integer.parseInt(event.getNewValue());
        });
        stock.setOnEditCommit(event -> {
            TreeItem<Item> currentEditingItem = tableView.getTreeItem(event.getTreeTablePosition().getRow());
            currentEditingItem.getValue().setStockProperty(event.getNewValue());
            if(event.getTreeTablePosition().getRow() == 0) item1_stock = event.getNewValue();
            if(event.getTreeTablePosition().getRow() == 1) item2_stock = event.getNewValue();
            if(event.getTreeTablePosition().getRow() == 2) item3_stock = event.getNewValue();
            if(event.getTreeTablePosition().getRow() == 3) item4_stock = event.getNewValue();
            if(event.getTreeTablePosition().getRow() == 4) item5_stock = event.getNewValue();
        });
        current.setOnEditCommit(event -> {
            TreeItem<Item> currentEditingItem = tableView.getTreeItem(event.getTreeTablePosition().getRow());
            currentEditingItem.getValue().setCurrentProperty(event.getNewValue());
            if(event.getTreeTablePosition().getRow() == 0) item1_curr = Integer.parseInt(event.getNewValue());
            if(event.getTreeTablePosition().getRow() == 1) item2_curr = Integer.parseInt(event.getNewValue());
            if(event.getTreeTablePosition().getRow() == 2) item3_curr = Integer.parseInt(event.getNewValue());
            if(event.getTreeTablePosition().getRow() == 3) item4_curr = Integer.parseInt(event.getNewValue());
            if(event.getTreeTablePosition().getRow() == 4) item5_curr = Integer.parseInt(event.getNewValue());
        });
        tableView.setEditable(true);
        tableView.setRoot(root);
        tableView.setShowRoot(false);
    }

    public void goBack(ActionEvent actionEvent) {
        MainController.exit_stage(back);
    }

    public void setData(ActionEvent actionEvent) {
        // 데이터 보내기 / 아이템1, 아이템2, 아이템3, 아이템4, 아이템5 보내기
        MainController.send("change");
    }

    public void setAdmin(ActionEvent actionEvent) throws Exception {
        String filePath = Objects.requireNonNull(LoginController.class.getResource("")).getPath() + "../data/user.txt";
        File file = new File(filePath);
        String updatePW = changePW.getText();
        boolean flag = SignUpController.check(updatePW);
        if(!flag) MainController.pop_up("형식에 맞지 않습니다.");

        if (flag) {
            MainController.writeFile("user.txt", changeID.getText() + " " + changePW.getText());
        } else {
            MainController.pop_up("형식에 맞지 않습니다.");
            changePW.setText("");
            changePW.setStyle("-fx-border-color: #000000;");
        }
    }
}
