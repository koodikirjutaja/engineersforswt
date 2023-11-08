

/**
 * Encapsulates everything that has to do with the purchase tab (the tab
 * labelled "History" in the menu).
 *
 public class HistoryController implements Initializable {

@Override
public void initialize(URL location, ResourceBundle resources) {
// TODO: implement
}
} **/
package ee.ut.math.tvt.salessystem.ui.controllers;

import ee.ut.math.tvt.salessystem.dao.SalesSystemDAO;
//import ee.ut.math.tvt.salessystem.dataobjects.HistoryItem;
import ee.ut.math.tvt.salessystem.dataobjects.Purchase;
import ee.ut.math.tvt.salessystem.dataobjects.SoldItem;
import javafx.beans.property.ReadOnlyObjectWrapper;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.net.URL;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.ResourceBundle;
import java.util.stream.Collectors;

public class HistoryController implements Initializable {

    private static final Logger log = LogManager.getLogger(HistoryController.class);

    private final SalesSystemDAO dao;

    @FXML
    private Button btnShowBetweenDates;
    @FXML
    private Button btnShowLast10;
    @FXML
    private Button btnShowAll;
    @FXML
    private DatePicker dpStartDate;
    @FXML
    private DatePicker dpEndDate;
    @FXML
    private TableView<Purchase> purchaseHistoryTable;
    @FXML
    private TableView<SoldItem> itemsTable;

    @FXML
    private TableColumn<Purchase, String> dateColumn;

    @FXML
    private TableColumn<Purchase, String> timeColumn;

    @FXML
    private TableColumn<Purchase, Double> totalColumn;


    @FXML
    private TableColumn<SoldItem, Long> purchaseIdColumn;
    @FXML
    private TableColumn<SoldItem, String> nameColumn;
    @FXML
    private TableColumn<SoldItem, Double> priceColumn;
    @FXML
    private TableColumn<SoldItem, Integer> quantityColumn;
    @FXML
    private TableColumn<SoldItem, Double> sumColumn;

    public HistoryController(SalesSystemDAO dao) {
        this.dao = dao;
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        log.debug("HistoryController-initialize");
        // Initialization logic here.
        // For example, you might want to load all history items by default.

        //purchase history
        DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
        dateColumn.setCellValueFactory(cellData -> new ReadOnlyObjectWrapper<>(cellData.getValue().getPurchaseTime().format(dateFormatter)));
        DateTimeFormatter timeFormatter = DateTimeFormatter.ofPattern("HH:mm:ss.SSS");
        timeColumn.setCellValueFactory(cellData -> new ReadOnlyObjectWrapper<>(
                cellData.getValue().getPurchaseTime().format(timeFormatter)
        ));
        totalColumn.setCellValueFactory(cellData -> new ReadOnlyObjectWrapper<>(cellData.getValue().calculateTotalSum()));

        //items
        purchaseIdColumn.setCellValueFactory(new PropertyValueFactory<>("purchaseId"));
        nameColumn.setCellValueFactory(new PropertyValueFactory<>("name"));
        priceColumn.setCellValueFactory(new PropertyValueFactory<>("price"));
        quantityColumn.setCellValueFactory(new PropertyValueFactory<>("quantity"));
        sumColumn.setCellValueFactory(cellData -> new ReadOnlyObjectWrapper<>(cellData.getValue().getSum()));

        purchaseHistoryTable.getSelectionModel().selectedItemProperty().addListener((obs, oldSelection, newSelection) -> {
            if (newSelection != null) {
                // Filter and display only the items related to the selected purchase
                displayItemsForSelectedPurchase(newSelection);
            }
        });

        loadAllHistoryItems();
        log.debug("HistoryController-initialize-loadAllHistoryItems");
    }

    @FXML
    protected void showBetweenDatesButtonClicked() {
        log.debug("HistoryController-showBetweenDatesButtonClicked");
        log.info("Showing history between selected dates");
        // Implement logic to filter and display history items between the selected dates

        LocalDateTime startDateTime = dpStartDate.getValue() != null ? dpStartDate.getValue().atStartOfDay() : null;
        LocalDateTime endDateTime = dpEndDate.getValue() != null ? dpEndDate.getValue().atTime(23, 59, 59) : null;

        if (startDateTime != null && endDateTime != null && !startDateTime.isAfter(endDateTime)) {
            List<Purchase> filteredPurchases = dao.findPurchase().stream()
                    .filter(purchase -> !purchase.getPurchaseTime().isBefore(startDateTime) && !purchase.getPurchaseTime().isAfter(endDateTime))
                    .collect(Collectors.toList());

            purchaseHistoryTable.setItems(FXCollections.observableArrayList(filteredPurchases));
            itemsTable.setItems(null); // Clear the items table
            log.info("Filtered history items displayed");
        } else {
            log.error("Invalid date range selected");
            // Handle error for invalid date range
            // Show an alert to the user or log a message
        }
    }

    @FXML
    protected void showLast10ButtonClicked() {
        log.debug("HistoryController-showLast10ButtonClicked");
        log.info("Showing last 10 history items");
        // Implement logic to display the last 10 history items
        List<Purchase> sortedPurchases = dao.findPurchase().stream() // Use findPurchase here
                .sorted((p1, p2) -> p2.getPurchaseTime().compareTo(p1.getPurchaseTime()))
                .limit(10)
                .collect(Collectors.toList());

        purchaseHistoryTable.setItems(FXCollections.observableArrayList(sortedPurchases));
        itemsTable.setItems(null); // Clearing the items table

    }

    @FXML
    protected void showAllButtonClicked() {
        log.debug("HistoryController-showAllButtonClicked");
        log.info("Showing all history items");
        loadAllHistoryItems();
        log.debug("HistoryController-showAllButtonClicked-loadAllHistoryItems");
    }

    private void loadAllHistoryItems() {
        log.debug("HistoryController-loadAllHistoryItems");

            try {
                List<Purchase> purchaseList = dao.findPurchase();
                ObservableList<Purchase> purchasesObservableList = FXCollections.observableList(purchaseList);
                purchaseHistoryTable.setItems(purchasesObservableList);

               /* List<SoldItem> soldItemList = dao.findSoldItems();
                ObservableList<SoldItem> soldItemObservableList = FXCollections.observableList(soldItemList);
                itemsTable.setItems(soldItemObservableList); */

            } catch (Exception e) {
                log.error("Error loading history items", e);
            }
        }
    private void displayItemsForSelectedPurchase(Purchase selectedPurchase) {
        // Get the purchase ID from the selected purchase
        Long selectedPurchaseId = selectedPurchase.getId();

        // Filter the sold items for this purchase ID
        List<SoldItem> filteredItems = dao.findSoldItems().stream()
                .filter(item -> item.getPurchaseId().equals(selectedPurchaseId))
                .collect(Collectors.toList());

        // Update the itemsTable with the filtered items
        itemsTable.setItems(FXCollections.observableArrayList(filteredItems));
    }


}

