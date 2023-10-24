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
        long barCode = dao.findStockItems().size();
        itemName = itemNameField.getText();
        itemDescription = itemDescriptionField.getText();
        itemPrice = Double.parseDouble(itemPriceField.getText());
        itemQuantity = Integer.parseInt(itemQuantityField.getText());
        StockItem newStockItem = new StockItem(barCode, itemName, itemDescription, itemPrice, itemQuantity);
        dao.saveStockItem(newStockItem);
        refreshStockItems();
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
