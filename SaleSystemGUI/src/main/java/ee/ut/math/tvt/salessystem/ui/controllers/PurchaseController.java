package ee.ut.math.tvt.salessystem.ui.controllers;

import ee.ut.math.tvt.salessystem.SalesSystemException;
import ee.ut.math.tvt.salessystem.dao.SalesSystemDAO;
import ee.ut.math.tvt.salessystem.dataobjects.SoldItem;
import ee.ut.math.tvt.salessystem.dataobjects.StockItem;
import ee.ut.math.tvt.salessystem.logic.ShoppingCart;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.net.URL;
import java.util.Optional;
import java.util.ResourceBundle;
import java.util.stream.Collectors;
import java.util.Comparator;


/**
 * Encapsulates everything that has to do with the purchase tab (the tab
 * labelled "Point-of-sale" in the menu). Consists of the purchase menu,
 * current purchase dialog and shopping cart table.
 */
public class PurchaseController implements Initializable {

    private static final Logger log = LogManager.getLogger(PurchaseController.class);

    private final SalesSystemDAO dao;
    private final ShoppingCart shoppingCart;

    @FXML
    private Button newPurchase;
    @FXML
    private Button submitPurchase;
    @FXML
    private Button cancelPurchase;
    @FXML
    private TextField barCodeField;
    @FXML
    private TextField quantityField;
    @FXML
    private ComboBox<String> productNameComboBox;
    @FXML
    private TextField priceField;
    @FXML
    private Button addItemButton;
    @FXML
    private Button editQuantityButton;
    @FXML
    private TableView<SoldItem> purchaseTableView;

    public PurchaseController(SalesSystemDAO dao, ShoppingCart shoppingCart) {
        this.dao = dao;
        this.shoppingCart = shoppingCart;
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        log.info("Initializing PurchaseController");

        // Populate the ComboBox with product names
        productNameComboBox.setItems(FXCollections.observableArrayList(
                dao.findStockItems().stream()
                        .filter(item -> item.getQuantity() > 0)
                        .map(StockItem::getName)
                        .collect(Collectors.toList())
        ));
        productNameComboBox.valueProperty().addListener((obs, oldValue, newValue) -> {
            // When a product is selected, update the other fields accordingly
            if (newValue != null) {
                updateProductDetails(newValue);
            }
        });

        // Disabling 'cancel' and 'submit' buttons initially
        cancelPurchase.setDisable(true);
        submitPurchase.setDisable(true);
        log.debug("Cancel and Submit buttons disabled");



        // Setting items in the purchase table view from the shopping cart
        purchaseTableView.setItems(FXCollections.observableList(shoppingCart.getAll()));
        log.debug("Purchase table view initialized with items from shopping cart");
        FXCollections.sort(purchaseTableView.getItems(), Comparator.comparing(SoldItem::getQuantity));

        // Disabling product input fields initially
        disableProductField(true);
        log.debug("Product input fields disabled");

        editQuantityButton.setDisable(true);

        purchaseTableView.getSelectionModel().selectedItemProperty().addListener((obs, oldSelection, newSelection) ->
                editQuantityButton.setDisable(newSelection == null)
        );

        // Setting up listener for bar code field to update item details on focus loss
        this.barCodeField.focusedProperty().addListener(new ChangeListener<Boolean>() {
            @Override
            public void changed(ObservableValue<? extends Boolean> arg0, Boolean oldPropertyValue, Boolean newPropertyValue) {
                if (!newPropertyValue) {
                    log.debug("Bar code field focus lost, updating item details");
                    fillInputsBySelectedStockItem();
                }
            }
        });

        log.info("PurchaseController initialization complete");
    }

    private void updateProductDetails(String productName) {
        StockItem stockItem = dao.findStockItem(productName);
        if (stockItem != null) {
            barCodeField.setText(String.valueOf(stockItem.getId()));
            priceField.setText(String.format("%.2f", stockItem.getPrice()));
        }
    }

    /** Event handler for the <code>new purchase</code> event. */
    @FXML
    protected void newPurchaseButtonClicked() {
        log.info("Initiating new sale process");
        try {
            enableInputs();
        } catch (SalesSystemException e) {
            log.error("Error encountered during new sale process: " + e.getMessage(), e);
        }
        refreshProductList();
        log.info("New sale process ready");
    }

    @FXML
    protected void editQuantityButtonClicked() {
        SoldItem selectedItem = purchaseTableView.getSelectionModel().getSelectedItem();
        if (selectedItem != null) {
            TextInputDialog dialog = new TextInputDialog(String.valueOf(selectedItem.getQuantity()));
            dialog.setTitle("Edit Quantity");
            dialog.setHeaderText("Editing Quantity for " + selectedItem.getName());
            dialog.setContentText("Enter new quantity:");

            Optional<String> result = dialog.showAndWait();
            result.ifPresent(quantityStr -> {
                try {
                    int newQuantity = Integer.parseInt(quantityStr);
                    if (newQuantity > 0) {
                        selectedItem.setQuantity(newQuantity);
                        purchaseTableView.refresh();
                    } else {
                        showErrorMessage("Quantity must be greater than 0.");
                    }
                } catch (NumberFormatException e) {
                    showErrorMessage("Invalid quantity format.");
                }
            });
        }
    }

