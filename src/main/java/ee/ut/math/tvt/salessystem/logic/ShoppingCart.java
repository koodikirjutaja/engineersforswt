package ee.ut.math.tvt.salessystem.logic;

import ee.ut.math.tvt.salessystem.dao.SalesSystemDAO;
import ee.ut.math.tvt.salessystem.dataobjects.SoldItem;
import ee.ut.math.tvt.salessystem.dataobjects.StockItem;

import java.util.ArrayList;
import java.util.List;

public class ShoppingCart {

    private final SalesSystemDAO dao;
    private final List<SoldItem> items = new ArrayList<>();

    public ShoppingCart(SalesSystemDAO dao) {
        this.dao = dao;
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
        // Starting a transaction (no-op for in-memory DAO)
        dao.beginTransaction();
        try {
            for (SoldItem soldItem : items) {
                // Find the corresponding stock item in the warehouse
                StockItem stockItem = dao.findStockItem(soldItem.getStockItem().getId());
                if (stockItem != null) {
                    // Decrease the quantity of the stock item in the warehouse
                    int newQuantity = stockItem.getQuantity() - soldItem.getQuantity();
                    if (newQuantity >= 0) {
                        dao.setStockItem(stockItem.getId(), stockItem.getName(), stockItem.getDescription(), stockItem.getPrice(), newQuantity);
                        // Save the sold item
                        dao.saveSoldItem(soldItem);
                    } else {
                        throw new IllegalStateException("Insufficient stock for item: " + soldItem.getName());
                    }
                } else {
                    throw new IllegalStateException("Item not found in stock: " + soldItem.getName());
                }
            }
            // Committing the transaction (no-op for in-memory DAO)
            dao.commitTransaction();
            // Clearing the items from the shopping cart
            items.clear();
        } catch (Exception e) {
            // Rolling back the transaction in case of an exception (no-op for in-memory DAO)
            dao.rollbackTransaction();
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
