package transaction.domain;

import javax.persistence.*;
import lombok.Data;
import transaction.InventoryApplication;

@Entity
@Table(name = "Inventory_table")
@Data
public class Inventory {

    @Id
    @GeneratedValue(strategy=GenerationType.AUTO)
    private Long id;
    private Long stock;

    @PostUpdate
    public void onPostUpdate(){
    }

    public static InventoryRepository repository(){
        InventoryRepository inventoryRepository = InventoryApplication.applicationContext.getBean(InventoryRepository.class);
        return inventoryRepository;
    }

    // 순수 재고차감 도메인 로직
    public InventoryChanged decreaseStock(Integer qty) {
        if (this.stock < qty) {
            throw new RuntimeException("재고가 부족합니다");
        }
        
        this.stock -= qty;
        return new InventoryChanged(this);
    }

}
