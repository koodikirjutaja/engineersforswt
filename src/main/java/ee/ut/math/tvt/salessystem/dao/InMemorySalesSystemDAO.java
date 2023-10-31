package ee.ut.math.tvt.salessystem.dao;

import ee.ut.math.tvt.salessystem.FieldFormatException;
import ee.ut.math.tvt.salessystem.dataobjects.SoldItem;
import ee.ut.math.tvt.salessystem.dataobjects.StockItem;

import java.util.ArrayList;
import java.util.List;

public class InMemorySalesSystemDAO implements SalesSystemDAO {

    private final List<StockItem> stockItemList;
    private final List<SoldItem> soldItemList;

    public InMemorySalesSystemDAO() {
        List<StockItem> items = new ArrayList<StockItem>();
        items.add(new StockItem(1L, "Lays chips", "Potato chips", 11.0, 5));
        items.add(new StockItem(2L, "Chupa-chups", "Sweets", 8.0, 8));
        items.add(new StockItem(3L, "Frankfurters", "Beer sauseges", 15.0, 12));
        items.add(new StockItem(4L, "Free Beer", "Student's delight", 0.0, 100));
        this.stockItemList = items;
        this.soldItemList = new ArrayList<>();
    }

    @Override
    public List<StockItem> findStockItems() {
        return stockItemList;
    }

    @Override
    public StockItem findStockItem(long id) {
        for (StockItem item : stockItemList) {
            if (item.getId() == id)
                return item;
        }
        return null;
    }

    @Override
    public StockItem findStockItem(String name) {
        for (StockItem item : stockItemList) {
            if (item.getName().equals(name))
                return item;
        }
        return null;
    }

    @Override
    public void saveSoldItem(SoldItem item) {
        soldItemList.add(item);
    }

    @Override
    public void saveStockItem(StockItem stockItem) {
        stockItemList.add(stockItem);
    }

    @Override
    public void setStockItem(long id, String name, String description, double price, int quantity) {
        StockItem stockItem = findStockItem(id);
        stockItem.setName(name);
        stockItem.setDescription(description);
        stockItem.setQuantity(quantity);
        stockItem.setPrice(price);
    }

    @Override
    public void setStockItem(StockItem stockItem, String name, String description, double price, int quantity) {
        stockItem.setName(name);
        stockItem.setDescription(description);
        stockItem.setQuantity(quantity);
        stockItem.setPrice(price);
    }

    @Override
    public void addStockItem(String itemName,
                             String itemDescription,
                             String itemPriceStr,
                             String itemQuantityStr) {
        if (itemName.equals(""))
            throw new FieldFormatException("Item must have a name!");
        StockItem stockItem = findStockItem(itemName);
        if (stockItem != null) {
            addExistingStockItem(stockItem, itemName, itemDescription, itemPriceStr, itemQuantityStr);
        }
        else {
            addNewStockItem(itemName, itemDescription, itemPriceStr, itemQuantityStr);
        }
    }
    @Override
    public void addExistingStockItem(StockItem stockItem,
                                     String itemName,
                                     String itemDescription,
                                     String itemPriceStr,
                                     String itemQuantityStr) {
        double itemPrice;
        int itemQuantity;
        if (itemPriceStr.equals("")) {
            itemPrice = stockItem.getPrice();
        } else {
            itemPrice = getPriceFromString(itemPriceStr);
        }
        if (itemQuantityStr.equals("")) {
            itemQuantity = stockItem.getQuantity();
        } else {
            itemQuantity = getNewStockItemQuantity(
                    stockItem,
                    getQuantityFromString(itemQuantityStr)
            );
        }
        if (itemDescription.equals("")) {
            itemDescription = stockItem.getDescription();
        }
        setStockItem(stockItem, itemName, itemDescription, itemPrice, itemQuantity);
    }
    @Override
    public void addNewStockItem(String itemName,
                                String itemDescription,
                                String itemPriceStr,
                                String itemQuantityStr){
        long barCode = findStockItems().size()+1;
        checkInputFields(itemName,
                        itemDescription,
                        itemPriceStr,
                        itemQuantityStr);
        double itemPrice = Double.parseDouble(itemPriceStr);
        int itemQuantity = Integer.parseInt(itemQuantityStr);
        if (itemPrice < 0)
            throw new FieldFormatException("Item price must not be negative");
        if (itemQuantity < 0)
            throw new FieldFormatException("Item quantity must not be negative");
        StockItem stockItem = new StockItem(barCode, itemName, itemDescription, itemPrice, itemQuantity);
        saveStockItem(stockItem);
    }
    @Override
    public void checkInputFields(String itemName,
                                 String itemDescription,
                                 String itemPriceStr,
                                 String itemQuantityStr) {
        if (itemName.equals(""))
            throw new FieldFormatException("Item must have a name!");
        if (itemDescription.equals(""))
            throw new FieldFormatException("Item must have a description!");
        if (!itemPriceStr.matches("[0-9]+(\\.[0-9]+){0,1}"))
            throw new FieldFormatException("Item price is not a number");
        if (!itemQuantityStr.matches("[0-9]+(\\.[0-9]+){0,1}"))
            throw new FieldFormatException("Item quantity is not a number");
    }

    @Override
    public double getPriceFromString(String itemPriceStr) {
        if (!itemPriceStr.matches("[0-9]+(\\.[0-9]+){0,1}"))
            throw new FieldFormatException("Not a number");
        double itemPrice = Double.parseDouble(itemPriceStr);
        if (itemPrice < 0)
            throw new FieldFormatException("Item price must not be negative");
        return itemPrice;
    }
    @Override
    public int getNewStockItemQuantity(StockItem stockItem, int changeInQuantity) {
        int itemQuantity = stockItem.getQuantity() + changeInQuantity;
        if (itemQuantity < 0)
            throw new FieldFormatException("Item quantity must not be negative");
        return itemQuantity;
    }
    @Override
    public int getQuantityFromString(String itemQuantityStr) {
        if (!itemQuantityStr.matches("-?[0-9]+")) // luba negatiivsed arvud
            throw new FieldFormatException("Not a number");
        int changeInQuantity = Integer.parseInt(itemQuantityStr);
        return changeInQuantity;
    }

    @Override
    public void beginTransaction() {
    }

    @Override
    public void rollbackTransaction() {
    }

    @Override
    public void commitTransaction() {
    }
}
