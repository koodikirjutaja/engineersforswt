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
import javafx.scene.control.Button;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.net.URL;
import java.util.ResourceBundle;

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
    private TextField nameField;
    @FXML
    private TextField priceField;
    @FXML
    private Button addItemButton;
    @FXML
    private TableView<SoldItem> purchaseTableView;

    public PurchaseController(SalesSystemDAO dao, ShoppingCart shoppingCart) {
        this.dao = dao;
        this.shoppingCart = shoppingCart;
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        log.info("Initializing PurchaseController");

        // Disabling 'cancel' and 'submit' buttons initially
        cancelPurchase.setDisable(true);
        submitPurchase.setDisable(true);
        log.debug("Cancel and Submit buttons disabled");

        // Setting items in the purchase table view from the shopping cart
        purchaseTableView.setItems(FXCollections.observableList(shoppingCart.getAll()));
        log.debug("Purchase table view initialized with items from shopping cart");

        // Disabling product input fields initially
        disableProductField(true);
        log.debug("Product input fields disabled");

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

    /** Event handler for the <code>new purchase</code> event. */
    @FXML
    protected void newPurchaseButtonClicked() {
        log.info("Initiating new sale process");
        try {
            enableInputs();
        } catch (SalesSystemException e) {
            log.error("Error encountered during new sale process: " + e.getMessage(), e);
        }
        log.info("New sale process ready");
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
        log.debug("Filling info by selected stock item");
        StockItem stockItem = getStockItemByBarcode();
        if (stockItem != null) {
            nameField.setText(stockItem.getName());
            priceField.setText(String.valueOf(stockItem.getPrice()));
            log.debug("Stock item details set - Name: " + stockItem.getName() + ", Price: " + stockItem.getPrice());
        } else {
            resetProductField();
            log.debug("No stock item found, reset product field");
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
        this.nameField.setDisable(disable);
        this.priceField.setDisable(disable);
    }

    /**
     * Reset dialog fields.
     */
    private void resetProductField() {
        log.debug("PurchaseController: Resetting product fields to default values");
        barCodeField.setText("");
        quantityField.setText("1");
        nameField.setText("");
        priceField.setText("");
    }
}
