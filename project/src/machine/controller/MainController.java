package machine.controller;

import Server.model.StageStore;
import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.stage.Stage;
import machine.model.Beverage;
import machine.model.Coin;

import java.io.*;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.*;

import static machine.controller.AdminController.*;

public class MainController implements Initializable {
    static Socket socket;
    private final Stage stage = StageStore.stage;
    // 음료수 버튼, 동전 넣는 버튼, 반환 버튼, 관리자 로그인 버튼
    public Button soda, coffee, water, sports_drink, premium_coffee;
    public Button coin_10, coin_50, coin_100, coin_500, coin_1000;
    public static String item1_name, item2_name, item3_name, item4_name, item5_name;
    public static int item1_price, item2_price, item3_price, item4_price, item5_price;
    public static int item1_curr = 0, item2_curr = 0, item3_curr = 0, item4_curr = 0, item5_curr = 0;
    public static int total = 0;
    public TextField text_coin;
    public Button coin_return;
    public Button log_in;
    public TextField output;
    // 거스름돈
    static Stack<Coin> change_10, change_50, change_100, change_500, change_1000;
    // 음료 재고
    static Queue<Beverage> water_stock, coffee_stock, sports_drink_stock, premium_coffee_stock, soda_stock;
    // 전체 들어온 돈
    static int total_coins = 0;
    //    HashMap<String, Integer> coins;
    static ArrayList<Coin> coins = new ArrayList<>();


    // 초기 거스름돈 5개씩
    public void setting_change(int count) {
        change_10 = new Stack<Coin>();
        change_50 = new Stack<Coin>();
        change_100 = new Stack<Coin>();
        change_500 = new Stack<Coin>();
        change_1000 = new Stack<Coin>();

        for (int i = 0; i < count; i++) {
            change_10.push(new Coin(10));
            change_50.push(new Coin(50));
            change_100.push(new Coin(100));
            change_500.push(new Coin(500));
            change_1000.push(new Coin(1000));
        }
    }

    // 초기 음료수  3개세팅
    public void setting_beverage(int count) {
        water_stock = new LinkedList<>();
        coffee_stock = new LinkedList<>();
        sports_drink_stock = new LinkedList<>();
        premium_coffee_stock = new LinkedList<>();
        soda_stock = new LinkedList<>();

        for (int i = 0; i < count; i++) {
            water_stock.add(new Beverage(item1_name, item1_price));
            coffee_stock.add(new Beverage(item2_name, item2_price));
            sports_drink_stock.add(new Beverage(item3_name, item3_price));
            premium_coffee_stock.add(new Beverage(item4_name, item4_price));
            soda_stock.add(new Beverage(item5_name, item5_price));
        }
    }

    // 버튼 활성화
    public void active_button() {
        coin_10.setDisable(false);
        coin_50.setDisable(false);
        coin_100.setDisable(false);
        coin_500.setDisable(false);
        coin_1000.setDisable(false);
        log_in.setDisable(false);
    }

// ======================================================================

    /*
        1. 버튼 클릭 시
            1. 넣은 돈에서 물의 가격을 살 수 있는가?
            2. 살수 있다면 넣은 돈에서 돈을 깍는다.
            3. 남은 가격을 화면에 보여준다.
     */
    public void water_Clicked(ActionEvent event) {
        Date current = new Date();
        SimpleDateFormat date = new SimpleDateFormat("yyyy-MM-dd");
        if(!water_stock.isEmpty()) send(item1_name);
    }
    public void coffee_Clicked(ActionEvent event) {
        Date current = new Date();
        SimpleDateFormat date = new SimpleDateFormat("yyyy-MM-dd");
        if(!coffee_stock.isEmpty()) send(item2_name);
    }
    public void sports_drink_Clicked(ActionEvent event) {
        Date current = new Date();
        SimpleDateFormat date = new SimpleDateFormat("yyyy-MM-dd");
        if(!sports_drink_stock.isEmpty()) send(item3_name);
    }
    public void premium_coffee_Clicked(ActionEvent event) {
        Date current = new Date();
        SimpleDateFormat date = new SimpleDateFormat("yyyy-MM-dd");
        if(!premium_coffee_stock.isEmpty()) send(item4_name);
    }
    public void soda_Clicked(ActionEvent event) {
        Date current = new Date();
        SimpleDateFormat date = new SimpleDateFormat("yyyy-MM-dd");
        if(!soda_stock.isEmpty()) send(item5_name);
    }

