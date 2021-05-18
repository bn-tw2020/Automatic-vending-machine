package Server.controller;

import Server.model.Item;
import com.sun.org.apache.xpath.internal.operations.Bool;
import javafx.application.Platform;
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


    @Override
    public void initialize(URL location, ResourceBundle resources) {
        // 서버 시작 코드 (Executor Service 생성, ServerSocket 생성 및 포트 바인딩, 연결 수락)

        executorService = Executors.newFixedThreadPool(50);
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
                        Socket socket = serverSocket.accept(); // 연결 수락
                        while(connections.size() >= 2) {}
                        String message = "[연결 수락 : " + socket.getRemoteSocketAddress() + ": " + Thread.currentThread().getName() + "]";
                        Platform.runLater(() -> displayText(message));
                        Client client = new Client(socket);
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


    class Client {
        // 데이터 통신 코드
        Socket socket;
        int total_coins = 0; // 총 수익금
        public String check = null;
        TreeItem<Item> item1, item2, item3, item4, item5, root;
        Boolean flag = false;
        Client(Socket socket) {
            this.socket = socket;
            item1 = new TreeItem<>(new Item("물", "450", "5", "0"));
            item2 = new TreeItem<>(new Item("커피", "500", "5", "0"));
            item3 = new TreeItem<>(new Item("이온음료", "550", "5", "0"));
            item4 = new TreeItem<>(new Item("프리미엄 커피", "700", "5", "0"));
            item5 = new TreeItem<>(new Item("탄산 음료", "750", "5", "0"));
            root = new TreeItem<>(new Item("Name", "0", "0", "0"));
            root.getChildren().setAll(item1, item2, item3, item4, item5);

            Platform.runLater(()-> {
                if(tableView.getRoot() == null) {
                    flag = true;
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
                                if (meta.startsWith("close")) {
                                    Platform.runLater(() -> { displayText("연결이 끊어졌습니다."); });
                                    connections.remove(Client.this);
                                    socket.close();

                                    Platform.runLater(() -> {
                                        if(flag){
                                            tableView.setRoot(null);
                                        } else {
                                            tableView1.setRoot(null);
                                        }
                                        displayText("[연결 개수 : " + connections.size() + " ]");
                                    });
                                }
                                else if((meta.startsWith("data"))) {
                                    check = dataInputStream.readUTF();
                                    // 구매 처리하기
                                    if(item1.getValue().getNameProperty().equals(check)) { // 물 구매
                                        total_coins += Integer.parseInt(item1.getValue().getPriceProperty());
                                        send("success");
                                    }
                                    else if(item2.getValue().getNameProperty().equals(check)) {
                                        total_coins += Integer.parseInt(item2.getValue().getPriceProperty());
                                        send("success");
                                    }
                                    else if(item3.getValue().getNameProperty().equals(check)) {
                                        total_coins += Integer.parseInt(item3.getValue().getPriceProperty());
                                        send("success");
                                    }
                                    else if(item4.getValue().getNameProperty().equals(check)) {
                                        total_coins += Integer.parseInt(item4.getValue().getPriceProperty());
                                        send("success");
                                    }
                                    else if(item5.getValue().getNameProperty().equals(check)) {
                                        total_coins += Integer.parseInt(item5.getValue().getPriceProperty());
                                        send("success");
                                    }
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
                            Platform.runLater(()->makeTable(connections.size()));
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
                                Platform.runLater(()-> displayText("[판매완료] " + socket.getRemoteSocketAddress() + ": " + connections.indexOf(Client.this) + " : " + check + " " + item1.getValue().getPriceProperty() + " 총 수익 : " + total_coins));
                            }
                            else if(item2.getValue().getNameProperty().equals(check)) { // 커피 성공
                                dataOutputStream.writeUTF("success");
                                dataOutputStream.writeUTF(check);
                                dataOutputStream.flush();
                                Platform.runLater(()-> displayText("[판매완료] " + socket.getRemoteSocketAddress() + ": " + connections.indexOf(Client.this) + " : " + check + " " + item2.getValue().getPriceProperty() + " 총 수익 : " + total_coins));
                            }
                            else if(item3.getValue().getNameProperty().equals(check)) { // 이온 성공
                                dataOutputStream.writeUTF("success");
                                dataOutputStream.writeUTF(check);
                                dataOutputStream.flush();
                                Platform.runLater(()-> displayText("[판매완료] " + socket.getRemoteSocketAddress() + ": " + connections.indexOf(Client.this) + " : " + check + " " + item3.getValue().getPriceProperty() + " 총 수익 : " + total_coins));
                            }
                            else if(item4.getValue().getNameProperty().equals(check)) { // 비싼커피 성공
                                dataOutputStream.writeUTF("success");
                                dataOutputStream.writeUTF(check);
                                dataOutputStream.flush();
                                Platform.runLater(()-> displayText("[판매완료] " + socket.getRemoteSocketAddress() + ": " + connections.indexOf(Client.this) + " : " + check + " " + item4.getValue().getPriceProperty() + " 총 수익 : " + total_coins));
                            }
                            else if(item5.getValue().getNameProperty().equals(check)) { // 소다 성공
                                dataOutputStream.writeUTF("success");
                                dataOutputStream.writeUTF(check);
                                dataOutputStream.flush();
                                Platform.runLater(()-> displayText("[판매완료] " + socket.getRemoteSocketAddress() + ": " + connections.indexOf(Client.this) + " : " + check + " " + item5.getValue().getPriceProperty() + " 총 수익 : " + total_coins));
                            }
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

    void makeTable(int size) {
        if(size == 0) { }
        else if(size == 1) {}
        else if(size == 2) {}
    }
}
