package Server.controller;

import Server.model.Item;
import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextArea;
import javafx.scene.control.cell.PropertyValueFactory;

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

    @FXML public TextArea txtDisplay;
    @FXML private TableView<Item> table, table1;
    @FXML private TableColumn<Item, String> name, name1;
    @FXML private TableColumn<Item, Integer> price, price1;
    @FXML private TableColumn<Item, Integer> stock, stock1;
    @FXML private TableColumn<Item, Integer> current, current1;
    public String check = null;
    public ObservableList<Item> list = FXCollections.observableArrayList(new Item("물", 450, 5, 0), new Item("커피", 500, 5, 0),
            new Item("이온음료", 550, 5, 0), new Item("프리미엄 커피", 700, 5, 0), new Item("탄산 음료", 750, 5, 0));

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        table.setEditable(true);
        name.setCellValueFactory(new PropertyValueFactory<Item, String>("name"));
        price.setCellValueFactory(new PropertyValueFactory<Item, Integer>("price"));
        stock.setCellValueFactory(new PropertyValueFactory<Item, Integer>("stock"));
        current.setCellValueFactory(new PropertyValueFactory<Item, Integer>("current"));
        table.setItems(list);

        // 서버 시작 코드 (Executor Service 생성, ServerSocket 생성 및 포트 바인딩, 연결 수락)
        executorService = Executors.newFixedThreadPool(3);
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
        Client(Socket socket) {
            this.socket = socket;
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
                                    Platform.runLater(() -> {
                                        displayText("연결이 끊어졌습니다.");
                                    });
                                    connections.remove(Client.this);
                                    socket.close();
                                    Platform.runLater(() -> displayText("[연결 개수 : " + connections.size() + " ]"));
                                }
                                else if((meta.startsWith("data"))) {
                                    check = dataInputStream.readUTF();
                                    // 구매 처리하기
                                    if(list.get(0).getName().equals(check)) { // 물 구매
                                        total_coins += list.get(0).getPrice();
                                        send("success");
                                    }
                                }
                            }
                            //for(Client client : connections){
//                                client.send(data); // 모든 클라이언트에게 보냄
//                            }
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
                            dataOutputStream.writeUTF(list.get(0).getName()); dataOutputStream.writeInt(list.get(0).getPrice());
                            dataOutputStream.writeUTF(list.get(1).getName()); dataOutputStream.writeInt(list.get(1).getPrice());
                            dataOutputStream.writeUTF(list.get(2).getName()); dataOutputStream.writeInt(list.get(2).getPrice());
                            dataOutputStream.writeUTF(list.get(3).getName()); dataOutputStream.writeInt(list.get(3).getPrice());
                            dataOutputStream.writeUTF(list.get(4).getName()); dataOutputStream.writeInt(list.get(4).getPrice());
                            dataOutputStream.flush();
                        }
                        else if(message.equals("success")) {
                            if(list.get(0).getName().equals(check)) { // 물 성공
                                dataOutputStream.writeUTF("success");
                                dataOutputStream.writeUTF(check);
                                dataOutputStream.flush();
                                Platform.runLater(()-> System.out.println(check + " " + total_coins));
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
