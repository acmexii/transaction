package transaction.infra;

import org.springframework.beans.BeanUtils;
import org.springframework.kafka.support.Acknowledgment;
import org.springframework.kafka.support.KafkaHeaders;
import org.springframework.messaging.MessageChannel;
import org.springframework.messaging.MessageHeaders;
import org.springframework.messaging.support.MessageBuilder;
import org.springframework.transaction.support.TransactionSynchronizationAdapter;
import org.springframework.transaction.support.TransactionSynchronizationManager;
import org.springframework.util.MimeTypeUtils;
import transaction.InventoryApplication;
import transaction.config.kafka.KafkaProcessor;

public class AbstractEvent {

    String eventType;
    Long timestamp;

    public AbstractEvent(Object aggregate){
        this();
        BeanUtils.copyProperties(aggregate, this);
    }

    public AbstractEvent(){
        this.setEventType(this.getClass().getSimpleName());
        this.timestamp = System.currentTimeMillis();
    }

    public void publish() {
        /**
         * spring streams 방식
         */
        KafkaProcessor processor = InventoryApplication.applicationContext.getBean(
            KafkaProcessor.class
        );
        MessageChannel outputChannel = processor.outboundTopic();

        outputChannel.send(
            MessageBuilder
                .withPayload(this)
                .setHeader(
                    MessageHeaders.CONTENT_TYPE,
                    MimeTypeUtils.APPLICATION_JSON
                )
                .setHeader(                    
                    "type",
                    getEventType()
                )
                .setHeader(
                    KafkaHeaders.MESSAGE_KEY, new String("MsgKey-" + Math.floor(Math.random() * 10)).getBytes() // Create random Integer between 0~9. 
                )
                .build()
        );
    }

    public void publish(String messageKey) {
        /**
         * spring streams 방식
         */
        KafkaProcessor processor = InventoryApplication.applicationContext.getBean(
            KafkaProcessor.class
        );
        MessageChannel outputChannel = processor.outboundTopic();

        outputChannel.send(
            MessageBuilder
                .withPayload(this)
                .setHeader(
                    MessageHeaders.CONTENT_TYPE,
                    MimeTypeUtils.APPLICATION_JSON
                )
                .setHeader(                    
                    "type",
                    getEventType()
                )
                .setHeader(
                    KafkaHeaders.MESSAGE_KEY, messageKey
                )
                .build()
        );
    }

    public void publishAfterCommit(Acknowledgment acknowledgment, boolean needToPublish){
        TransactionSynchronizationManager.registerSynchronization(new TransactionSynchronizationAdapter() {

            @Override
            public void afterCommit() {
                try {
                    if(needToPublish) {
                        AbstractEvent.this.publish();  // DB 커밋 후, Kafka Event 발행
                    }
                    acknowledgment.acknowledge(); // Kafka offset commit 

                } catch (Exception e) {
                    e.printStackTrace();
                    // 이벤트 발행 실패 시, 대응 전략 구현
                    //eventRetryService.scheduleRetry(this, 3); example Code
                }
            }
        });
    }
    
    public void publishAfterCommit(){
        TransactionSynchronizationManager.registerSynchronization(new TransactionSynchronizationAdapter() {

            @Override
            public void afterCompletion(int status) {
                AbstractEvent.this.publish();
            }
        });
    }


    public String getEventType() {
        return eventType;
    }

    public void setEventType(String eventType) {
        this.eventType = eventType;
    }

    public Long getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(Long timestamp) {
        this.timestamp = timestamp;
    }

    public boolean validate(){
        return getEventType().equals(getClass().getSimpleName());
    }
}