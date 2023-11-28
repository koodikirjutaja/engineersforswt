package ee.ut.math.tvt.salessystem.logic;

import ee.ut.math.tvt.salessystem.dao.HibernateSalesSystemDAO;
import ee.ut.math.tvt.salessystem.dao.SalesSystemDAO;
import ee.ut.math.tvt.salessystem.dataobjects.Purchase;
import ee.ut.math.tvt.salessystem.dataobjects.SoldItem;
import ee.ut.math.tvt.salessystem.dataobjects.StockItem;
import org.hibernate.Session;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class ShoppingCart {

    private final SalesSystemDAO dao;

    private Long currentPurchaseId;
    private final List<SoldItem> items = new ArrayList<>();

    public ShoppingCart(SalesSystemDAO dao) {
        this.dao = dao;
        currentPurchaseId = (long) dao.findPurchase().size();
    }

    /**
     * Add new SoldItem to table.
     */
    public void addItem(SoldItem item) {


        // Find the item in the cart with the same ID as the item to be added
        SoldItem existingItem = items.stream()
                .filter(i -> i.getStockItem().getId() == item.getStockItem().getId())
                .findFirst()
                .orElse(null);

        if (existingItem != null) {
            // Increase the quantity of the existing item
            int newQuantity = existingItem.getQuantity() + item.getQuantity();
            // Check if the warehouse has enough stock
            if (newQuantity <= dao.findStockItem(existingItem.getStockItem().getId()).getQuantity()) {
                existingItem.setQuantity(newQuantity);
            } else {
                throw new IllegalArgumentException("Not enough stock for item: " + item.getName());
            }
        } else {
            // If the item is not in the cart, check stock availability before adding
            if (item.getQuantity() <= dao.findStockItem(item.getStockItem().getId()).getQuantity()) {
                items.add(item);
            } else {
                throw new IllegalArgumentException("Not enough stock for item: " + item.getName());
            }
        }

        // Log the addition
        //log.debug("Added " + item.getName() + " quantity of " + item.getQuantity());
    }


    public List<SoldItem> getAll() {
        return items;
    }

    public void cancelCurrentPurchase() {
        items.clear();
    }

    public void submitCurrentPurchase() {
        try {
            Purchase purchase = new Purchase(currentPurchaseId, LocalDateTime.now(), new ArrayList<>(items));

            // Business logic for updating stock and associating purchase with sold items
            for (SoldItem soldItem : purchase.getItems()) {
                soldItem.addPurchase(purchase);
                StockItem stockItem = dao.findStockItem(soldItem.getStockItem().getId());
                if (stockItem != null) {
                    int newQuantity = stockItem.getQuantity() - soldItem.getQuantity();
                    if (newQuantity >= 0) {
                        stockItem.setQuantity(newQuantity);
                    } else {
                        throw new IllegalStateException("Insufficient stock for item: " + soldItem.getName());
                    }
                } else {
                    throw new IllegalStateException("Item not found in stock: " + soldItem.getName());
                }
            }

            // Saving the Purchase object (with updated stock)
            dao.savePurchase(purchase);

            // Clearing the items from the shopping cart
            items.clear();
            currentPurchaseId++;
        } catch (Exception e) {
            throw e; // Re-throwing the exception to indicate failure
        }
    }


    public void removeItem(SoldItem item) {
        items.remove(item);
        // Update the stock TODO
        // For example, if the ShoppingCart keeps track of stock quantities,
        // this would be a good place to increment the stock quantity back.
    }

    public double calculateTotalSum() {
        double totalSum = 0.0;
        for (SoldItem item : items) {
            totalSum += item.getPrice() * item.getQuantity();
        }
        return totalSum;
    }

}
