package machine.controller;

import com.sun.org.apache.xpath.internal.operations.Bool;
import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.chart.AreaChart;
import javafx.scene.chart.XYChart;
import javafx.scene.control.*;
import javafx.scene.control.cell.TextFieldTreeTableCell;
import machine.model.Item;

import java.io.*;
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
    public TextField change10, change50, change100, change500, change1000;
    public AreaChart chart;
    TreeItem<Item> item1, item2, item3, item4, item5, root;

    // 수금 하기 // 거스름돈에서 빼내기
    public void getCoin(ActionEvent actionEvent) {
        int totalMoney = Integer.parseInt(money.getText());
        money.setText("");
        total = 0;

        int count_10 = 0; int count_50 = 0; int count_100 = 0; int count_500 = 0; int count_1000 = 0;
        String outputString = "";
        while(totalMoney >= 0) {
            if(totalMoney - 1000 >= 0 && !change_1000.isEmpty()) {
                count_1000++; totalMoney -= 1000; change_1000.pop();
            }
            else if(totalMoney - 500 >= 0 && !change_500.isEmpty()) {
                count_500++; totalMoney -= 500; change_500.pop();
            }
            else if(totalMoney - 100 >= 0 && !change_100.isEmpty()) {
                count_100++; totalMoney -= 100; change_100.pop();
            }
            else if(totalMoney - 50 >= 0 && !change_50.isEmpty()) {
                count_50++; totalMoney -= 50; change_50.pop();
            }
            else if(totalMoney - 10 >= 0 && !change_10.isEmpty()) {
                count_10++; totalMoney -= 10; change_10.pop();
            } else {
                break;
            }
        }
        change10.setText(String.valueOf(change_10.size()));change50.setText(String.valueOf(change_50.size()));change100.setText(String.valueOf(change_100.size()));change500.setText(String.valueOf(change_500.size()));change1000.setText(String.valueOf(change_1000.size()));
        send("setChange", change_10.size(), change_50.size(), change_100.size(), change_500.size(), change_1000.size());
        if(count_1000 > 0) outputString += "1000원 : " + count_1000 + "개 ";
        if(count_500 > 0) outputString += "500원 : " + count_500 + "개 ";
        if(count_100 > 0) outputString += "100원 : " + count_100 + "개 ";
        if(count_50 > 0) outputString += "50원 : " + count_50 + "개 ";
        if(count_10 > 0) outputString += "10원 : " + count_10 + "개 ";

        System.out.println("10원 : " + change_10.size() + " 50원 : " + change_50.size() + " 100원 : " + change_100.size() + " 500원 : " + change_500.size() + " 1000원 : " + change_1000.size());
        txtDisplay.setText(outputString.trim() + "를 수금했습니다.");
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        changeID.setText(LoginController.userId);
        changePW.setText(LoginController.userPw);
        change10.setText(String.valueOf(change_10.size()));
        change50.setText(String.valueOf(change_50.size()));
        change100.setText(String.valueOf(change_100.size()));
        change500.setText(String.valueOf(change_500.size()));
        change1000.setText(String.valueOf(change_1000.size()));
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
        soldUpdate();
        monthUpdate();
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

    public void soldUpdate() {
        String filePath = Objects.requireNonNull(SignUpController.class.getResource("")).getPath() + "../data/soldout.txt";
        File file = new File(filePath);
        String msg;
        try {
            FileReader fileReader = new FileReader(file);
            BufferedReader bufferedReader = new BufferedReader(fileReader);
            while((msg = bufferedReader.readLine()) != null) {
                txtDisplay.appendText(msg + "\n");
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void monthUpdate() {
        int month, price;
        String temp;

        int[] month_rate = {0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0};

        String filePath = Objects.requireNonNull(MainController.class.getResource("")).getPath() + "../data/beverage.txt";
        File file = new File(filePath);

        try {
            FileReader fileReader = new FileReader(file);
            BufferedReader bufferedReader = new BufferedReader(fileReader);

            while((temp = bufferedReader.readLine()) != null) {
                month = Integer.parseInt(temp.split(" ")[1].split("-")[1]);
                price = Integer.parseInt(temp.split(" ")[2]);
                month_rate[month - 1] += price;
            }
            XYChart.Series series = new XYChart.Series();
            series.setName("월별 매출");
            series.setData(FXCollections.observableArrayList(
                    new XYChart.Data("1월", month_rate[0]),
                    new XYChart.Data("2월", month_rate[1]),
                    new XYChart.Data("3월", month_rate[2]),
                    new XYChart.Data("4월", month_rate[3]),
                    new XYChart.Data("5월", month_rate[4]),
                    new XYChart.Data("6월", month_rate[5]),
                    new XYChart.Data("7월", month_rate[6]),
                    new XYChart.Data("8월", month_rate[7]),
                    new XYChart.Data("9월", month_rate[8]),
                    new XYChart.Data("10월", month_rate[9]),
                    new XYChart.Data("11월", month_rate[10]),
                    new XYChart.Data("12월", month_rate[11])
            ));
            chart.getData().add(series);
        } catch(Exception e) {
            e.printStackTrace();
        }

    }

    public void setChange10(ActionEvent actionEvent) {
        int changeCoin = Integer.parseInt(change10.getText());
        if(change_10.size() == changeCoin) return;
        else send("setChange", "10", changeCoin);
    }
    public void setChange50(ActionEvent actionEvent) {
        int changeCoin = Integer.parseInt(change50.getText());
        if(change_50.size() == changeCoin) return;
        else send("setChange", "50", changeCoin);
    }
    public void setChange100(ActionEvent actionEvent) {
        int changeCoin = Integer.parseInt(change100.getText());
        if(change_100.size() == changeCoin) return;
        else send("setChange", "100", changeCoin);
    }
    public void setChange500(ActionEvent actionEvent) {
        int changeCoin = Integer.parseInt(change500.getText());
        if(change_500.size() == changeCoin) return;
        else send("setChange", "500", changeCoin);
    }
    public void setChange1000(ActionEvent actionEvent) {
        int changeCoin = Integer.parseInt(change1000.getText());
        if(change_1000.size() == changeCoin) return;
        else send("setChange", "1000", changeCoin);
    }
}
