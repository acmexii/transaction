package transaction.domain;

import java.time.LocalDate;
import java.util.*;
import lombok.*;
import transaction.domain.*;
import transaction.infra.AbstractEvent;

//<<< DDD / Domain Event
@Data
@ToString
public class InventoryChanged extends AbstractEvent {

    private Long id;
    private Long stock;

    public InventoryChanged(Inventory aggregate) {
        super(aggregate);
    }

    public InventoryChanged() {
        super();
    }
}
//>>> DDD / Domain Event
