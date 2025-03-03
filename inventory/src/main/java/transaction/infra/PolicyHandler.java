package transaction.infra;

import transaction.config.kafka.KafkaProcessor;

import org.springframework.cloud.stream.annotation.StreamListener;
import org.springframework.kafka.support.Acknowledgment;
import org.springframework.messaging.handler.annotation.Header;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import com.fasterxml.jackson.databind.ObjectMapper;

import transaction.domain.*;
import org.springframework.kafka.support.KafkaHeaders;

@Service
@Transactional
public class PolicyHandler{
    
    private final ObjectMapper objectMapper;
    private final InventoryRepository inventoryRepository;

    public PolicyHandler(ObjectMapper objectMapper, InventoryRepository inventoryRepository) {
        this.objectMapper = objectMapper;
        this.inventoryRepository = inventoryRepository;
    }
    
    @StreamListener(value=KafkaProcessor.INPUT)
    public void handleMessage(
        @Header("type") String eventType, @Payload String eventString,
        @Header(KafkaHeaders.ACKNOWLEDGMENT) Acknowledgment acknowledgment) {

            if (eventType.equals("OrderPlaced")) {
                try {
                    OrderPlaced orderPlaced = objectMapper.readValue(eventString, OrderPlaced.class);
                    
                    // 리포지토리 조회 및 도메인 로직 실행
                    Inventory inventory = inventoryRepository.findById(Long.valueOf(orderPlaced.getProductId()))
                        .orElseThrow(() -> new RuntimeException("Inventory not found"));
                    
                    InventoryChanged event = inventory.decreaseStock(orderPlaced.getQty());
                    inventoryRepository.save(inventory);
                    
                    // 이벤트 발행
                    event.publishAfterCommit(acknowledgment, true);
                } catch (Exception e) {
                    throw new RuntimeException(e);
                }
        } else {
            // 처리 대상이 아닌 이벤트는 Lagging 처리를 위해 오프셋만만 커밋
            acknowledgment.acknowledge();
        }
    }    
}