    /*
        1. 돈 넣는 과정
            1. 현재 돈을 넣었는데 5000원을 넘었다면 리턴
            2. 돈 리스트에 추가
            3. 돈 삽입하고 UI업데이트
            4. 음료수 구매가능한지 품절 여부확인
            5. 반환 여부 UI 업데이트
            6. 천원 3개 넣었는지 확인
            7. 5000원 넘었는지 확인하기
     */
    public void coinInsert_10(ActionEvent actionEvent) {
        output.setText("");
        if (total_coins + 10 > 5000) return;
        coins.add(new Coin(10));
        change_10.push(new Coin(10));
        total_coins += 10;
        update_input_coin(); // 돈 삽입한거 ui 업데이트
        onOffReturnCoin();
        is_buy(); // 음료수 구매가능한거 ui 업데이트
        return_coin(); // 반환가능한지 업데이트
        is_input_1000(); // 천언을 3개 넣었는지 확인
        is_limit_coin(); // 5000원 이상을 넣었는지 확인
        send("setChange", "10", change_10.size());
    }
    public void coinInsert_50(ActionEvent actionEvent) {
        output.setText("");
        if (total_coins + 50 > 5000) return;
        coins.add(new Coin(50));
        change_50.push(new Coin(50));
        total_coins += 50;
        update_input_coin(); // 돈 삽입한거 ui 업데이트
        onOffReturnCoin();
        is_buy(); // 음료수 구매가능한거 ui 업데이트
        return_coin(); // 반환가능한지 업데이트
        is_input_1000(); // 천언을 3개 넣었는지 확인
        is_limit_coin(); // 5000원 이상을 넣었는지 확인
        send("setChange", "50", change_50.size());
    }
    public void coinInsert_100(ActionEvent actionEvent) {
        output.setText("");
        if (total_coins + 100 > 5000) return;
        coins.add(new Coin(100));
        change_100.push(new Coin(100));
        total_coins += 100;
        update_input_coin(); // 돈 삽입한거 ui 업데이트
        onOffReturnCoin();
        is_buy(); // 음료수 구매가능한거 ui 업데이트
        return_coin(); // 반환가능한지 업데이트
        is_input_1000(); // 천언을 3개 넣었는지 확인
        is_limit_coin(); // 5000원 이상을 넣었는지 확인
        send("setChange", "100", change_100.size());
    }
    public void coinInsert_500(ActionEvent actionEvent) {
        output.setText("");
        if (total_coins + 500 > 5000) return;
        coins.add(new Coin(500));
        change_500.push(new Coin(500));
        total_coins += 500;
        update_input_coin(); // 돈 삽입한거 ui 업데이트
        onOffReturnCoin();
        is_buy(); // 음료수 구매가능한거 ui 업데이트
        return_coin(); // 반환가능한지 업데이트
        is_input_1000(); // 천언을 3개 넣었는지 확인
        is_limit_coin(); // 5000원 이상을 넣었는지 확인
        send("setChange", "500", change_500.size());
    }
    public void coinInsert_1000(ActionEvent actionEvent) {
        output.setText("");
        if (total_coins + 1000 > 5000) return;
        coins.add(new Coin(1000));
        change_1000.push(new Coin(1000));
        total_coins += 1000;
        update_input_coin(); // 돈 삽입한거 ui 업데이트
        onOffReturnCoin();
        is_buy(); // 음료수 구매가능한거 ui 업데이트
        return_coin(); // 반환가능한지 업데이트
        is_input_1000(); // 천언을 3개 넣었는지 확인
        is_limit_coin(); // 5000원 이상을 넣었는지 확인
        send("setChange", "1000", change_1000.size());
    }

    // 현재까지 넣은돈 ui 업데이트
    public void update_input_coin() {
        text_coin.setText(Integer.toString(total_coins));
    }

    // 구매 가능한가?
    public void is_buy() {
        // 물 구매가능? 품절인가?
        Platform.runLater(()->{
            if (total_coins >= item1_price) {
                water.setDisable(false);
                if (water_stock.isEmpty()) {
                    water.setDisable(true);
                    water.setText("품절");
                    send("SoldOut", item1_name);
                }
            } else {
                water.setDisable(true);
                if (water_stock.isEmpty()) {
                    water.setDisable(true);
                    water.setText("품절");
                    send("SoldOut", item1_name);
                }
            }

            if (total_coins >= item2_price) {
                coffee.setDisable(false);
                if (coffee_stock.isEmpty()) {
                    coffee.setDisable(true);
                    coffee.setText("품절");
                    send("SoldOut", item2_name);
                }
            } else {
                coffee.setDisable(true);
                if (coffee_stock.isEmpty()) {
                    coffee.setDisable(true);
                    coffee.setText("품절");
                    send("SoldOut", item2_name);
                }
            }

            if (total_coins >= item3_price) {
                sports_drink.setDisable(false);
                if (sports_drink_stock.isEmpty()) {
                    sports_drink.setDisable(true);
                    sports_drink.setText("품절");
                    send("SoldOut", item3_name);
                }
            } else {
                sports_drink.setDisable(true);
                if (sports_drink_stock.isEmpty()) {
                    sports_drink.setDisable(true);
                    sports_drink.setText("품절");
                    send("SoldOut", item3_name);
                }
            }

            if (total_coins >= item4_price) {
                premium_coffee.setDisable(false);
                if (premium_coffee_stock.isEmpty()) {
                    premium_coffee.setDisable(true);
                    premium_coffee.setText("품절");
                    send("SoldOut", item4_name);
                }
            } else {
                premium_coffee.setDisable(true);
                if (premium_coffee_stock.isEmpty()) {
                    premium_coffee.setDisable(true);
                    premium_coffee.setText("품절");
                    send("SoldOut", item4_name);
                }
            }

            if (total_coins >= item5_price) {
                soda.setDisable(false);
                if (soda_stock.isEmpty()) {
                    soda.setDisable(true);
                    soda.setText("품절");
                    send("SoldOut", item5_name);
                }
            } else {
                soda.setDisable(true);
                if (soda_stock.isEmpty()) {
                    soda.setDisable(true);
                    soda.setText("품절");
                    send("SoldOut", item5_name);
                }
            }
        });
    }

