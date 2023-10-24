package ee.ut.math.tvt.salessystem.ui.controllers;

import com.sun.javafx.collections.ObservableListWrapper;
import ee.ut.math.tvt.salessystem.dao.SalesSystemDAO;
import ee.ut.math.tvt.salessystem.dataobjects.StockItem;
import javafx.collections.FXCollections;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;

import java.net.URL;
import java.util.ResourceBundle;

public class StockController implements Initializable {

    private final SalesSystemDAO dao;
    String itemName;
    String itemDescription;
    int itemQuantity;
    double itemPrice;
    @FXML
    private Button addItem;
    @FXML
    private TableView<StockItem> warehouseTableView;
    @FXML
    private TextField itemNameField;
    @FXML
    private TextField itemQuantityField;
    @FXML
    private TextField itemDescriptionField;

    @FXML
    private TextField itemPriceField;
    @FXML
    public void onAddItemClicked(){
        addItem();
    };
    public StockController(SalesSystemDAO dao) {
        this.dao = dao;
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        refreshStockItems();
        // TODO refresh view after adding new items
    }
    public void addItem(){
        if (itemNameField.getText().equals(""))
            throw new IllegalArgumentException("Item must have a name!");
        StockItem stockItem = dao.findStockItem(itemNameField.getText());
        if (stockItem != null) {
            addExistingItem(stockItem);
        }
        else {
            addNewItem();
        }
    }

    public void addExistingItem(StockItem stockItem) {
        itemName = itemNameField.getText();
        if (itemPriceField.getText().equals("")) {
            itemPrice = stockItem.getPrice();
        } else {
            getNewPrice();
        }
        if (itemQuantityField.getText().equals("")) {
            itemQuantity = stockItem.getQuantity();
        } else {
            getNewQuantity(stockItem);
        }
        if (itemDescriptionField.getText().equals("")) {
            itemDescription = stockItem.getDescription();
        } else {
            getNewDescription();
        }
        setStockItem(stockItem);
        refreshStockItems();
    }

    public void addNewItem() {
        long barCode = dao.findStockItems().size();
        itemName = itemNameField.getText();
        if (itemName.equals(""))
            throw new IllegalArgumentException("Item must have a name!");
        itemDescription = itemDescriptionField.getText();
        if (itemDescription.equals(""))
            throw new IllegalArgumentException("Item must have a description!");
        if (!itemPriceField.getText().matches("[0-9]+(\\.[0-9]+){0,1}"))
            throw new NumberFormatException("Not a number");
        if (!itemQuantityField.getText().matches("[0-9]+(\\.[0-9]+){0,1}"))
            throw new NumberFormatException("Not a number");
        itemPrice = Double.parseDouble(itemPriceField.getText());
        itemQuantity = Integer.parseInt(itemQuantityField.getText());
        if (itemPrice < 0)
            throw new IllegalArgumentException("Item price must not be negative");
        if (itemQuantity < 0)
            throw new IllegalArgumentException("Item quantity must not be negative");
        StockItem stockItem = new StockItem(barCode, itemName, itemDescription, itemPrice, itemQuantity);
        dao.saveStockItem(stockItem);
        refreshStockItems();
    }

    private void setStockItem(StockItem stockItem) {
        dao.setStockItem(stockItem.getId(), itemName, itemDescription, itemPrice, itemQuantity);
        stockItem.setName(itemName);
        stockItem.setDescription(itemDescription);
        stockItem.setPrice(itemPrice);
        stockItem.setQuantity(itemQuantity);
    }

    private void getNewPrice() {
        if (!itemPriceField.getText().matches("[0-9]+(\\.[0-9]+){0,1}"))
            throw new NumberFormatException("Not a number");
        itemPrice = Double.parseDouble(itemPriceField.getText());
        if (itemPrice < 0)
            throw new IllegalArgumentException("Item price must not be negative");
    }

    private void getNewQuantity(StockItem stockItem) {
        if (!itemQuantityField.getText().matches("[0-9]+(\\.[0-9]+){0,1}"))
            throw new NumberFormatException("Not a number");
        itemQuantity = stockItem.getQuantity() + Integer.parseInt(itemQuantityField.getText());
        if (itemQuantity < 0)
            throw new IllegalArgumentException("Item quantity must not be negative");
    }

    private void getNewDescription() {
        itemDescription = itemDescriptionField.getText();
    }

    @FXML
    public void refreshButtonClicked() {
        refreshStockItems();
    }

    private void refreshStockItems() {
        warehouseTableView.setItems(FXCollections.observableList(dao.findStockItems()));
        warehouseTableView.refresh();
    }
}