    private void showErrorMessage(String message) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle("Error");
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }


    private void refreshProductList() {
        productNameComboBox.setItems(FXCollections.observableArrayList(
                dao.findStockItems().stream()
                        .filter(item -> item.getQuantity() > 0)
                        .map(StockItem::getName)
                        .collect(Collectors.toList())
        ));
    }



    /**
     * Event handler for the <code>cancel purchase</code> event.
     */
    @FXML
    protected void cancelPurchaseButtonClicked() {
        log.info("Cancelling sale process");
        try {
            shoppingCart.cancelCurrentPurchase();
            disableInputs();
            purchaseTableView.refresh();
            log.debug("Sale process cancelled and UI elements reset");
        } catch (SalesSystemException e) {
            log.error("Error cancelling sale: " + e.getMessage(), e);
        }
    }

    /**
     * Event handler for the <code>submit purchase</code> event.
     */
    @FXML
    protected void submitPurchaseButtonClicked() {
        log.info("Sale complete");
        try {
            log.debug("Contents of the current basket:\n" + shoppingCart.getAll());
            shoppingCart.submitCurrentPurchase();
            disableInputs();
            refreshProductList();
            purchaseTableView.refresh();
        } catch (SalesSystemException e) {
            log.error(e.getMessage(), e);
        }
    }

    // switch UI to the state that allows to proceed with the purchase
    private void enableInputs() {
        log.debug("PurchaseController: Enabling inputs and resetting product fields");
        resetProductField();
        disableProductField(false);
        cancelPurchase.setDisable(false);
        submitPurchase.setDisable(false);
        newPurchase.setDisable(true);
    }

    // switch UI to the state that allows to initiate new purchase
    private void disableInputs() {
        log.debug("PurchaseController: Disabling inputs and resetting product fields");
        resetProductField();
        cancelPurchase.setDisable(true);
        submitPurchase.setDisable(true);
        newPurchase.setDisable(false);
        disableProductField(true);
    }

    private void fillInputsBySelectedStockItem() {
        StockItem stockItem = dao.findStockItem(Long.parseLong(barCodeField.getText()));
        if (stockItem != null) {
            productNameComboBox.setValue(stockItem.getName());
            priceField.setText(String.format("%.2f", stockItem.getPrice()));
        }
    }

    // Search the warehouse for a StockItem with the bar code entered
    // to the barCode textfield.
    private StockItem getStockItemByBarcode() {
        log.debug("Attempting to get stock item by barcode");
        try {
            long code = Long.parseLong(barCodeField.getText());
            StockItem item = dao.findStockItem(code);
            if (item == null) {
                log.debug("No stock item found for barcode: " + code);
            }
            return item;
        } catch (NumberFormatException e) {
            log.warn("Invalid barcode format: " + barCodeField.getText(), e);
            return null;
        }
    }

    /**
     * Add new item to the cart.
     */
    @FXML
    public void addItemEventHandler() {
        log.info("Adding new item to cart");
        StockItem stockItem = getStockItemByBarcode();

        if (stockItem != null) {
            int quantity;
            try {
                quantity = Integer.parseInt(quantityField.getText());
            } catch (NumberFormatException e) {
                log.warn("Invalid quantity format for item " + stockItem.getName() + ", defaulting to 1", e);
                quantity = 1;
            }
            shoppingCart.addItem(new SoldItem(stockItem, quantity));
            purchaseTableView.refresh();
            FXCollections.sort(purchaseTableView.getItems(), Comparator.comparing(SoldItem::getQuantity));
            log.info("Item added: " + stockItem.getName() + ", Quantity: " + quantity);
        } else {
            log.warn("No stock item found for the entered barcode");
        }
    }

    /**
     * Sets whether or not the product component is enabled.
     */
    private void disableProductField(boolean disable) {
        log.debug("PurchaseController: Setting product fields to " + (disable ? "disabled" : "enabled"));
        this.addItemButton.setDisable(disable);
        this.barCodeField.setDisable(disable);
        this.quantityField.setDisable(disable);
        this.productNameComboBox.setDisable(disable);
        this.priceField.setDisable(disable);
    }

    /**
     * Reset dialog fields.
     */
    private void resetProductField() {
        log.debug("PurchaseController: Resetting product fields to default values");
        barCodeField.setText("");
        quantityField.setText("1");
        productNameComboBox.setValue(null);
        priceField.setText("");
    }
}