    // 품절 UI 업데이트
    public void is_buy_UI() {
        // 물 구매가능? 품절인가?
        Platform.runLater(()->{
            if (total_coins >= item1_price) {
                water.setDisable(false);
                if (water_stock.isEmpty()) {
                    water.setDisable(true);
                    water.setText("품절");
                }
            } else {
                water.setDisable(true);
                if (water_stock.isEmpty()) {
                    water.setDisable(true);
                    water.setText("품절");
                }
            }

            if (total_coins >= item2_price) {
                coffee.setDisable(false);
                if (coffee_stock.isEmpty()) {
                    coffee.setDisable(true);
                    coffee.setText("품절");
                }
            } else {
                coffee.setDisable(true);
                if (coffee_stock.isEmpty()) {
                    coffee.setDisable(true);
                    coffee.setText("품절");
                }
            }

            if (total_coins >= item3_price) {
                sports_drink.setDisable(false);
                if (sports_drink_stock.isEmpty()) {
                    sports_drink.setDisable(true);
                    sports_drink.setText("품절");
                }
            } else {
                sports_drink.setDisable(true);
                if (sports_drink_stock.isEmpty()) {
                    sports_drink.setDisable(true);
                    sports_drink.setText("품절");
                }
            }

            if (total_coins >= item4_price) {
                premium_coffee.setDisable(false);
                if (premium_coffee_stock.isEmpty()) {
                    premium_coffee.setDisable(true);
                    premium_coffee.setText("품절");
                }
            } else {
                premium_coffee.setDisable(true);
                if (premium_coffee_stock.isEmpty()) {
                    premium_coffee.setDisable(true);
                    premium_coffee.setText("품절");
                }
            }

            if (total_coins >= item5_price) {
                soda.setDisable(false);
                if (soda_stock.isEmpty()) {
                    soda.setDisable(true);
                    soda.setText("품절");
                }
            } else {
                soda.setDisable(true);
                if (soda_stock.isEmpty()) {
                    soda.setDisable(true);
                    soda.setText("품절");
                }
            }
        });
    }

    // 반환 가능한지 버튼 활성화
    public void return_coin() { coin_return.setDisable(total_coins == 0); }

    // 천원 넣을 수 있는지 확인
    public void is_input_1000() {
        int count = 0;
        for (Coin coin : coins) {
            if (coin.value == 1000) count++;
        }
        coin_1000.setDisable(count >= 3);
    }

    // 총 5000원 이상 넣었는지 확인
    public void is_limit_coin() {
        if (total_coins >= 5000) {
            coin_10.setDisable(true);
            coin_50.setDisable(true);
            coin_100.setDisable(true);
            coin_500.setDisable(true);
            coin_1000.setDisable(true);
        } else {
            coin_10.setDisable(false);
            coin_50.setDisable(false);
            coin_100.setDisable(false);
            coin_500.setDisable(false);
            coin_1000.setDisable(false);
            is_input_1000();
        }
    }

    // 반환코인이 없다면 비활성화
    public void onOffReturnCoin() {
        coin_return.setDisable(total_coins == 0);
    }

    // ====================================================

    /*
        1. 돈 반환
     */
    public void coinReturn(ActionEvent actionEvent) {
        System.out.println(total_coins);
        int count_10 = 0; int count_50 = 0; int count_100 = 0; int count_500 = 0; int count_1000 = 0;
        String outputString = "";
        while(total_coins >= 0) {
            if(total_coins - 1000 >= 0 && !change_1000.isEmpty()) {
                count_1000++; total_coins -= 1000; change_1000.pop();
            }
            else if(total_coins - 500 >= 0 && !change_500.isEmpty()) {
                count_500++; total_coins -= 500; change_500.pop();
            }
            else if(total_coins - 100 >= 0 && !change_100.isEmpty()) {
                count_100++; total_coins -= 100; change_100.pop();
            }
            else if(total_coins - 50 >= 0 && !change_50.isEmpty()) {
                count_50++; total_coins -= 50; change_50.pop();
            }
            else if(total_coins - 10 >= 0 && !change_10.isEmpty()) {
                count_10++; total_coins -= 10; change_10.pop();
            } else {
                break;
            }
        }

        if(total_coins > 0) {
            output.setText("잔액이 부족합니다.");
            return;
        }

        System.out.println(count_1000);
        System.out.println(count_500);
        System.out.println(count_100);
        System.out.println(count_50);
        System.out.println(count_10);
        send("setChange", change_10.size(), change_50.size(), change_100.size(), change_500.size(), change_1000.size());
        if(count_1000 > 0) outputString += "1000원 : " + count_1000 + "개 ";
        if(count_500 > 0) outputString += "500원 : " + count_500 + "개 ";
        if(count_100 > 0) outputString += "100원 : " + count_100 + "개 ";
        if(count_50 > 0) outputString += "50원 : " + count_50 + "개 ";
        if(count_10 > 0) outputString += "10원 : " + count_10 + "개 ";

        System.out.println("10원 : " + change_10.size() + " 50원 : " + change_50.size() + " 100원 : " + change_100.size() + " 500원 : " + change_500.size() + " 1000원 : " + change_1000.size());

        output.setText(outputString.trim());
        text_coin.setText(Integer.toString(total_coins));
        coins.clear(); // 어떤 돈이 들어있는지에 대한 초기화

        coin_10.setDisable(false); coin_50.setDisable(false); coin_100.setDisable(false);
        coin_500.setDisable(false); coin_1000.setDisable(false);
        onOffReturnCoin();
        is_buy_UI();
    }



