package ee.ut.math.tvt.salessystem.ui;

import ee.ut.math.tvt.salessystem.dao.HibernateSalesSystemDAO;
import ee.ut.math.tvt.salessystem.dao.SalesSystemDAO;
import ee.ut.math.tvt.salessystem.dao.InMemorySalesSystemDAO;
import ee.ut.math.tvt.salessystem.ui.controllers.HistoryController;
import ee.ut.math.tvt.salessystem.ui.controllers.PurchaseController;
import ee.ut.math.tvt.salessystem.ui.controllers.StockController;
import ee.ut.math.tvt.salessystem.logic.ShoppingCart;
import ee.ut.math.tvt.salessystem.ui.controllers.TeamTabController;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Group;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.Tab;
import javafx.scene.control.TabPane;
import javafx.scene.layout.BorderPane;
import javafx.scene.paint.Color;
import javafx.stage.Stage;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.IOException;
import java.net.URL;

/**
 * Graphical user interface of the sales system.
 */
public class SalesSystemUI extends Application {


    private static final Logger log = LogManager.getLogger(SalesSystemUI.class);

    private final SalesSystemDAO dao;
    private final ShoppingCart shoppingCart;

    public SalesSystemUI() {
      //  dao = new InMemorySalesSystemDAO();
        dao = new HibernateSalesSystemDAO();
        shoppingCart = new ShoppingCart(dao);
    }

    @Override
    public void start(Stage primaryStage) throws Exception {
        log.info("javafx version: " + System.getProperty("javafx.runtime.version"));
        log.info("SalesSystemUI: Starting");

        log.info("Creating Point-of-sale tab");
        Tab purchaseTab = new Tab();
        purchaseTab.setText("Point-of-sale");
        purchaseTab.setClosable(false);
        purchaseTab.setContent(loadControls("PurchaseTab.fxml", new PurchaseController(dao, shoppingCart)));
        log.info("Created Point-of-sale tab");

        log.info("Creating Warehouse tab");
        Tab stockTab = new Tab();
        stockTab.setText("Warehouse");
        stockTab.setClosable(false);
        stockTab.setContent(loadControls("StockTab.fxml", new StockController(dao)));
        log.info("Created Warehouse tab");

        log.info("Creating History tab");
        Tab historyTab = new Tab();
        historyTab.setText("History");
        historyTab.setClosable(false);
        historyTab.setContent(loadControls("HistoryTab.fxml", new HistoryController(dao)));
        log.info("Created History tab");

        log.info("Creating Team tab");
        Tab teamTab = new Tab();
        teamTab.setText("Team");
        teamTab.setClosable(false);
        teamTab.setContent(loadControls("TeamTab.fxml", new TeamTabController()));
        log.info("Created Team tab");


        Group root = new Group();
        Scene scene = new Scene(root, 600, 500, Color.WHITE);
        //scene.getStylesheets().add(getClass().getResource("DefaultTheme.css").toExternalForm());

        BorderPane borderPane = new BorderPane();
        borderPane.prefHeightProperty().bind(scene.heightProperty());
        borderPane.prefWidthProperty().bind(scene.widthProperty());
        borderPane.setCenter(new TabPane(purchaseTab, stockTab, historyTab, teamTab));
        root.getChildren().add(borderPane);

        scene.getStylesheets().add(getClass().getResource("DefaultTheme.css").toExternalForm());
        primaryStage.setTitle("Sales system");
        primaryStage.setScene(scene);
        primaryStage.show();

        log.info("Salesystem GUI started");
    }

    private Node loadControls(String fxml, Initializable controller) throws IOException {
        URL resource = getClass().getResource(fxml);
        if (resource == null)
            throw new IllegalArgumentException(fxml + " not found");

        FXMLLoader fxmlLoader = new FXMLLoader(resource);
        fxmlLoader.setController(controller);
        return fxmlLoader.load();
    }
}


