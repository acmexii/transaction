package transaction.domain;

import java.time.LocalDate;
import java.util.*;
import lombok.*;
import transaction.domain.*;
import transaction.infra.AbstractEvent;

//<<< DDD / Domain Event
@Data
@ToString
public class OrderPlaced extends AbstractEvent {

    private Long id;
    private String productId;
    private Integer qty;
    private String customerId;
    private Double amount;

    public OrderPlaced(Order aggregate) {
        super(aggregate);
    }

    public OrderPlaced() {
        super();
    }
}
//>>> DDD / Domain Event