    // =====================================================

    /*
        1. 관리자 로그인
     */
    @FXML
    public void loginBtn(ActionEvent actionEvent) { new_stage("loginUI", "login"); }


    // POPUP 띄우기
    static public void pop_up(String message) throws Exception {
        FXMLLoader loader = new FXMLLoader();
        loader.setLocation(MainController.class.getResource("../view/popup.fxml"));
        Parent root= (Parent) loader.load();
        Scene scene = new Scene(root);

        PopUpController pop = loader.getController();
        pop.init(message);
        Stage stage = new Stage();
        stage.setTitle("경고");
        stage.setScene(scene);
        stage.show();
    }

    // 공용 함수, 화면 없애기
    static public void exit_stage(Button btn) {
        Stage stage = (Stage)btn.getScene().getWindow();
        stage.close();
    }

    // 1. 새로운 화면 띄우기
    static public void new_stage(String name, String title) {
        try{
            FXMLLoader fxmlLoader = new FXMLLoader(MainController.class.getResource("../view/" + name + ".fxml"));
            Parent root = fxmlLoader.load();
            Stage stage = new Stage();
            stage.setTitle(title);
            stage.setScene(new Scene(root));
            stage.setResizable(false);
            stage.show();
        }catch (Exception e) {
            e.printStackTrace();
        }
    }

    // 파일로 관리자 데이터 저장하기
    static public void writeFile(String filename, String message) {
        String filePath = Objects.requireNonNull(MainController.class.getResource("")).getPath() + "../data/" + filename;
        File file = new File(filePath);

        try {
            FileWriter fileWriter = new FileWriter(file, false);
            BufferedWriter bufferedWriter = new BufferedWriter(fileWriter);

            bufferedWriter.write(message);
            bufferedWriter.newLine();
            bufferedWriter.flush();
        } catch(Exception e) {
            e.printStackTrace();
        }
    }

    // 파일로 관리자 데이터 저장하기
    static public void writeFileTrue(String filename, String message) {
        String filePath = Objects.requireNonNull(MainController.class.getResource("")).getPath() + "../data/" + filename;
        File file = new File(filePath);

        try {
            FileWriter fileWriter = new FileWriter(file, true);
            BufferedWriter bufferedWriter = new BufferedWriter(fileWriter);

            bufferedWriter.write(message);
            bufferedWriter.newLine();
            bufferedWriter.flush();
        } catch(Exception e) {
            e.printStackTrace();
        }
    }


    /*
        1. 소켓 통신
     */
    // 연결 하기
    public void startClient(){
        // 연결 시작 코드
        Thread thread = new Thread(){
            @Override
            public void run(){
                try {
                    socket = new Socket();
                    socket.connect(new InetSocketAddress("127.0.0.1", 7777));
                    Platform.runLater(()->{ System.out.println("[연결 완료:  " + socket.getRemoteSocketAddress() + "]"); });
                }catch(Exception e){
                    System.out.println("[서버 통신 안됨]");
                    if(!socket.isClosed()) { stopClient(); }
                    return;
                }
                receive(); // 서버에서 보낸 데이터 받기
            }
        };
        thread.start();
    }

    // 연결 끊기
    public static void stopClient(){
        try{
            Platform.runLater(()->{ System.out.println("연결 끊음"); });
            if(socket != null && !socket.isClosed()){
                socket.close();
            }
        }catch(IOException ignored){ }
    }

