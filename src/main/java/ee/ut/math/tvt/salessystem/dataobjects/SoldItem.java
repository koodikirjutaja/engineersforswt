package ee.ut.math.tvt.salessystem.dataobjects;


import javax.persistence.*;

/**
 * Already bought StockItem. SoldItem duplicates name and price for preserving history.
 */
@Entity
@Table(name = "SoldItem")
public class SoldItem {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Column(name = "purchaseId")
    private Long purchaseId;
    @ManyToOne
    @JoinColumn(name = "stockItemId",referencedColumnName = "id")
    private StockItem stockItem;
    @Column(name = "name")
    private String name;
    @Column(name="quantity")
    private Integer quantity;
    @Column(name="price")
    private double price;

    public SoldItem() {
    }

    public SoldItem(StockItem stockItem, int quantity) {
        this.id = stockItem.getId();
        this.stockItem = stockItem;
        this.name = stockItem.getName();
        this.price = stockItem.getPrice();
        this.quantity = quantity;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getPurchaseId(){return purchaseId;}

    public void setPurchaseId(Long purchaseId){this.purchaseId = purchaseId;}

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public double getPrice() {
        return price;
    }

    public void setPrice(double price) {
        this.price = price;
    }

    public Integer getQuantity() {
        return quantity;
    }

    public void setQuantity(Integer quantity) {
        this.quantity = quantity;
    }

    public double getSum() {
        return price * ((double) quantity);
    }

    public StockItem getStockItem() {
        return stockItem;
    }

    public void setStockItem(StockItem stockItem) {
        this.stockItem = stockItem;
    }

    @Override
    public String toString() {
        return String.format("SoldItem{id=%d, name='%s'}", id, name);
    }
}
