package machine.model;

import javafx.beans.property.SimpleStringProperty;

public class Item {
    SimpleStringProperty nameProperty;
    SimpleStringProperty priceProperty;
    SimpleStringProperty stockProperty;
    SimpleStringProperty currentProperty;

    public Item(String name, String price, String stock, String current) {
        this.nameProperty = new SimpleStringProperty(name);
        this.priceProperty = new SimpleStringProperty(price);
        this.stockProperty = new SimpleStringProperty(stock);
        this.currentProperty = new SimpleStringProperty(current);
    }

    public void setNameProperty(String nameProperty) {
        this.nameProperty.set(nameProperty);
    }

    public void setPriceProperty(String priceProperty) {
        this.priceProperty.set(priceProperty);
    }

    public void setStockProperty(String stockProperty) {
        this.stockProperty.set(stockProperty);
    }

    public void setCurrentProperty(String currentProperty) {
        this.currentProperty.set(currentProperty);
    }

    public String getNameProperty() {
        return nameProperty.get();
    }

    public SimpleStringProperty namePropertyProperty() {
        return nameProperty;
    }

    public String getPriceProperty() {
        return priceProperty.get();
    }

    public SimpleStringProperty pricePropertyProperty() {
        return priceProperty;
    }

    public String getStockProperty() {
        return stockProperty.get();
    }

    public SimpleStringProperty stockPropertyProperty() {
        return stockProperty;
    }

    public String getCurrentProperty() {
        return currentProperty.get();
    }

    public SimpleStringProperty currentPropertyProperty() {
        return currentProperty;
    }
}
