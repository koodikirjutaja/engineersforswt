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
        log.info("PurchaseController initializing");
        log.debug("PurchaseController-initialize");
        cancelPurchase.setDisable(true);
        log.debug("PurchaseController-initialize-cancelPurchase-setDisable: true");
        submitPurchase.setDisable(true);
        log.debug("PurchaseController-initialize-submitPurchase-setDisable: true");
        purchaseTableView.setItems(FXCollections.observableList(shoppingCart.getAll()));
        log.debug("PurchaseController-initialize-setItems");
        disableProductField(true);
        log.debug("PurchaseController-initialize-disableProductField: true");

        this.barCodeField.focusedProperty().addListener(new ChangeListener<Boolean>() {
            @Override
            public void changed(ObservableValue<? extends Boolean> arg0, Boolean oldPropertyValue, Boolean newPropertyValue) {
                if (!newPropertyValue) {
                    log.debug("PurchaseController-initialize-changed");
                    fillInputsBySelectedStockItem();
                    log.debug("PurchaseController-initialize-changed-fillInputsBySelectedStockItem");
                }
            }
        });
        log.info("PurchaseController initialized");
    }

    /** Event handler for the <code>new purchase</code> event. */
    @FXML
    protected void newPurchaseButtonClicked() {
        log.info("New sale process started");
        log.debug("PurchaseController-newPurchaseButtonClicked");
        try {
            enableInputs();
            log.debug("PurchaseController-enableInputs");
        } catch (SalesSystemException e) {
            log.error(e.getMessage(), e);
        }
        log.info("New sale process ended");
    }

    /**
     * Event handler for the <code>cancel purchase</code> event.
     */
    @FXML
    protected void cancelPurchaseButtonClicked() {
        log.info("Sale cancelled");
        log.debug("PurchaseController-cancelPurchaseButtonClicked");
        try {
            shoppingCart.cancelCurrentPurchase();
            log.debug("PurchaseController-cancelPurchaseButtonClicked-cancelCurrentPurchase");
            disableInputs();
            log.debug("PurchaseController-cancelPurchaseButtonClicked-disableInputs");
            purchaseTableView.refresh();
            log.debug("PurchaseController-cancelPurchaseButtonClicked-refresh");
        } catch (SalesSystemException e) {
            log.error(e.getMessage(), e);
        }
    }

    /**
     * Event handler for the <code>submit purchase</code> event.
     */
    @FXML
    protected void submitPurchaseButtonClicked() {
        log.info("Sale complete");
        log.debug("PurchaseController-submitPurchaseButtonClicked");
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
        log.debug("PurchaseController: Enabling inputs");
        resetProductField();
        disableProductField(false);
        cancelPurchase.setDisable(false);
        submitPurchase.setDisable(false);
        newPurchase.setDisable(true);
        log.debug("PurchaseController: Enabled inputs");
    }

    // switch UI to the state that allows to initiate new purchase
    private void disableInputs() {
        log.debug("PurchaseController: Disabling inputs");
        resetProductField();
        cancelPurchase.setDisable(true);
        submitPurchase.setDisable(true);
        newPurchase.setDisable(false);
        disableProductField(true);
        log.debug("PurchaseController: Disabled inputs");
    }

    private void fillInputsBySelectedStockItem() {
        log.info("Filling info by selected stock item");
        log.debug("PurchaseController-fillInputsBySelectedStockItem");
        StockItem stockItem = getStockItemByBarcode();
        log.debug("PurchaseController-fillInputsBySelectedStockItem-getStockItemByBarcode");
        if (stockItem != null) {
            nameField.setText(stockItem.getName());
            log.debug("PurchaseController-fillInputsBySelectedStockItem-setText");
            priceField.setText(String.valueOf(stockItem.getPrice()));
            log.debug("PurchaseController-fillInputsBySelectedStockItem-setText");
        } else {
            resetProductField();
            log.debug("PurchaseController-fillInputsBySelectedStockItem-resetProductField");
        }
    }

    // Search the warehouse for a StockItem with the bar code entered
    // to the barCode textfield.
    private StockItem getStockItemByBarcode() {
        log.info("Getting stock item by barcode");
        log.debug("PurchaseController-getStockItemByBarcode");
        try {
            long code = Long.parseLong(barCodeField.getText());
            log.debug("PurchaseController-getStockItemByBarcode: Got code");
            return dao.findStockItem(code);
        } catch (NumberFormatException e) {
            log.error("PurchaseController-getStockItemByBarcode: " + e.getMessage(), e);
            return null;
        }
    }

    /**
     * Add new item to the cart.
     */
    @FXML
    public void addItemEventHandler() {
        log.info("Adding new item to cart");
        log.debug("PurchaseController-addItemEventHandler");
        // add chosen item to the shopping cart.
        StockItem stockItem = getStockItemByBarcode();
        log.debug("PurchaseController-addItemEventHandler-getStockItemByBarcode");
        if (stockItem != null) {
            int quantity;
            try {
                quantity = Integer.parseInt(quantityField.getText());
                log.debug("PurchaseController-addItemEventHandler: Got quantity");
            } catch (NumberFormatException e) {
                quantity = 1;
                log.debug("PurchaseController-addItemEventHandler: Quantity = 1");
            }
            shoppingCart.addItem(new SoldItem(stockItem, quantity));
            log.debug("PurchaseController-addItemEventHandler-addItem");
            purchaseTableView.refresh();
            log.debug("PurchaseController-addItemEventHandler-refresh");
        }
    }

    /**
     * Sets whether or not the product component is enabled.
     */
    private void disableProductField(boolean disable) {
        log.debug("PurchaseController: Disabling product field");
        this.addItemButton.setDisable(disable);
        this.barCodeField.setDisable(disable);
        this.quantityField.setDisable(disable);
        this.nameField.setDisable(disable);
        this.priceField.setDisable(disable);
        log.debug("PurchaseController: Disabled product field");
    }

    /**
     * Reset dialog fields.
     */
    private void resetProductField() {
        log.debug("PurchaseController: Resetting product field");
        barCodeField.setText("");
        quantityField.setText("1");
        nameField.setText("");
        priceField.setText("");
        log.debug("PurchaseController: Reset product field");
    }
}
