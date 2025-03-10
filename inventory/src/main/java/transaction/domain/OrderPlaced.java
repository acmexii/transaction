package transaction.domain;

import lombok.*;
import transaction.infra.AbstractEvent;

@Data
@ToString
public class OrderPlaced extends AbstractEvent {

    private Long id;
    private String productId;
    private Integer qty;
    private String customerId;
    private Double amount;
}