    // 데이터 받기 코드
    void receive(){
        while(true){
            try {
                InputStream inputStream = socket.getInputStream();
                DataInputStream dataInputStream = new DataInputStream(inputStream);
                String meta = null;

                while((meta = dataInputStream.readUTF()) != null) {
                    // 초기 세팅
                    if(meta.equals("init")) { // 소켓과의 초기 세팅
                        int initChange = dataInputStream.readInt();
                        int initBeverage = dataInputStream.readInt();
                        setting_change(initChange);
                        setting_beverage(initBeverage);
                        item1_name = dataInputStream.readUTF(); item1_price = dataInputStream.readInt();
                        item2_name = dataInputStream.readUTF(); item2_price = dataInputStream.readInt();
                        item3_name = dataInputStream.readUTF(); item3_price = dataInputStream.readInt();
                        item4_name = dataInputStream.readUTF(); item4_price = dataInputStream.readInt();
                        item5_name = dataInputStream.readUTF(); item5_price = dataInputStream.readInt();
                        Platform.runLater(()-> { // sports_drink, premium_coffee;
                            water.setText(String.valueOf(item1_price)); coffee.setText(String.valueOf(item2_price)); sports_drink.setText(String.valueOf(item3_price));
                            premium_coffee.setText(String.valueOf(item4_price)); soda.setText(String.valueOf(item5_price));
                            System.out.println("10원 : " + change_10.size() + " 50원 : " + change_50.size() + " 100원 : " + change_100.size() + " 500원 : " + change_500.size() + " 1000원 : " + change_1000.size());
                        });
                    }
                    // 아이템 구매 확인 메시지
                    else if(meta.equals("success")) {
                        String check = dataInputStream.readUTF();
                        if(check.equals(item1_name)) {
                            if(!water_stock.isEmpty()) {
                                water_stock.poll();
                                total_coins -= item1_price;
//                                for(int i = 0; i < coins.size(); i++) if(coins.get(i).getValue() == item1_price) coins.remove(i);
                                item1_curr += 1; total += item1_price;
                                update_input_coin(); // 돈 삽입한거 ui 업데이트
                                onOffReturnCoin();
                                is_buy(); // 음료수 구매가능한거 ui 업데이트
                                return_coin(); // 반환가능한지 업데이트
                                is_input_1000(); // 천언을 3개 넣었는지 확인
                                output.setText(item1_name);
                                Platform.runLater(()-> {
                                    Date current_date = new Date();
                                    SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
                                    writeFileTrue("beverage.txt", item1_name + " " + dateFormat.format(current_date) + " " + item1_price);
                                    if(water_stock.isEmpty()) writeFileTrue("soldout.txt", item1_name + " " + dateFormat.format(current_date) + " 품절");
                                    System.out.println("10원 : " + change_10.size() + " 50원 : " + change_50.size() + " 100원 : " + change_100.size() + " 500원 : " + change_500.size() + " 1000원 : " + change_1000.size());
                                });
                            }
                        }
                        else if(check.equals(item2_name)) {
                            if(!coffee_stock.isEmpty()) {
                                coffee_stock.poll();
                                total_coins -= item2_price;
//                                for(int i = 0; i < coins.size(); i++) if(coins.get(i).getValue() == item2_price) coins.remove(i);
                                item2_curr += 1;  total += item2_price;
                                update_input_coin(); // 돈 삽입한거 ui 업데이트
                                onOffReturnCoin();
                                is_buy(); // 음료수 구매가능한거 ui 업데이트
                                return_coin(); // 반환가능한지 업데이트
                                is_input_1000(); // 천언을 3개 넣었는지 확인
                                output.setText(item2_name);
                                Platform.runLater(()-> {
                                    Date current_date = new Date();
                                    SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
                                    writeFileTrue("beverage.txt", item2_name + " " + dateFormat.format(current_date) + " " + item2_price);
                                    if(coffee_stock.isEmpty()) writeFileTrue("soldout.txt", item2_name + " " + dateFormat.format(current_date) + " 품절");
                                    System.out.println("10원 : " + change_10.size() + " 50원 : " + change_50.size() + " 100원 : " + change_100.size() + " 500원 : " + change_500.size() + " 1000원 : " + change_1000.size());
                                });
                            }
                        }
                        else if(check.equals(item3_name)) {
                            if(!sports_drink_stock.isEmpty()) {
                                sports_drink_stock.poll();
                                item3_curr += 1;  total += item3_price;
//                                for(int i = 0; i < coins.size(); i++) if(coins.get(i).getValue() == item3_price) coins.remove(i);
                                total_coins -= item3_price;
                                update_input_coin(); // 돈 삽입한거 ui 업데이트
                                onOffReturnCoin();
                                is_buy(); // 음료수 구매가능한거 ui 업데이트
                                return_coin(); // 반환가능한지 업데이트
                                is_input_1000(); // 천언을 3개 넣었는지 확인
                                output.setText(item3_name);
                                Platform.runLater(()-> {
                                    Date current_date = new Date();
                                    SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
                                    writeFileTrue("beverage.txt", item3_name + " " + dateFormat.format(current_date) + " " + item3_price);
                                    if(sports_drink_stock.isEmpty()) writeFileTrue("soldout.txt", item3_name + " " + dateFormat.format(current_date) + " 품절");
                                    System.out.println("10원 : " + change_10.size() + " 50원 : " + change_50.size() + " 100원 : " + change_100.size() + " 500원 : " + change_500.size() + " 1000원 : " + change_1000.size());
                                });
                            }
                        }
                        else if(check.equals(item4_name)) {
                            if(!premium_coffee_stock.isEmpty()) {
                                premium_coffee_stock.poll();
                                item4_curr += 1; total += item4_price;
//                                for(int i = 0; i < coins.size(); i++) if(coins.get(i).getValue() == item4_price) coins.remove(i);
                                total_coins -= item4_price;
                                update_input_coin(); // 돈 삽입한거 ui 업데이트
                                onOffReturnCoin();
                                is_buy(); // 음료수 구매가능한거 ui 업데이트
                                return_coin(); // 반환가능한지 업데이트
                                is_input_1000(); // 천언을 3개 넣었는지 확인
                                output.setText(item4_name);
                                Platform.runLater(()-> {
                                    Date current_date = new Date();
                                    SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
                                    writeFileTrue("beverage.txt", item4_name + " " + dateFormat.format(current_date) + " " + item4_price);
                                    if(premium_coffee_stock.isEmpty()) writeFileTrue("soldout.txt", item4_name + " " + dateFormat.format(current_date) + " 품절");
                                    System.out.println("10원 : " + change_10.size() + " 50원 : " + change_50.size() + " 100원 : " + change_100.size() + " 500원 : " + change_500.size() + " 1000원 : " + change_1000.size());
                                });
                            }
                        }
                        else if(check.equals(item5_name)) {
                            if(!soda_stock.isEmpty()) {
                                soda_stock.poll();
                                total_coins -= item5_price;
//                                for(int i = 0; i < coins.size(); i++) if(coins.get(i).getValue() == item5_price) coins.remove(i);
                                item5_curr += 1;  total += item5_price;
                                update_input_coin(); // 돈 삽입한거 ui 업데이트
                                onOffReturnCoin();
                                is_buy(); // 음료수 구매가능한거 ui 업데이트
                                return_coin(); // 반환가능한지 업데이트
                                is_input_1000(); // 천언을 3개 넣었는지 확인
                                output.setText(item5_name);
                                Platform.runLater(()-> {
                                    Date current_date = new Date();
                                    SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
                                    writeFileTrue("beverage.txt", item5_name + " " + dateFormat.format(current_date) + " " + item5_price);
                                    if(soda_stock.isEmpty()) writeFileTrue("soldout.txt", item5_name + " " + dateFormat.format(current_date) + " 품절");
                                    System.out.println("10원 : " + change_10.size() + " 50원 : " + change_50.size() + " 100원 : " + change_100.size() + " 500원 : " + change_500.size() + " 1000원 : " + change_1000.size());
                                });
                            }
                        }
                    }
                    // 아이템 변경(재고, 이름, 가격)
                    else if(meta.equals("change_item")) {
                        item1_name = dataInputStream.readUTF(); item1_price = Integer.parseInt(dataInputStream.readUTF());
                        item1_stock = dataInputStream.readUTF(); item1_curr = Integer.parseInt(dataInputStream.readUTF());
                        item2_name = dataInputStream.readUTF(); item2_price = Integer.parseInt(dataInputStream.readUTF());
                        item2_stock = dataInputStream.readUTF(); item2_curr = Integer.parseInt(dataInputStream.readUTF());
                        item3_name = dataInputStream.readUTF(); item3_price = Integer.parseInt(dataInputStream.readUTF());
                        item3_stock = dataInputStream.readUTF(); item3_curr = Integer.parseInt(dataInputStream.readUTF());
                        item4_name = dataInputStream.readUTF(); item4_price = Integer.parseInt(dataInputStream.readUTF());
                        item4_stock = dataInputStream.readUTF(); item4_curr = Integer.parseInt(dataInputStream.readUTF());
                        item5_name = dataInputStream.readUTF(); item5_price = Integer.parseInt(dataInputStream.readUTF());
                        item5_stock = dataInputStream.readUTF(); item5_curr = Integer.parseInt(dataInputStream.readUTF());
                        Platform.runLater(()-> { // sports_drink, premium_coffee;
                            water.setText(String.valueOf(item1_price)); coffee.setText(String.valueOf(item2_price)); sports_drink.setText(String.valueOf(item3_price));
                            premium_coffee.setText(String.valueOf(item4_price)); soda.setText(String.valueOf(item5_price));

                            water_stock.clear(); coffee_stock.clear(); sports_drink_stock.clear(); premium_coffee_stock.clear(); soda_stock.clear();
                            for (int i = 0; i < Integer.parseInt(item1_stock); i++) {
                                water_stock.add(new Beverage(item1_name, item1_price));
                                is_buy_UI();
                            }
                            for (int i = 0; i < Integer.parseInt(item2_stock); i++) {
                                coffee_stock.add(new Beverage(item2_name, item2_price));
                                is_buy_UI();
                            }
                            for (int i = 0; i < Integer.parseInt(item3_stock); i++) {
                                sports_drink_stock.add(new Beverage(item3_name, item3_price));
                                is_buy_UI();
                            }
                            for (int i = 0; i < Integer.parseInt(item4_stock); i++) {
                                premium_coffee_stock.add(new Beverage(item4_name, item4_price));
                                is_buy_UI();
                            }
                            for (int i = 0; i < Integer.parseInt(item5_stock); i++) {
                                soda_stock.add(new Beverage(item5_name, item5_price));
                                is_buy_UI();
                            }
                        });

                    }
                    // 거스름돈 10원 개수 변경
                    else if(meta.equals("change10")) {
                        int changeCoin = dataInputStream.readInt();
                        Platform.runLater(()->{
                            while(change_10.size() != changeCoin) {
                                if(change_10.size() < changeCoin) change_10.push(new Coin(10));
                                else change_10.pop();
                            }
                            System.out.println();
                            System.out.println("10원 : " + change_10.size());
                            System.out.println("50원 : " + change_50.size());
                            System.out.println("100원 : " + change_100.size());
                            System.out.println("500원 : " + change_500.size());
                            System.out.println("1000원 : " + change_1000.size());
                            System.out.println();
                        });
                    }
                    // 거스름돈 50원 개수 변경
                    else if(meta.equals("change50")) {
                        int changeCoin = dataInputStream.readInt();
                        Platform.runLater(()->{
                            while(change_50.size() != changeCoin) {
                                if(change_50.size() < changeCoin) change_50.push(new Coin(50));
                                else change_50.pop();
                            }
                            System.out.println();
                            System.out.println("10원 : " + change_10.size());
                            System.out.println("50원 : " + change_50.size());
                            System.out.println("100원 : " + change_100.size());
                            System.out.println("500원 : " + change_500.size());
                            System.out.println("1000원 : " + change_1000.size());
                            System.out.println();
                        });
                    }
                    // 거스름돈 100원 개수 변경
                    else if(meta.equals("change100")) {
                        int changeCoin = dataInputStream.readInt();
                        Platform.runLater(()->{
                            while(change_100.size() != changeCoin) {
                                if(change_100.size() < changeCoin) change_100.push(new Coin(100));
                                else change_100.pop();
                            }
                            System.out.println();
                            System.out.println("10원 : " + change_10.size());
                            System.out.println("50원 : " + change_50.size());
                            System.out.println("100원 : " + change_100.size());
                            System.out.println("500원 : " + change_500.size());
                            System.out.println("1000원 : " + change_1000.size());
                            System.out.println();
                        });
                    }
                    // 거스름돈 500원 개수 변경
                    else if(meta.equals("change500")) {
                        int changeCoin = dataInputStream.readInt();
                        Platform.runLater(()->{
                            while(change_500.size() != changeCoin) {
                                if(change_500.size() < changeCoin) change_500.push(new Coin(500));
                                else change_500.pop();
                            }
                            System.out.println();
                            System.out.println("10원 : " + change_10.size());
                            System.out.println("50원 : " + change_50.size());
                            System.out.println("100원 : " + change_100.size());
                            System.out.println("500원 : " + change_500.size());
                            System.out.println("1000원 : " + change_1000.size());
                            System.out.println();
                        });
                    }
                    // 거스름돈 1000원 개수 변경
                    else if(meta.equals("change1000")) {
                        int changeCoin = dataInputStream.readInt();
                        Platform.runLater(()->{
                            while(change_1000.size() != changeCoin) {
                                if(change_1000.size() < changeCoin) change_1000.push(new Coin(1000));
                                else change_1000.pop();
                            }
                            System.out.println();
                            System.out.println("10원 : " + change_10.size());
                            System.out.println("50원 : " + change_50.size());
                            System.out.println("100원 : " + change_100.size());
                            System.out.println("500원 : " + change_500.size());
                            System.out.println("1000원 : " + change_1000.size());
                            System.out.println();
                        });
                    }
                }

            } catch (IOException e) {
                System.out.println("[서버 통신 안됨]");
                stopClient();
                break;
            }
        }
    }

