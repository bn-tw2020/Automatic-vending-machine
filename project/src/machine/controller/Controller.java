package machine.controller;

import javafx.event.ActionEvent;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import machine.model.Beverage;
import machine.model.Coin;

import java.net.URL;
import java.util.*;

public class Controller implements Initializable {

    // 음료수 버튼, 동전 넣는 버튼, 반환 버튼, 관리자 로그인 버튼
    public Button soda, coffee, water, sports_drink, premium_coffee;
    public Button coin_10, coin_50, coin_100, coin_500, coin_1000;
    public TextField text_coin;
    public Button coin_return;
    public Button log_in;
    public TextField output;

    // 거스름돈
    Stack<Coin> change_10, change_50, change_100, change_500, change_1000;
    // 음료 재고
    Queue<Beverage> water_stock, coffee_stock, sports_drink_stock, premium_coffee_stock, soda_stock;
    // 전체 들어온 돈
    int total_coins = 0;
    //    HashMap<String, Integer> coins;
    ArrayList<Coin> coins = new ArrayList<>();


    /*
        1. 초기 세팅
     */
    @Override
    public void initialize(URL location, ResourceBundle resources) {
        text_coin.setEditable(false);
//        output.setEditable(false);
        setting_change(5);
        setting_beverage(3);
        active_button();
    }

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
            water_stock.add(new Beverage("water", 450));
            coffee_stock.add(new Beverage("coffee", 500));
            sports_drink_stock.add(new Beverage("sport_drink", 550));
            premium_coffee_stock.add(new Beverage("premium_coffee", 700));
            soda_stock.add(new Beverage("soda", 750));
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
    public void water_Clicked(ActionEvent event) { }
    public void coffee_Clicked(ActionEvent event) { }
    public void sports_drink_Clicked(ActionEvent event) { }
    public void soda_Clicked(ActionEvent event) { }
    public void premium_coffee_Clicked(ActionEvent event) { }

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
    }

    // 현재까지 넣은돈 ui 업데이트
    public void update_input_coin() {
        text_coin.setText(Integer.toString(total_coins));
    }

    // 구매 가능한가?
    public void is_buy() {
        // 물 구매가능? 품절인가?
        if (total_coins >= 450) {
            water.setDisable(false);
            if (water_stock.isEmpty()) {
                water.setDisable(true);
                water.setText("Sold Out");
            }
        } else {
            water.setDisable(true);
            if (water_stock.isEmpty()) {
                water.setDisable(true);
                water.setText("Sold Out");
            }
        }

        if (total_coins >= 500) {
            coffee.setDisable(false);
            if (coffee_stock.isEmpty()) {
                water.setDisable(true);
                water.setText("Sold Out");
            }
        } else {
            coffee.setDisable(true);
            if (coffee_stock.isEmpty()) {
                coffee.setDisable(true);
                coffee.setText("Sold Out");
            }
        }

        if (total_coins >= 550) {
            sports_drink.setDisable(false);
            if (sports_drink_stock.isEmpty()) {
                water.setDisable(true);
                water.setText("Sold Out");
            }
        } else {
            sports_drink.setDisable(true);
            if (sports_drink_stock.isEmpty()) {
                sports_drink.setDisable(true);
                sports_drink.setText("Sold Out");
            }
        }

        if (total_coins >= 700) {
            premium_coffee.setDisable(false);
            if (premium_coffee_stock.isEmpty()) {
                premium_coffee.setDisable(true);
                premium_coffee.setText("Sold Out");
            }
        } else {
            premium_coffee.setDisable(true);
            if (premium_coffee_stock.isEmpty()) {
                premium_coffee.setDisable(true);
                premium_coffee.setText("Sold Out");
            }
        }

        if (total_coins >= 750) {
            soda.setDisable(false);
            if (soda_stock.isEmpty()) {
                soda.setDisable(true);
                soda.setText("Sold Out");
            }
        } else {
            soda.setDisable(true);
            if (soda_stock.isEmpty()) {
                soda.setDisable(true);
                soda.setText("Sold Out");
            }
        }
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

    public void onOffReturnCoin() {
        coin_return.setDisable(total_coins == 0);
    }

    // ====================================================

    /*
        1. 돈 반환
     */
    public void coinReturn(ActionEvent actionEvent) {

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

        System.out.println(total_coins);
        System.out.println(count_1000 + " " + count_500 + " " + count_100 + " " + count_50 + " " + count_10);
        if(count_1000 > 0) outputString += "1000원 : " + count_1000 + "개 ";
        if(count_500 > 0) outputString += "500원 : " + count_500 + "개 ";
        if(count_100 > 0) outputString += "100원 : " + count_100 + "개 ";
        if(count_50 > 0) outputString += "50원 : " + count_50 + "개 ";
        if(count_10 > 0) outputString += "10원 : " + count_10 + "개 ";
        System.out.println(outputString);
        System.out.println("1000원 남은 갯수 : " + change_1000.size());
        System.out.println("500원 남은 갯수 : " + change_500.size());
        System.out.println("100원 남은 갯수 : " + change_100.size());
        System.out.println("50원 남은 갯수 : " + change_50.size());
        System.out.println("10원 남은 갯수 : " + change_10.size());
        output.appendText(outputString.trim());
        text_coin.setText(Integer.toString(total_coins));
        coins.clear(); // 어떤 돈이 들어있는지에 대한 초기화

        coin_10.setDisable(false); coin_50.setDisable(false); coin_100.setDisable(false);
        coin_500.setDisable(false); coin_1000.setDisable(false);
    }


    // =====================================================


    /*
        1. 관리자 로그인
     */
    public void login_btn(ActionEvent actionEvent) {
    }

}
