package ee.ut.math.tvt.salessystem.dataobjects;

import javax.persistence.*;
import java.time.LocalDateTime;
import java.util.List;

@Entity
@Table(name = "Purchase")
public class Purchase {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Column(name = "purchaseTime")
    private LocalDateTime purchaseTime;
    @OneToMany(mappedBy = "purchase")
    private List<SoldItem> items;

    public Purchase(){

    }

    public Purchase(Long id, LocalDateTime purchaseTime, List<SoldItem> items) {
        this.id = id;
        this.purchaseTime = purchaseTime;
        this.items = items;
    }

    public Long getId() {
        return id;
    }

    public LocalDateTime getPurchaseTime() {
        return purchaseTime;
    }

    public List<SoldItem> getItems() {
        return items;
    }
    // Method to calculate the total sum of the purchase
    public double calculateTotalSum() {
        double sum = 0;
        for (SoldItem item : items) {
            sum += item.getSum();
        }
        return sum;
    }
}