    static void send(String message){
        // 데이터 전송 코드
        Thread thread = new Thread(){
            @Override
            public void run() {
                try {
                    DataOutputStream dataOutputStream = new DataOutputStream(socket.getOutputStream());
                    // 연결 끊기 위한 메시지 보내기
                    if(message.equals("close")) {
                        Platform.runLater(()-> System.out.println(message));
                        dataOutputStream.writeUTF("close");
                        dataOutputStream.flush();
                    }
                    // 어떤 음료수를 구할지 메시지 보내기
                    else if(message.equals(item1_name)) {
                        dataOutputStream.writeUTF("data");
                        dataOutputStream.writeUTF(item1_name);
                        dataOutputStream.flush();
                    }
                    else if(message.equals(item2_name)) {
                        dataOutputStream.writeUTF("data");
                        dataOutputStream.writeUTF(item2_name);
                        dataOutputStream.flush();
                    }
                    else if(message.equals(item3_name)) {
                        dataOutputStream.writeUTF("data");
                        dataOutputStream.writeUTF(item3_name);
                        dataOutputStream.flush();
                    }
                    else if(message.equals(item4_name)) {
                        dataOutputStream.writeUTF("data");
                        dataOutputStream.writeUTF(item4_name);
                        dataOutputStream.flush();
                    }
                    else if(message.equals(item5_name)) {
                        dataOutputStream.writeUTF("data");
                        dataOutputStream.writeUTF(item5_name);
                        dataOutputStream.flush();
                    }
                    // 음료수 명, 가격, 재고 변경 메시지 보내기
                    else if(message.equals("change")) {
                        dataOutputStream.writeUTF("change");
                        dataOutputStream.writeUTF(item1_name); dataOutputStream.writeInt(item1_price);
                        dataOutputStream.writeUTF(item1_stock); dataOutputStream.writeInt(item1_curr);

                        dataOutputStream.writeUTF(item2_name); dataOutputStream.writeInt(item2_price);
                        dataOutputStream.writeUTF(item2_stock); dataOutputStream.writeInt(item2_curr);

                        dataOutputStream.writeUTF(item3_name); dataOutputStream.writeInt(item3_price);
                        dataOutputStream.writeUTF(item3_stock); dataOutputStream.writeInt(item3_curr);

                        dataOutputStream.writeUTF(item4_name); dataOutputStream.writeInt(item4_price);
                        dataOutputStream.writeUTF(item4_stock); dataOutputStream.writeInt(item4_curr);

                        dataOutputStream.writeUTF(item5_name); dataOutputStream.writeInt(item5_price);
                        dataOutputStream.writeUTF(item5_stock); dataOutputStream.writeInt(item5_curr);

                        dataOutputStream.flush();
                    }
                } catch (IOException e) {
                    Platform.runLater(()-> System.out.println("[서버 통신 안됨]"));
                    stopClient();
                }

            }
        };
        thread.start();
    }
    // 데이터 전송 (수금, 품절)
    static void send(String message, String name) {
        Thread thread = new Thread(){
            @Override
            public void run() {
                try {
                    DataOutputStream dataOutputStream = new DataOutputStream(socket.getOutputStream());
                    // 품절 메시지 보내기
                    if(message.equals("SoldOut")) {
                        dataOutputStream.writeUTF("SoldOut");
                        dataOutputStream.writeUTF(name);
                        dataOutputStream.flush();
                    }
                    // 수금하기
                    else if(message.equals("getCoin")) {
                        dataOutputStream.writeUTF("getCoin");
                        dataOutputStream.writeUTF(name);
                        dataOutputStream.flush();
                    }
                } catch (IOException e) {
                    Platform.runLater(()-> System.out.println("[서버 통신 안됨]"));
                    stopClient();
                }

            }
        };
        thread.start();
    }

