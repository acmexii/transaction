package transaction.domain;

import javax.persistence.*;
import lombok.Data;
import transaction.OrderApplication;

@Entity
@Table(name = "Order_table")
@Data
//<<< DDD / Aggregate Root
public class Order  {
    
    @Id
    @GeneratedValue(strategy=GenerationType.AUTO)    
    private Long id;
    private String productId;
    private Integer qty;    
    private String customerId;    
    private Double amount;

    @PostPersist
    public void onPostPersist(){
        OrderPlaced orderPlaced = new OrderPlaced(this);
        orderPlaced.publishAfterCommit();
    }
    
    @PrePersist
    public void onPrePersist(){
        // Get request from Inventory
        //labshoppubsub.external.Inventory inventory =
        //    Application.applicationContext.getBean(labshoppubsub.external.InventoryService.class)
        //    .getInventory(/** mapping value needed */);

    }

    public static OrderRepository repository(){
        OrderRepository orderRepository = OrderApplication.applicationContext.getBean(OrderRepository.class);
        return orderRepository;
    }


}
