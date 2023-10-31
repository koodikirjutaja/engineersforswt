package ee.ut.math.tvt.salessystem.ui.controllers;

import com.sun.javafx.collections.ObservableListWrapper;
import ee.ut.math.tvt.salessystem.FieldFormatException;
import ee.ut.math.tvt.salessystem.dao.SalesSystemDAO;
import ee.ut.math.tvt.salessystem.dataobjects.StockItem;
import javafx.collections.FXCollections;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.net.URL;
import java.util.ResourceBundle;

public class StockController implements Initializable {

    private static final Logger log = LogManager.getLogger(StockController.class);

    private final SalesSystemDAO dao;

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
        log.info("Started add item process");
        log.debug("StockController-onAddItemClicked");
        addItem();
        log.debug("StockController-onAddItemClicked-addItem");
        log.info("Ended add item process");
    };
    public StockController(SalesSystemDAO dao) {
        this.dao = dao;
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        log.info("StockController Initializing");
        log.debug("StockController-initialize");
        refreshStockItems();
        log.debug("StockController-initialize-refreshStockItems");
        // TODO refresh view after adding new items
        log.info("StockController Initialized");
    }
    public void addItem(){
        log.debug("StockController-addItem");
        try {
            dao.addStockItem(
                    itemNameField.getText(),
                    itemDescriptionField.getText(),
                    itemPriceField.getText(),
                    itemQuantityField.getText()
            );
            log.debug("StockController-addItem-addStockItem: " +
                    itemNameField.getText() + ", " +
                    itemDescriptionField.getText() + ", " +
                    itemPriceField.getText() + ", " +
                    itemQuantityField.getText());
            refreshStockItems();
            log.debug("StockController-addItem-refreshStockItems");
        } catch (FieldFormatException e) {
            log.error("StockController-addItem-FieldFormatException: " + e.getMessage(), e);
            // Popup message
        } catch (Exception e) {
            log.error("StockController-addItem-Exception: " + e.getMessage(), e);
        }
    }
    @FXML
    public void refreshButtonClicked() {
        log.info("Refreshing stock items.");
        log.debug("StockController-refreshButtonClicked");
        refreshStockItems();
        log.debug("StockController-refreshButtonClicked-refreshStockItems");
        log.info("Refreshing stock items.");
    }

    private void refreshStockItems() {
        log.debug("StockController-refreshStockItems");
        warehouseTableView.setItems(FXCollections.observableList(dao.findStockItems()));
        log.debug("StockController-refreshStockItems-setItems");
        warehouseTableView.refresh();
        log.debug("StockController-refreshStockItems-refresh");
    }
}