    // 데이터 전송 (관지자 페이지), 거스름돈 변경
    static void send(String message, String money, int count){
        Thread thread = new Thread(){
            @Override
            public void run() {
                try {
                    DataOutputStream dataOutputStream = new DataOutputStream(socket.getOutputStream());
                    // 거스름돈 변경하기
                    if(message.equals("setChange")) {
                        if(money.equals("10")) {
                            Platform.runLater(()->{
                                System.out.println(message + " " + money + " " + count);
                            });
                            dataOutputStream.writeUTF("setChange10");
                            dataOutputStream.writeInt(count);
                            dataOutputStream.flush();
                        }
                        else if(money.equals("50")) {
                            Platform.runLater(()->{
                                System.out.println(message + " " + money + " " + count);
                            });
                            dataOutputStream.writeUTF("setChange50");
                            dataOutputStream.writeInt(count);
                            dataOutputStream.flush();
                        }
                        else if(money.equals("100")) {
                            Platform.runLater(()->{
                                System.out.println(message + " " + money + " " + count);
                            });
                            dataOutputStream.writeUTF("setChange100");
                            dataOutputStream.writeInt(count);
                            dataOutputStream.flush();

                        }
                        else if(money.equals("500")) {
                            Platform.runLater(()->{
                                System.out.println(message + " " + money + " " + count);
                            });
                            dataOutputStream.writeUTF("setChange500");
                            dataOutputStream.writeInt(count);
                            dataOutputStream.flush();
                        }
                        else if(money.equals("1000")) {
                            Platform.runLater(()->{
                                System.out.println(message + " " + money + " " + count);
                            });
                            dataOutputStream.writeUTF("setChange1000");
                            dataOutputStream.writeInt(count);
                            dataOutputStream.flush();
                        }
                    }
                } catch (IOException e) {
                    Platform.runLater(()-> System.out.println("[서버 통신 안됨]"));
                    stopClient();
                }

            }
        };
        thread.start();
    }

