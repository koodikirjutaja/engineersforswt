package ee.ut.math.tvt.salessystem.dao;
import ee.ut.math.tvt.salessystem.dataobjects.Purchase;
import ee.ut.math.tvt.salessystem.dataobjects.SoldItem;
import ee.ut.math.tvt.salessystem.dataobjects.StockItem;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.Persistence;
import java.util.ArrayList;
import java.util.List;

public class HibernateSalesSystemDAO implements SalesSystemDAO {
    private final EntityManagerFactory emf;
    private final EntityManager em;
    public HibernateSalesSystemDAO () {
// if you get ConnectException / JDBCConnectionException then you
// probably forgot to start the database before starting the application
        emf = Persistence.createEntityManagerFactory ("pos");
        em = emf.createEntityManager ();
    }
    // TODO implement missing methods
    public void close () {
        em.close ();
        emf.close ();
    }

    @Override
    public List<StockItem> findStockItems() {
        List<StockItem> items = new ArrayList<>();
        return items;
    }

    @Override
    public StockItem findStockItem(long id) {
        return null;
    }

    @Override
    public StockItem findStockItem(String name) {
        return null;
    }

    @Override
    public void saveStockItem(StockItem stockItem) {

    }

    @Override
    public void setStockItem(long id, String name, String description, double price, int quantity) {

    }

    @Override
    public void setStockItem(StockItem stockItem, String name, String description, double price, int quantity) {

    }

    @Override
    public void addStockItem(String itemName, String itemDescription, String itemPriceStr, String itemQuantityStr) {

    }

    @Override
    public void addStockItem(StockItem item) {

    }

    @Override
    public void addExistingStockItem(StockItem stockItem, String itemName, String itemDescription, String itemPriceStr, String itemQuantityStr) {

    }

    @Override
    public void addNewStockItem(String itemName, String itemDescription, String itemPriceStr, String itemQuantityStr) {

    }

    @Override
    public void checkInputFields(String itemName, String itemDescription, String itemPriceStr, String itemQuantityStr) {

    }

    @Override
    public double getPriceFromString(String itemPriceStr) {
        return 0;
    }

    @Override
    public int getNewStockItemQuantity(StockItem stockItem, int changeInQuantity) {
        return 0;
    }

    @Override
    public int getQuantityFromString(String itemQuantityStr) {
        return 0;
    }

    @Override
    public void saveSoldItem(SoldItem item) {

    }

    @Override
    public void beginTransaction () {
        em.getTransaction (). begin ();
    }
    @Override
    public void rollbackTransaction () {
        em.getTransaction (). rollback ();
    }
    @Override
    public void commitTransaction () {
        em.getTransaction (). commit ();
    }

    @Override
    public List<SoldItem> findSoldItems() {
        return null;
    }

    @Override
    public void savePurchase(Purchase purchase) {
        beginTransaction();
        try {
            em.persist(purchase);
            commitTransaction();
        } catch (Exception e){
            rollbackTransaction();
            throw e;
        }

    }

    @Override
    public List<Purchase> findPurchase() {
        return null;
    }
}
