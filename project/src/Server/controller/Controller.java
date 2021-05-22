package Server.controller;

import Server.model.Item;
import com.sun.org.apache.xpath.internal.operations.Bool;
import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.control.cell.TextFieldTreeTableCell;

import java.io.*;
import java.net.InetSocketAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.URL;
import java.util.Iterator;
import java.util.List;
import java.util.ResourceBundle;
import java.util.Vector;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class Controller implements Initializable {
    ExecutorService executorService;
    ServerSocket serverSocket;
    List<Client> connections = new Vector<Client>(); // 스레드에 안전함
    @FXML TreeTableView<Item> tableView, tableView1;
    @FXML TreeTableColumn<Item, String> name, name1;
    @FXML TreeTableColumn<Item, String> price, price1;
    @FXML TreeTableColumn<Item, String> stock, stock1;
    @FXML TreeTableColumn<Item, String> current, current1;
    @FXML public TextArea txtDisplay;
    @FXML TextField change10B, change50B, change100B, change500B, change1000B;
    @FXML TextField change10A, change50A, change100A, change500A, change1000A;
    Client client;
    @Override
    public void initialize(URL location, ResourceBundle resources) {
        // 서버 시작 코드 (Executor Service 생성, ServerSocket 생성 및 포트 바인딩, 연결 수락)

        executorService = Executors.newFixedThreadPool(300);
        try {
            serverSocket = new ServerSocket();
            serverSocket.bind(new InetSocketAddress("localhost", 7777));
        } catch (Exception e) {
            if (!serverSocket.isClosed()) {
                stopServer();
            }
            return;
        }

        Runnable runnable = new Runnable() { // 수락 작업을 생성
            @Override
            public void run() {
                Platform.runLater(() -> {
                    displayText("[서버 시작]");
                });
                while (true) {
                    try {
                        while(connections.size() >= 2) {}
                        Socket socket = serverSocket.accept(); // 연결 수락
                        String message = "[연결 수락 : " + socket.getRemoteSocketAddress() + ": " + Thread.currentThread().getName() + "]";
                        Platform.runLater(() -> displayText(message));
                        client = new Client(socket);
                        connections.add(client);
                        Platform.runLater(() -> displayText("[연결 개수 : " + connections.size() + " ]"));
                    } catch (Exception e) {
                        if (!serverSocket.isClosed()) {
                            stopServer();
                        }
                        break;
                    }
                }
            }
        };
        executorService.submit(runnable); // 스레드 풀에서 처리
    }

    public void fix(ActionEvent actionEvent) {
        if(client.vending.equals("A")) client.send("change_item");
        else if(client.vending.equals("B")) client.send("change_item");
        else{ return;}
    }

    void stopServer() {
        // 서버 종료 코드
        try {
            Iterator<Client> iterator = connections.iterator();
            while (iterator.hasNext()) { // 모든 socket 닫기
                Client client = iterator.next();
                client.socket.close();
                iterator.remove();
            }
            if (serverSocket != null && !serverSocket.isClosed()) { // server socket 닫기
                serverSocket.close();
            }
            if (executorService != null && !executorService.isShutdown()) { // ExecutorService 종료
                executorService.shutdown();
            }
        } catch (Exception ignored) {
        }
    }

    public void setChange10A(ActionEvent actionEvent) { }
    public void setChange10B(ActionEvent actionEvent) { }
    public void setChange50A(ActionEvent actionEvent) { }
    public void setChange50B(ActionEvent actionEvent) { }
    public void setChange100A(ActionEvent actionEvent) { }
    public void setChange100B(ActionEvent actionEvent) { }
    public void setChange500A(ActionEvent actionEvent) { }
    public void setChange500B(ActionEvent actionEvent) { }
    public void setChange1000A(ActionEvent actionEvent) { }
    public void setChange1000B(ActionEvent actionEvent) { }

    class Client {
        // 데이터 통신 코드
        Socket socket;
        int total_coins = 0; // 총 수익금
        public String check = null;
        TreeItem<Item> item1, item2, item3, item4, item5, root;
        Boolean flag = false;
        String vending = "";
        Client(Socket socket) {
            this.socket = socket;
            item1 = new TreeItem<>(new Item("물", "450", "3", "0"));
            item2 = new TreeItem<>(new Item("커피", "500", "3", "0"));
            item3 = new TreeItem<>(new Item("이온음료", "550", "3", "0"));
            item4 = new TreeItem<>(new Item("프리미엄 커피", "700", "3", "0"));
            item5 = new TreeItem<>(new Item("탄산 음료", "750", "3", "0"));
            root = new TreeItem<>(new Item("Name", "0", "0", "0"));
            root.getChildren().setAll(item1, item2, item3, item4, item5);
            Platform.runLater(()-> {
                if(tableView.getRoot() == null) {
                    flag = true;
                    vending = "A";
                    change10A.setText("5"); change50A.setText("5"); change100A.setText("5"); change500A.setText("5"); change1000A.setText("5");
                    name.setCellValueFactory(param -> param.getValue().getValue().namePropertyProperty());
                    price.setCellValueFactory(param -> param.getValue().getValue().pricePropertyProperty());
                    stock.setCellValueFactory(param -> param.getValue().getValue().stockPropertyProperty());
                    current.setCellValueFactory(param -> param.getValue().getValue().currentPropertyProperty());

                    name.setCellFactory(param -> new TextFieldTreeTableCell<>()); price.setCellFactory(param -> new TextFieldTreeTableCell<>());
                    stock.setCellFactory(param -> new TextFieldTreeTableCell<>()); current.setCellFactory(param -> new TextFieldTreeTableCell<>());
                    name.setCellFactory(TextFieldTreeTableCell.forTreeTableColumn()); price.setCellFactory(TextFieldTreeTableCell.forTreeTableColumn());
                    stock.setCellFactory(TextFieldTreeTableCell.forTreeTableColumn()); current.setCellFactory(TextFieldTreeTableCell.forTreeTableColumn());

                    name.setOnEditCommit(event -> {
                        TreeItem<Item> currentEditingItem = tableView.getTreeItem(event.getTreeTablePosition().getRow());
                        currentEditingItem.getValue().setNameProperty(event.getNewValue());
                    });
                    price.setOnEditCommit(event -> {
                        TreeItem<Item> currentEditingItem = tableView.getTreeItem(event.getTreeTablePosition().getRow());
                        currentEditingItem.getValue().setPriceProperty(event.getNewValue());
                    });
                    stock.setOnEditCommit(event -> {
                        TreeItem<Item> currentEdtingItem = tableView.getTreeItem(event.getTreeTablePosition().getRow());
                        currentEdtingItem.getValue().setStockProperty(event.getNewValue());
                    });
                    current.setOnEditCommit(event -> {
                        TreeItem<Item> currentEdtingItem = tableView.getTreeItem(event.getTreeTablePosition().getRow());
                        currentEdtingItem.getValue().setCurrentProperty(event.getNewValue());
                    });
                    tableView.setEditable(true);
                    tableView.setRoot(root);
                    tableView.setShowRoot(false);
                }
                else if(tableView1.getRoot() == null) {
                    vending = "B";
                    change10B.setText("5"); change50B.setText("5"); change100B.setText("5"); change500B.setText("5"); change1000B.setText("5");
                    name1.setCellValueFactory(param -> param.getValue().getValue().namePropertyProperty());
                    price1.setCellValueFactory(param -> param.getValue().getValue().pricePropertyProperty());
                    stock1.setCellValueFactory(param -> param.getValue().getValue().stockPropertyProperty());
                    current1.setCellValueFactory(param -> param.getValue().getValue().currentPropertyProperty());

                    name1.setCellFactory(param -> new TextFieldTreeTableCell<>()); price1.setCellFactory(param -> new TextFieldTreeTableCell<>());
                    stock1.setCellFactory(param -> new TextFieldTreeTableCell<>()); current1.setCellFactory(param -> new TextFieldTreeTableCell<>());
                    name1.setCellFactory(TextFieldTreeTableCell.forTreeTableColumn()); price1.setCellFactory(TextFieldTreeTableCell.forTreeTableColumn());
                    stock1.setCellFactory(TextFieldTreeTableCell.forTreeTableColumn()); current1.setCellFactory(TextFieldTreeTableCell.forTreeTableColumn());

                    name1.setOnEditCommit(event -> {
                        TreeItem<Item> currentEditingItem = tableView1.getTreeItem(event.getTreeTablePosition().getRow());
                        currentEditingItem.getValue().setNameProperty(event.getNewValue());
                    });
                    price1.setOnEditCommit(event -> {
                        TreeItem<Item> currentEditingItem = tableView1.getTreeItem(event.getTreeTablePosition().getRow());
                        currentEditingItem.getValue().setPriceProperty(event.getNewValue());
                    });
                    stock1.setOnEditCommit(event -> {
                        TreeItem<Item> currentEdtingItem = tableView1.getTreeItem(event.getTreeTablePosition().getRow());
                        currentEdtingItem.getValue().setStockProperty(event.getNewValue());
                    });
                    current1.setOnEditCommit(event -> {
                        TreeItem<Item> currentEdtingItem = tableView1.getTreeItem(event.getTreeTablePosition().getRow());
                        currentEdtingItem.getValue().setCurrentProperty(event.getNewValue());
                    });
                    tableView1.setEditable(true);
                    tableView1.setRoot(root);
                    tableView1.setShowRoot(false);
                }
            });
            send("init");
            receive();
        }

        void receive() {
            // 데이터 받기 코드
            Runnable runnable = new Runnable() { // 받기 작업 생성
                @Override
                public void run() {
                    try {
                        while (true) {
                            InputStream inputStream = socket.getInputStream();
                            DataInputStream dataInputStream = new DataInputStream(inputStream);
                            String meta = null;
                            while ((meta = dataInputStream.readUTF()) != null) {
                                if (meta.equals("close")) {
                                    Platform.runLater(() -> { displayText("연결이 끊어졌습니다."); });
                                    connections.remove(Client.this);
                                    socket.close();
                                    Platform.runLater(() -> {
                                        if(flag){
                                            tableView.setRoot(null);
                                            change10A.setText("0"); change50A.setText("0"); change100A.setText("0"); change500A.setText("0"); change1000A.setText("0");
                                        } else {
                                            tableView1.setRoot(null);
                                            change10B.setText("0"); change50B.setText("0"); change100B.setText("0"); change500B.setText("0"); change1000B.setText("0");
                                        }
                                        displayText("[연결 개수 : " + connections.size() + " ]");
                                    });
                                }
                                else if((meta.equals("data"))) {
                                    check = dataInputStream.readUTF();
                                    // 구매 처리하기
                                    if(item1.getValue().getNameProperty().equals(check)) { // 물 구매
                                        if(vending.equals("A")) {
                                            Platform.runLater(()-> {
                                                String name = tableView.getTreeItem(0).getValue().getNameProperty();
                                                int price = Integer.parseInt(tableView.getTreeItem(0).getValue().getPriceProperty());
                                                int current = Integer.parseInt(tableView.getTreeItem(0).getValue().getCurrentProperty());
                                                int stock = Integer.parseInt(tableView.getTreeItem(0).getValue().getStockProperty());
                                                total_coins += price;
                                                tableView.getTreeItem(0).getValue().setStockProperty(String.valueOf(stock - 1));
                                                tableView.getTreeItem(0).getValue().setCurrentProperty(String.valueOf(current + 1));
                                                item1.getValue().setStockProperty(String.valueOf(stock - 1));
                                                item1.getValue().setCurrentProperty(String.valueOf(current + 1));
                                            });
                                        }
                                        else if(vending.equals("B")) {
                                            Platform.runLater(()-> {
                                                String name = tableView1.getTreeItem(0).getValue().getNameProperty();
                                                int price = Integer.parseInt(tableView1.getTreeItem(0).getValue().getPriceProperty());
                                                int current = Integer.parseInt(tableView1.getTreeItem(0).getValue().getCurrentProperty());
                                                int stock = Integer.parseInt(tableView1.getTreeItem(0).getValue().getStockProperty());
                                                total_coins += price;
                                                tableView1.getTreeItem(0).getValue().setStockProperty(String.valueOf(stock - 1));
                                                tableView1.getTreeItem(0).getValue().setCurrentProperty(String.valueOf(current + 1));
                                                item1.getValue().setStockProperty(String.valueOf(stock - 1));
                                                item1.getValue().setCurrentProperty(String.valueOf(current + 1));
                                            });
                                        }
                                        send("success");
                                    }
                                    else if(item2.getValue().getNameProperty().equals(check)) {
                                        if(vending.equals("A")) {
                                            Platform.runLater(()-> {
                                                String name = tableView.getTreeItem(1).getValue().getNameProperty();
                                                int price = Integer.parseInt(tableView.getTreeItem(1).getValue().getPriceProperty());
                                                int current = Integer.parseInt(tableView.getTreeItem(1).getValue().getCurrentProperty());
                                                int stock = Integer.parseInt(tableView.getTreeItem(1).getValue().getStockProperty());
                                                total_coins += price;
                                                tableView.getTreeItem(1).getValue().setStockProperty(String.valueOf(stock - 1));
                                                tableView.getTreeItem(1).getValue().setCurrentProperty(String.valueOf(current + 1));
                                                item2.getValue().setStockProperty(String.valueOf(stock - 1));
                                                item2.getValue().setCurrentProperty(String.valueOf(current + 1));
                                            });
                                        }
                                        else if(vending.equals("B")) {
                                            Platform.runLater(()-> {
                                                String name = tableView1.getTreeItem(1).getValue().getNameProperty();
                                                int price = Integer.parseInt(tableView1.getTreeItem(1).getValue().getPriceProperty());
                                                int current = Integer.parseInt(tableView1.getTreeItem(1).getValue().getCurrentProperty());
                                                int stock = Integer.parseInt(tableView1.getTreeItem(1).getValue().getStockProperty());
                                                total_coins += price;
                                                tableView1.getTreeItem(1).getValue().setStockProperty(String.valueOf(stock - 1));
                                                tableView1.getTreeItem(1).getValue().setCurrentProperty(String.valueOf(current + 1));
                                                item2.getValue().setStockProperty(String.valueOf(stock - 1));
                                                item2.getValue().setCurrentProperty(String.valueOf(current + 1));
                                            });
                                        }
                                        send("success");
                                    }
                                    else if(item3.getValue().getNameProperty().equals(check)) {
                                        if(vending.equals("A")) {
                                            Platform.runLater(()-> {
                                                String name = tableView.getTreeItem(2).getValue().getNameProperty();
                                                int price = Integer.parseInt(tableView.getTreeItem(2).getValue().getPriceProperty());
                                                int current = Integer.parseInt(tableView.getTreeItem(2).getValue().getCurrentProperty());
                                                int stock = Integer.parseInt(tableView.getTreeItem(2).getValue().getStockProperty());
                                                total_coins += price;
                                                tableView.getTreeItem(2).getValue().setStockProperty(String.valueOf(stock - 1));
                                                tableView.getTreeItem(2).getValue().setCurrentProperty(String.valueOf(current + 1));
                                                item3.getValue().setStockProperty(String.valueOf(stock - 1));
                                                item3.getValue().setCurrentProperty(String.valueOf(current + 1));
                                            });
                                        }
                                        else if(vending.equals("B")) {
                                            Platform.runLater(()-> {
                                                String name = tableView1.getTreeItem(2).getValue().getNameProperty();
                                                int price = Integer.parseInt(tableView1.getTreeItem(2).getValue().getPriceProperty());
                                                int current = Integer.parseInt(tableView1.getTreeItem(2).getValue().getCurrentProperty());
                                                int stock = Integer.parseInt(tableView1.getTreeItem(2).getValue().getStockProperty());
                                                total_coins += price;
                                                tableView1.getTreeItem(2).getValue().setStockProperty(String.valueOf(stock - 1));
                                                tableView1.getTreeItem(2).getValue().setCurrentProperty(String.valueOf(current + 1));
                                                item3.getValue().setStockProperty(String.valueOf(stock - 1));
                                                item3.getValue().setCurrentProperty(String.valueOf(current + 1));
                                            });
                                        }
                                        send("success");
                                    }
                                    else if(item4.getValue().getNameProperty().equals(check)) {
                                        if(vending.equals("A")) {
                                            Platform.runLater(()-> {
                                                String name = tableView.getTreeItem(3).getValue().getNameProperty();
                                                int price = Integer.parseInt(tableView.getTreeItem(3).getValue().getPriceProperty());
                                                int current = Integer.parseInt(tableView.getTreeItem(3).getValue().getCurrentProperty());
                                                int stock = Integer.parseInt(tableView.getTreeItem(3).getValue().getStockProperty());
                                                total_coins += price;
                                                tableView.getTreeItem(3).getValue().setStockProperty(String.valueOf(stock - 1));
                                                tableView.getTreeItem(3).getValue().setCurrentProperty(String.valueOf(current + 1));
                                                item4.getValue().setStockProperty(String.valueOf(stock - 1));
                                                item4.getValue().setCurrentProperty(String.valueOf(current + 1));
                                            });
                                        }
                                        else if(vending.equals("B")) {
                                            Platform.runLater(()-> {
                                                String name = tableView1.getTreeItem(3).getValue().getNameProperty();
                                                int price = Integer.parseInt(tableView1.getTreeItem(3).getValue().getPriceProperty());
                                                int current = Integer.parseInt(tableView1.getTreeItem(3).getValue().getCurrentProperty());
                                                int stock = Integer.parseInt(tableView1.getTreeItem(3).getValue().getStockProperty());
                                                total_coins += price;
                                                tableView1.getTreeItem(3).getValue().setStockProperty(String.valueOf(stock - 1));
                                                tableView1.getTreeItem(3).getValue().setCurrentProperty(String.valueOf(current + 1));
                                                item4.getValue().setStockProperty(String.valueOf(stock - 1));
                                                item4.getValue().setCurrentProperty(String.valueOf(current + 1));
                                            });
                                        }
                                        send("success");
                                    }
                                    else if(item5.getValue().getNameProperty().equals(check)) {
                                        if(vending.equals("A")) {
                                            Platform.runLater(()-> {
                                                String name = tableView.getTreeItem(4).getValue().getNameProperty();
                                                int price = Integer.parseInt(tableView.getTreeItem(4).getValue().getPriceProperty());
                                                int current = Integer.parseInt(tableView.getTreeItem(4).getValue().getCurrentProperty());
                                                int stock = Integer.parseInt(tableView.getTreeItem(4).getValue().getStockProperty());
                                                total_coins += price;
                                                tableView.getTreeItem(4).getValue().setStockProperty(String.valueOf(stock - 1));
                                                tableView.getTreeItem(4).getValue().setCurrentProperty(String.valueOf(current + 1));
                                                item5.getValue().setStockProperty(String.valueOf(stock - 1));
                                                item5.getValue().setCurrentProperty(String.valueOf(current + 1));
                                            });
                                        }
                                        else if(vending.equals("B")) {
                                            Platform.runLater(()-> {
                                                String name = tableView1.getTreeItem(4).getValue().getNameProperty();
                                                int price = Integer.parseInt(tableView1.getTreeItem(4).getValue().getPriceProperty());
                                                int current = Integer.parseInt(tableView1.getTreeItem(4).getValue().getCurrentProperty());
                                                int stock = Integer.parseInt(tableView1.getTreeItem(4).getValue().getStockProperty());
                                                total_coins += price;
                                                tableView1.getTreeItem(4).getValue().setStockProperty(String.valueOf(stock - 1));
                                                tableView1.getTreeItem(4).getValue().setCurrentProperty(String.valueOf(current + 1));
                                                item5.getValue().setStockProperty(String.valueOf(stock - 1));
                                                item5.getValue().setCurrentProperty(String.valueOf(current + 1));
                                            });
                                        }
                                        send("success");
                                    }
                                }
                                else if((meta.equals("change"))) {
                                    String changeItem1_name = dataInputStream.readUTF(); int changeItem1_price = dataInputStream.readInt(); String changeItem1_stock = dataInputStream.readUTF(); int changeItem1_curr = dataInputStream.readInt();
                                    String changeItem2_name = dataInputStream.readUTF(); int changeItem2_price = dataInputStream.readInt(); String changeItem2_stock = dataInputStream.readUTF(); int changeItem2_curr = dataInputStream.readInt();
                                    String changeItem3_name = dataInputStream.readUTF(); int changeItem3_price = dataInputStream.readInt(); String changeItem3_stock = dataInputStream.readUTF(); int changeItem3_curr = dataInputStream.readInt();
                                    String changeItem4_name = dataInputStream.readUTF(); int changeItem4_price = dataInputStream.readInt(); String changeItem4_stock = dataInputStream.readUTF(); int changeItem4_curr = dataInputStream.readInt();
                                    String changeItem5_name = dataInputStream.readUTF(); int changeItem5_price = dataInputStream.readInt(); String changeItem5_stock = dataInputStream.readUTF(); int changeItem5_curr = dataInputStream.readInt();
                                    if(vending.equals("A")) {
                                        Platform.runLater(()-> {
                                            System.out.println(changeItem1_curr);
                                            System.out.println(changeItem2_curr);
                                            System.out.println(changeItem3_curr);
                                            System.out.println(changeItem4_curr);
                                            System.out.println(changeItem5_curr);

                                            tableView.getTreeItem(0).getValue().setNameProperty(changeItem1_name); tableView.getTreeItem(0).getValue().setPriceProperty(String.valueOf(changeItem1_price));
                                            tableView.getTreeItem(0).getValue().setStockProperty(changeItem1_stock); tableView.getTreeItem(0).getValue().setCurrentProperty(String.valueOf(changeItem1_curr));
                                            item1.getValue().setNameProperty(changeItem1_name); item1.getValue().setPriceProperty(String.valueOf(changeItem1_price));
                                            item1.getValue().setStockProperty(changeItem1_stock); item1.getValue().setCurrentProperty(String.valueOf(changeItem1_curr));

                                            tableView.getTreeItem(1).getValue().setNameProperty(changeItem2_name); tableView.getTreeItem(1).getValue().setPriceProperty(String.valueOf(changeItem2_price));
                                            tableView.getTreeItem(1).getValue().setStockProperty(changeItem2_stock); tableView.getTreeItem(1).getValue().setCurrentProperty(String.valueOf(changeItem2_curr));
                                            item2.getValue().setNameProperty(changeItem2_name); item2.getValue().setPriceProperty(String.valueOf(changeItem2_price));
                                            item2.getValue().setStockProperty(changeItem2_stock); item2.getValue().setCurrentProperty(String.valueOf(changeItem2_curr));

                                            tableView.getTreeItem(2).getValue().setNameProperty(changeItem3_name); tableView.getTreeItem(2).getValue().setPriceProperty(String.valueOf(changeItem3_price));
                                            tableView.getTreeItem(2).getValue().setStockProperty(changeItem3_stock); tableView.getTreeItem(2).getValue().setCurrentProperty(String.valueOf(changeItem3_curr));
                                            item3.getValue().setNameProperty(changeItem3_name); item3.getValue().setPriceProperty(String.valueOf(changeItem3_price));
                                            item3.getValue().setStockProperty(changeItem3_stock); item3.getValue().setCurrentProperty(String.valueOf(changeItem3_curr));

                                            tableView.getTreeItem(3).getValue().setNameProperty(changeItem4_name); tableView.getTreeItem(3).getValue().setPriceProperty(String.valueOf(changeItem4_price));
                                            tableView.getTreeItem(3).getValue().setStockProperty(changeItem4_stock); tableView.getTreeItem(3).getValue().setCurrentProperty(String.valueOf(changeItem4_curr));
                                            item4.getValue().setNameProperty(changeItem4_name); item4.getValue().setPriceProperty(String.valueOf(changeItem4_price));
                                            item4.getValue().setStockProperty(changeItem4_stock); item4.getValue().setCurrentProperty(String.valueOf(changeItem4_curr));

                                            tableView.getTreeItem(4).getValue().setNameProperty(changeItem5_name); tableView.getTreeItem(4).getValue().setPriceProperty(String.valueOf(changeItem5_price));
                                            tableView.getTreeItem(4).getValue().setStockProperty(changeItem5_stock); tableView.getTreeItem(4).getValue().setCurrentProperty(String.valueOf(changeItem5_curr));
                                            item5.getValue().setNameProperty(changeItem5_name); item5.getValue().setPriceProperty(String.valueOf(changeItem5_price));
                                            item5.getValue().setStockProperty(changeItem5_stock); item5.getValue().setCurrentProperty(String.valueOf(changeItem5_curr));
                                        });
                                    }
                                    else if(vending.equals("B")) {
                                        Platform.runLater(()-> {
                                            tableView1.getTreeItem(0).getValue().setNameProperty(changeItem1_name); tableView1.getTreeItem(0).getValue().setPriceProperty(String.valueOf(changeItem1_price));
                                            tableView1.getTreeItem(0).getValue().setStockProperty(changeItem1_stock); tableView1.getTreeItem(0).getValue().setCurrentProperty(String.valueOf(changeItem1_curr));
                                            item1.getValue().setNameProperty(changeItem1_name); item1.getValue().setPriceProperty(String.valueOf(changeItem1_price));
                                            item1.getValue().setStockProperty(changeItem1_stock); item1.getValue().setCurrentProperty(String.valueOf(changeItem1_curr));

                                            tableView1.getTreeItem(1).getValue().setNameProperty(changeItem2_name); tableView1.getTreeItem(1).getValue().setPriceProperty(String.valueOf(changeItem2_price));
                                            tableView1.getTreeItem(1).getValue().setStockProperty(changeItem2_stock); tableView1.getTreeItem(1).getValue().setCurrentProperty(String.valueOf(changeItem2_curr));
                                            item2.getValue().setNameProperty(changeItem2_name); item2.getValue().setPriceProperty(String.valueOf(changeItem2_price));
                                            item2.getValue().setStockProperty(changeItem2_stock); item2.getValue().setCurrentProperty(String.valueOf(changeItem2_curr));

                                            tableView1.getTreeItem(2).getValue().setNameProperty(changeItem3_name); tableView1.getTreeItem(2).getValue().setPriceProperty(String.valueOf(changeItem3_price));
                                            tableView1.getTreeItem(2).getValue().setStockProperty(changeItem3_stock); tableView1.getTreeItem(2).getValue().setCurrentProperty(String.valueOf(changeItem3_curr));
                                            item3.getValue().setNameProperty(changeItem3_name); item3.getValue().setPriceProperty(String.valueOf(changeItem3_price));
                                            item3.getValue().setStockProperty(changeItem3_stock); item3.getValue().setCurrentProperty(String.valueOf(changeItem3_curr));

                                            tableView1.getTreeItem(3).getValue().setNameProperty(changeItem4_name); tableView1.getTreeItem(3).getValue().setPriceProperty(String.valueOf(changeItem4_price));
                                            tableView1.getTreeItem(3).getValue().setStockProperty(changeItem4_stock); tableView1.getTreeItem(3).getValue().setCurrentProperty(String.valueOf(changeItem4_curr));
                                            item4.getValue().setNameProperty(changeItem4_name); item4.getValue().setPriceProperty(String.valueOf(changeItem4_price));
                                            item4.getValue().setStockProperty(changeItem4_stock); item4.getValue().setCurrentProperty(String.valueOf(changeItem4_curr));

                                            tableView1.getTreeItem(4).getValue().setNameProperty(changeItem5_name); tableView1.getTreeItem(4).getValue().setPriceProperty(String.valueOf(changeItem5_price));
                                            tableView1.getTreeItem(4).getValue().setStockProperty(changeItem5_stock); tableView1.getTreeItem(4).getValue().setCurrentProperty(String.valueOf(changeItem5_curr));
                                            item5.getValue().setNameProperty(changeItem5_name); item5.getValue().setPriceProperty(String.valueOf(changeItem5_price));
                                            item5.getValue().setStockProperty(changeItem5_stock); item5.getValue().setCurrentProperty(String.valueOf(changeItem5_curr));
                                        });
                                    }
                                    send("change_item");
                                }
                                else if((meta.equals("setChange10"))) {
                                    int count = dataInputStream.readInt();
                                    if(vending.equals("A")) change10A.setText(String.valueOf(count));
                                    else change10B.setText(String.valueOf(count));
                                    send("Change10");
                                    Platform.runLater(()-> System.out.println("10원: " + count));
                                }
                                else if((meta.equals("setChange50"))) {
                                    int count = dataInputStream.readInt();
                                    if(vending.equals("A")) change50A.setText(String.valueOf(count));
                                    else change50B.setText(String.valueOf(count));
                                    send("Change50");
                                    Platform.runLater(()-> System.out.println("check: 50원: " + count));
                                }
                                else if((meta.equals("setChange100"))) {
                                    int count = dataInputStream.readInt();
                                    if(vending.equals("A")) change100A.setText(String.valueOf(count));
                                    else change100B.setText(String.valueOf(count));
                                    send("Change100");
                                    Platform.runLater(()-> System.out.println("check: 100원: " + count));
                                }
                                else if((meta.equals("setChange500"))) {
                                    int count = dataInputStream.readInt();
                                    if(vending.equals("A")) change500A.setText(String.valueOf(count));
                                    else change500B.setText(String.valueOf(count));
                                    send("Change500");
                                    Platform.runLater(()-> System.out.println("500원: " + count));
                                }
                                else if((meta.equals("setChange1000"))) {
                                    int count = dataInputStream.readInt();
                                    if(vending.equals("A")) change1000A.setText(String.valueOf(count));
                                    else change1000B.setText(String.valueOf(count));
                                    send("Change1000");
                                    Platform.runLater(()-> System.out.println("1000원: " + count));
                                }
                                else if((meta.equals("setChangeAll"))) {
                                    int c10 = dataInputStream.readInt(); int c50 = dataInputStream.readInt();
                                    int c100 = dataInputStream.readInt(); int c500 = dataInputStream.readInt();
                                    int c1000 = dataInputStream.readInt();
                                    if(vending.equals("A")) {
                                        change10A.setText(String.valueOf(c10));
                                        change50A.setText(String.valueOf(c50));
                                        change100A.setText(String.valueOf(c100));
                                        change500A.setText(String.valueOf(c500));
                                        change1000A.setText(String.valueOf(c1000));
                                    }
                                    else {
                                        change10B.setText(String.valueOf(c10));
                                        change50B.setText(String.valueOf(c50));
                                        change100B.setText(String.valueOf(c100));
                                        change500B.setText(String.valueOf(c500));
                                        change1000B.setText(String.valueOf(c1000));
                                    }
                                    Platform.runLater(()-> {
                                        System.out.println("10원: " + c10);
                                        System.out.println("50원: " + c50);
                                        System.out.println("100원: " + c100);
                                        System.out.println("500원: " + c500);
                                        System.out.println("1000원: " + c1000);
                                    });
                                }
                                else if((meta.equals("SoldOut"))) {
                                    String name = dataInputStream.readUTF();
                                    if(vending.equals("A")) Platform.runLater(()-> displayText("A 자판기에서 " + name + "이 품절되었습니다."));
                                    else  Platform.runLater(()-> displayText("B 자판기에서 " + name + "이 품절되었습니다."));
                                }
                            }
                        }
                    } catch (Exception e) {
                        try {
                            connections.remove(Client.this);
                            String message = "[클라이언트 통신 안됨: " + socket.getRemoteSocketAddress() + ": " + Thread.currentThread().getName() + " ]";
                            Platform.runLater(() -> displayText(message));
                            socket.close();
                        } catch (IOException e2) {
                        }
                    }
                }
            };
            executorService.submit(runnable); // 스레드풀에서 처리
        }


        void send(String message) {
            // 데이터 전송 코드
            Runnable runnable = new Runnable() { // 보내기 작업 생성
                @Override
                public void run() {
                    try {
                        DataOutputStream dataOutputStream = new DataOutputStream(socket.getOutputStream());

                        if (message.equals("init")) {
                            dataOutputStream.writeUTF("init");
                            dataOutputStream.writeInt(5);
                            dataOutputStream.writeInt(3);
                            dataOutputStream.writeUTF(item1.getValue().getNameProperty()); dataOutputStream.writeInt(Integer.parseInt(item1.getValue().getPriceProperty()));
                            dataOutputStream.writeUTF(item2.getValue().getNameProperty()); dataOutputStream.writeInt(Integer.parseInt(item2.getValue().getPriceProperty()));
                            dataOutputStream.writeUTF(item3.getValue().getNameProperty()); dataOutputStream.writeInt(Integer.parseInt(item3.getValue().getPriceProperty()));
                            dataOutputStream.writeUTF(item4.getValue().getNameProperty()); dataOutputStream.writeInt(Integer.parseInt(item4.getValue().getPriceProperty()));
                            dataOutputStream.writeUTF(item5.getValue().getNameProperty()); dataOutputStream.writeInt(Integer.parseInt(item5.getValue().getPriceProperty()));
                            dataOutputStream.flush();
                        }
                        else if(message.equals("success")) {
                            if(item1.getValue().getNameProperty().equals(check)) { // 물 성공
                                dataOutputStream.writeUTF("success");
                                dataOutputStream.writeUTF(check);
                                dataOutputStream.flush();
                                Platform.runLater(()-> displayText("[판매완료] " + socket.getRemoteSocketAddress() + ": " + vending + " : " + check + " " + item1.getValue().getPriceProperty() + " 총 수익 : " + total_coins));
                            }
                            else if(item2.getValue().getNameProperty().equals(check)) { // 커피 성공
                                dataOutputStream.writeUTF("success");
                                dataOutputStream.writeUTF(check);
                                dataOutputStream.flush();
                                Platform.runLater(()-> displayText("[판매완료] " + socket.getRemoteSocketAddress() + ": " + vending + " : " + check + " " + item2.getValue().getPriceProperty() + " 총 수익 : " + total_coins));
                            }
                            else if(item3.getValue().getNameProperty().equals(check)) { // 이온 성공
                                dataOutputStream.writeUTF("success");
                                dataOutputStream.writeUTF(check);
                                dataOutputStream.flush();
                                Platform.runLater(()-> displayText("[판매완료] " + socket.getRemoteSocketAddress() + ": " + vending + " : " + check + " " + item3.getValue().getPriceProperty() + " 총 수익 : " + total_coins));
                            }
                            else if(item4.getValue().getNameProperty().equals(check)) { // 비싼커피 성공
                                dataOutputStream.writeUTF("success");
                                dataOutputStream.writeUTF(check);
                                dataOutputStream.flush();
                                Platform.runLater(()-> displayText("[판매완료] " + socket.getRemoteSocketAddress() + ": " + vending + " : " + check + " " + item4.getValue().getPriceProperty() + " 총 수익 : " + total_coins));
                            }
                            else if(item5.getValue().getNameProperty().equals(check)) { // 소다 성공
                                dataOutputStream.writeUTF("success");
                                dataOutputStream.writeUTF(check);
                                dataOutputStream.flush();
                                Platform.runLater(()-> displayText("[판매완료] " + socket.getRemoteSocketAddress() + ": " + vending + " : " + check + " " + item5.getValue().getPriceProperty() + " 총 수익 : " + total_coins));
                            }
                        }
                        else if(message.equals("change_item")) {
                            dataOutputStream.writeUTF("change_item");
                            dataOutputStream.writeUTF(item1.getValue().getNameProperty()); dataOutputStream.writeUTF(item1.getValue().getPriceProperty());
                            dataOutputStream.writeUTF(item1.getValue().getStockProperty()); dataOutputStream.writeUTF(item1.getValue().getCurrentProperty());

                            dataOutputStream.writeUTF(item2.getValue().getNameProperty()); dataOutputStream.writeUTF(item2.getValue().getPriceProperty());
                            dataOutputStream.writeUTF(item2.getValue().getStockProperty()); dataOutputStream.writeUTF(item2.getValue().getCurrentProperty());

                            dataOutputStream.writeUTF(item3.getValue().getNameProperty()); dataOutputStream.writeUTF(item3.getValue().getPriceProperty());
                            dataOutputStream.writeUTF(item3.getValue().getStockProperty()); dataOutputStream.writeUTF(item3.getValue().getCurrentProperty());

                            dataOutputStream.writeUTF(item4.getValue().getNameProperty()); dataOutputStream.writeUTF(item4.getValue().getPriceProperty());
                            dataOutputStream.writeUTF(item4.getValue().getStockProperty()); dataOutputStream.writeUTF(item4.getValue().getCurrentProperty());

                            dataOutputStream.writeUTF(item5.getValue().getNameProperty()); dataOutputStream.writeUTF(item5.getValue().getPriceProperty());
                            dataOutputStream.writeUTF(item5.getValue().getStockProperty()); dataOutputStream.writeUTF(item5.getValue().getCurrentProperty());
                            dataOutputStream.flush();

                        }
                        else if(message.equals("Change10")) {
                            dataOutputStream.writeUTF("change10");
                            if(vending.equals("A")) dataOutputStream.writeInt(Integer.parseInt(change10A.getText()));
                            else dataOutputStream.writeInt(Integer.parseInt(change10B.getText()));
                            dataOutputStream.flush();
                        }
                        else if(message.equals("Change50")) {
                            dataOutputStream.writeUTF("change50");
                            if(vending.equals("A")) dataOutputStream.writeInt(Integer.parseInt(change50A.getText()));
                            else dataOutputStream.writeInt(Integer.parseInt(change50B.getText()));
                            dataOutputStream.flush();
                        }
                        else if(message.equals("Change100")) {
                            dataOutputStream.writeUTF("change100");
                            if(vending.equals("A")) dataOutputStream.writeInt(Integer.parseInt(change100A.getText()));
                            else dataOutputStream.writeInt(Integer.parseInt(change100B.getText()));
                            dataOutputStream.flush();
                        }
                        else if(message.equals("Change500")) {
                            dataOutputStream.writeUTF("change500");
                            if(vending.equals("A")) dataOutputStream.writeInt(Integer.parseInt(change500A.getText()));
                            else dataOutputStream.writeInt(Integer.parseInt(change500B.getText()));
                            dataOutputStream.flush();
                        }
                        else if(message.equals("Change1000")) {
                            dataOutputStream.writeUTF("change1000");
                            if(vending.equals("A")) dataOutputStream.writeInt(Integer.parseInt(change1000A.getText()));
                            else dataOutputStream.writeInt(Integer.parseInt(change1000B.getText()));
                            dataOutputStream.flush();
                        }

                    } catch (Exception e) {
                        try {
                            String message = "[클라이언트 통신 안됨: " + socket.getRemoteSocketAddress() + ": " + Thread.currentThread().getName() + " ]";
                            Platform.runLater(() -> displayText(message));
                            connections.remove(Client.this);
                            socket.close();
                        } catch (IOException e2) {
                        }
                    }
                }
            };
            executorService.submit(runnable); // 스레드풀에서 처리
        }
    }

    void displayText(String text) {
        txtDisplay.appendText(text + "\n");
    }

}