    // 데이터 전송 코드
    static void send(String message, int c10, int c50, int c100, int c500, int c1000){
        Thread thread = new Thread(){
            @Override
            public void run() {
                try {
                    DataOutputStream dataOutputStream = new DataOutputStream(socket.getOutputStream());
                    if(message.equals("setChange")) {
                        dataOutputStream.writeUTF("setChangeAll");
                        dataOutputStream.writeInt(c10); dataOutputStream.writeInt(c50);
                        dataOutputStream.writeInt(c100); dataOutputStream.writeInt(c500);
                        dataOutputStream.writeInt(c1000);
                        dataOutputStream.flush();
                    }
                } catch (IOException e) {
                    Platform.runLater(()-> System.out.println("[서버 통신 안됨]"));
                    stopClient();
                }

            }
        };
        thread.start();
    }


    /*
        1. 초기 세팅 파일 세팅(매출)
     */

    public void settingBeverage() {
        String filePath = Objects.requireNonNull(MainController.class.getResource("")).getPath() + "../data/beverage.txt";
        File file = new File(filePath);
        try {
            if(file.exists()) {
                if(file.delete()) {
                    System.out.println("삭제");
                    if(file.createNewFile()) {
                        System.out.println("파일 생성");
                    } else {
                        System.out.println("파일을 생성하지 못함");
                    }
                }
                else {
                    System.out.println("실패");
                }
            } else{
                System.out.println("존재 하지 않습니다.");
            }
        } catch(Exception e) {
            e.printStackTrace();
        }
    }
    public void settingSoldout() {
        String filePath = Objects.requireNonNull(MainController.class.getResource("")).getPath() + "../data/soldout.txt";
        File file = new File(filePath);
        try {
            if(file.exists()) {
                if(file.delete()) {
                    System.out.println("삭제");
                    if(file.createNewFile()) {
                        System.out.println("파일 생성");
                    } else {
                        System.out.println("파일을 생성하지 못함");
                    }
                }
                else {
                    System.out.println("실패");
                }
            } else{
                System.out.println("존재 하지 않습니다.");
            }
        } catch(Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        stage.setOnCloseRequest(event -> send("close"));
        text_coin.setEditable(false);
        settingBeverage();
        settingSoldout();
        active_button();
        startClient();
    }

}
