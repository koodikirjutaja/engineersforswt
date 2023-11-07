package ee.ut.math.tvt.salessystem.ui.controllers;

import com.sun.javafx.collections.ObservableListWrapper;
import ee.ut.math.tvt.salessystem.FieldFormatException;
import ee.ut.math.tvt.salessystem.dao.SalesSystemDAO;
import ee.ut.math.tvt.salessystem.dataobjects.StockItem;
import javafx.collections.FXCollections;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.layout.GridPane;
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
    private Button editItemButton;
    @FXML
    private TextField itemPriceField;
    @FXML
    public void onAddItemClicked(){
        log.info("Started add item process");
        addItem();
        log.info("Ended add item process");
    };


    public StockController(SalesSystemDAO dao) {
        this.dao = dao;
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        log.info("StockController Initializing");
        refreshStockItems();
        log.info("StockController Initialized");
    }

    @FXML
    public void onEditItemClicked() {
        StockItem selectedItem = warehouseTableView.getSelectionModel().getSelectedItem();
        if (selectedItem != null) {
            Dialog<ButtonType> dialog = new Dialog<>();
            dialog.setTitle("Edit Item");
            dialog.setHeaderText("Edit quantity and price of the selected item");

            // Dialog components
            DialogPane dialogPane = dialog.getDialogPane();
            dialogPane.getButtonTypes().addAll(ButtonType.OK, ButtonType.CANCEL);

            TextField editQuantityField = new TextField(String.valueOf(selectedItem.getQuantity()));
            TextField editPriceField = new TextField(String.valueOf(selectedItem.getPrice()));

            GridPane grid = new GridPane();
            grid.addRow(0, new Label("Quantity:"), editQuantityField);
            grid.addRow(1, new Label("Price:"), editPriceField);
            dialogPane.setContent(grid);

            dialog.setResultConverter(dialogButton -> {
                if (dialogButton == ButtonType.OK) {
                    try {
                        int newQuantity = Integer.parseInt(editQuantityField.getText());
                        double newPrice = Double.parseDouble(editPriceField.getText());

                        if (newQuantity < 0 || newPrice < 0) {
                            showAlert("Input Validation", "Quantity and price must be greater than or equal to 0");
                            return null; // Prevent dialog from closing
                        }

                        selectedItem.setQuantity(newQuantity);
                        selectedItem.setPrice(newPrice);
                        dao.setStockItem(selectedItem.getId(), selectedItem.getName(), selectedItem.getDescription(), newPrice, newQuantity);
                        refreshStockItems();
                    } catch (NumberFormatException e) {
                        showAlert("Input Error", "Invalid number format. Please enter valid numbers.");
                        return null; // Prevent dialog from closing
                    } catch (Exception e) {
                        showAlert("Error", "An error occurred: " + e.getMessage());
                    }
                }
                return null;
            });

            dialog.showAndWait();
        }
    }

    private void showAlert(String title, String content) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(content);
        alert.showAndWait();
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
