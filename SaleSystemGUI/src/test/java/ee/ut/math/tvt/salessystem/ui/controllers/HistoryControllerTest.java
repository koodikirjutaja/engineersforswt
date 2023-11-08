/*
package ee.ut.math.tvt.salessystem.ui.controllers;

import ee.ut.math.tvt.salessystem.dao.SalesSystemDAO;
import ee.ut.math.tvt.salessystem.dataobjects.Purchase;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.control.TableView;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import static javafx.beans.binding.Bindings.when;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class HistoryControllerTest {

    @Mock
    private SalesSystemDAO daoMock;

    private HistoryController historyController;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        historyController = new HistoryController(daoMock);


        historyController.purchaseHistoryTable = new TableView<>();
        historyController.dpStartDate = new DatePicker();
        historyController.dpEndDate = new DatePicker();
    }

    @Test
    void testShowAllButtonClicked() {

        List<Purchase> testData = new ArrayList<>();
        testData.add(new Purchase(1L, LocalDateTime.now(), null));
        when(daoMock.findPurchase()).thenReturn(testData);


        historyController.showAllButtonClicked();


        ObservableList<Purchase> tableData = historyController.purchaseHistoryTable.getItems();
        assertNotNull(tableData);
        assertEquals(1, tableData.size());
        assertEquals(testData.get(0), tableData.get(0));
    }

    @Test
    void testShowLast10ButtonClicked() {

        List<Purchase> testData = new ArrayList<>();
        for (long i = 1; i <= 15; i++) {
            testData.add(new Purchase(i, LocalDateTime.now().minusDays(i), null));
        }
        when(daoMock.findPurchase()).thenReturn(testData);


        historyController.showLast10ButtonClicked();

        ObservableList<Purchase> tableData = historyController.purchaseHistoryTable.getItems();
        assertNotNull(tableData);
        assertEquals(10, tableData.size());
        // Verify the order is correct (recent purchases first)
        assertTrue(tableData.get(0).getId() > tableData.get(tableData.size() - 1).getId());
    }

}*/
