package ee.ut.math.tvt.salessystem.dao;

import ee.ut.math.tvt.salessystem.dataobjects.Purchase;
import ee.ut.math.tvt.salessystem.dataobjects.SoldItem;
import ee.ut.math.tvt.salessystem.dataobjects.StockItem;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.Persistence;
import javax.persistence.Query;
import java.util.ArrayList;
import java.util.List;

public class HibernateSalesSystemDAO implements SalesSystemDAO {
    private final EntityManagerFactory emf;
    private final EntityManager em;

    public HibernateSalesSystemDAO() {
// if you get ConnectException / JDBCConnectionException then you
// probably forgot to start the database before starting the application
        emf = Persistence.createEntityManagerFactory("pos");
        em = emf.createEntityManager();
    }

    // TODO implement missing methods
    public void close() {
        em.close();
        emf.close();
    }

    @Override
    public List<StockItem> findStockItems() {
        Query query = em.createQuery("SELECT s FROM StockItem s", StockItem.class);
        return query.getResultList();
    }

    @Override
    public StockItem findStockItem(long id) {
        return em.find(StockItem.class, id);

    }

    @Override
    public StockItem findStockItem(String name) {
        Query query = em.createQuery("SELECT s FROM StockItem s WHERE s.name = :name", StockItem.class);
        query.setParameter("name", name);
        List<StockItem> items = query.getResultList();
        return items.isEmpty() ? null : items.get(0);
    }

    @Override
    public void saveStockItem(StockItem stockItem) {
        em.getTransaction().begin();
        em.persist(stockItem);
        em.getTransaction().commit();
    }

    @Override
    public void setStockItem(long id, String name, String description, double price, int quantity) {
        StockItem stockItem = em.find(StockItem.class, id);
        if (stockItem != null) {
            em.getTransaction().begin();
            stockItem.setName(name);
            stockItem.setDescription(description);
            stockItem.setPrice(price);
            stockItem.setQuantity(quantity);
            em.merge(stockItem);
            em.getTransaction().commit();
        }
    }

    @Override
    public void setStockItem(StockItem stockItem, String name, String description, double price, int quantity) {
        em.getTransaction().begin();
        stockItem.setName(name);
        stockItem.setDescription(description);
        stockItem.setPrice(price);
        stockItem.setQuantity(quantity);
        em.merge(stockItem);
        em.getTransaction().commit();
    }

    @Override
    public void addStockItem(String itemName, String itemDescription, String itemPriceStr, String itemQuantityStr) {
        StockItem stockItem = findStockItem(itemName);
        if (stockItem != null) {
            addExistingStockItem(stockItem, itemName, itemDescription, itemPriceStr, itemQuantityStr);
        } else {
            addNewStockItem(itemName, itemDescription, itemPriceStr, itemQuantityStr);
        }
    }

    @Override
    public void addStockItem(StockItem item) {
        em.getTransaction().begin();
        em.persist(item);
        em.getTransaction().commit();
    }

    @Override
    public void addExistingStockItem(StockItem stockItem, String itemName, String itemDescription, String itemPriceStr, String itemQuantityStr) {
        if (stockItem == null) {
            throw new IllegalArgumentException("The provided stockItem cannot be null");
        }

        double price = getPriceFromString(itemPriceStr);
        int quantity = getQuantityFromString(itemQuantityStr);

        em.getTransaction().begin();
        try {
            if (itemName != null && !itemName.isEmpty()) {
                stockItem.setName(itemName);
            }
            if (itemDescription != null && !itemDescription.isEmpty()) {
                stockItem.setDescription(itemDescription);
            }
            if (itemPriceStr != null && !itemPriceStr.isEmpty()) {
                stockItem.setPrice(price);
            }
            if (itemQuantityStr != null && !itemQuantityStr.isEmpty()) {
                stockItem.setQuantity(quantity);
            }
            em.merge(stockItem); // This will update the stockItem since it's already managed by the EntityManager
            em.getTransaction().commit();
        } catch (RuntimeException e) {
            em.getTransaction().rollback();
            throw e;
        }
    }

    @Override
    public void addNewStockItem(String itemName, String itemDescription, String itemPriceStr, String itemQuantityStr) {
        double price = getPriceFromString(itemPriceStr);
        int quantity = getQuantityFromString(itemQuantityStr);
        StockItem stockItem = new StockItem(null, itemName, itemDescription, price, quantity);
        saveStockItem(stockItem);
    }

    @Override
    public void checkInputFields(String itemName, String itemDescription, String itemPriceStr, String itemQuantityStr) {

    }

    @Override
    public double getPriceFromString(String itemPriceStr) {
        try {
            return Double.parseDouble(itemPriceStr);
        } catch (NumberFormatException e) {
            throw new IllegalArgumentException("Price must be a valid number.");
        }
    }

    @Override
    public int getNewStockItemQuantity(StockItem stockItem, int changeInQuantity) {
        return stockItem.getQuantity() + changeInQuantity;
    }

    @Override
    public int getQuantityFromString(String itemQuantityStr) {
        try {
            return Integer.parseInt(itemQuantityStr);
        } catch (NumberFormatException e) {
            throw new IllegalArgumentException("Quantity must be a valid integer.");
        }
    }

    @Override
    public void saveSoldItem(SoldItem item) {
        em.getTransaction().begin();
        em.persist(item);
        em.getTransaction().commit();
    }

    @Override
    public void beginTransaction() {
        em.getTransaction().begin();
    }

    @Override
    public void rollbackTransaction() {
        em.getTransaction().rollback();
    }

    @Override
    public void commitTransaction() {
        em.getTransaction().commit();
    }

    @Override
    public List<SoldItem> findSoldItems() {
        Query query = em.createQuery("SELECT s FROM SoldItem s", SoldItem.class);
        return query.getResultList();
    }

    @Override
    public void savePurchase(Purchase purchase) {
        beginTransaction();
        try {
            em.persist(purchase);
            commitTransaction();
        } catch (Exception e) {
            rollbackTransaction();
            throw e;
        }

    }

    @Override
    public List<Purchase> findPurchase() {
        Query query = em.createQuery("SELECT p FROM Purchase p", Purchase.class);
        return query.getResultList();
    }
}
