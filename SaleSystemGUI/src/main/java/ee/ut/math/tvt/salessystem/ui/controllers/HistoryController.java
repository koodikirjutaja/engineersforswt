

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
import ee.ut.math.tvt.salessystem.dataobjects.SoldItem;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.DatePicker;
import javafx.scene.control.TableView;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.net.URL;
import java.util.ResourceBundle;

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
    private TableView<SoldItem> historyTableView;

    public HistoryController(SalesSystemDAO dao) {
        this.dao = dao;
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        log.debug("HistoryController-initialize");
        // Initialization logic here.
        // For example, you might want to load all history items by default.
        loadAllHistoryItems();
        log.debug("HistoryController-initialize-loadAllHistoryItems");
    }

    @FXML
    protected void showBetweenDatesButtonClicked() {
        log.debug("HistoryController-showBetweenDatesButtonClicked");
        log.info("Showing history between selected dates");
        // Implement logic to filter and display history items between the selected dates
    }

    @FXML
    protected void showLast10ButtonClicked() {
        log.debug("HistoryController-showLast10ButtonClicked");
        log.info("Showing last 10 history items");
        // Implement logic to display the last 10 history items
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
        // Implement logic to fetch and display all history items.
        // historyTableView.setItems( ... );
    }
}

