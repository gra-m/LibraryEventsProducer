package fun.madeby.producer;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import fun.madeby.domain.LibEvent;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.support.SendResult;
import org.springframework.stereotype.Component;
import org.springframework.util.concurrent.ListenableFuture;
import org.springframework.util.concurrent.ListenableFutureCallback;

@Component
@Slf4j
public class LibEventProducer {

  final KafkaTemplate<Integer, String> kafkaTemplate;
  final ObjectMapper objectMapper;

  @Autowired
  public LibEventProducer(KafkaTemplate<Integer, String> kafkaTemplate, ObjectMapper objectMapper) {
    this.kafkaTemplate = kafkaTemplate;
    this.objectMapper = objectMapper;
  }

  public void sendLibEvent(LibEvent libEvent) throws JsonProcessingException {
    Integer key = libEvent.getLibraryEventId();
    String value = objectMapper.writeValueAsString(libEvent);

    // note orig code uses Listenable future and callback, this is deprecated.
    // for an event to be sent template: default-topic: must be set in application-local.yml
    // Using deprecated code for now todo come back and update this
    /*CompletableFuture<SendResult<Integer, String>> completableFuture =  kafkaTemplate.sendDefault(key, value);
    completableFuture.whenCompleteAsync(new BiConsumer<SendResult<Integer, String>, Throwable>() {
          @Override
          public void accept(SendResult<Integer, String> result, Throwable throwable) {
                handleSuccess(key, value, result);

          }
    });*/

    @SuppressWarnings("unchecked")
    ListenableFuture<SendResult<Integer, String>> listenableFuture =
        (ListenableFuture<SendResult<Integer, String>>) kafkaTemplate.sendDefault(key, value);
    listenableFuture.addCallback(
        (new ListenableFutureCallback<SendResult<Integer, String>>() {
          @Override
          public void onFailure(Throwable ex) {
            handleFailure(key, value, ex);
          }

          @Override
          public void onSuccess(SendResult<Integer, String> result) {
            handleSuccess(key, value, result);
          }
        }));
  }

  private void handleFailure(Integer key, String value, Throwable ex) {
    log.error("Message send failed with {}, \n\nKEY WAS {}, VALUE WAS {}", ex.getMessage(), key, value);
        try {
              throw ex;
        }
        catch( Throwable e ) {
              log.error("Error in OnFailure {}", e.getMessage());}
  }

  private void handleSuccess(Integer key, String value, SendResult<Integer, String> result) {
    log.info(
        "Message sent successfully for key {} and value {} to partition {}",
        key,
        value,
        result.getRecordMetadata().partition());
  }
}
