package fun.madeby.producer;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import fun.madeby.domain.LibEvent;

import java.util.List;
import java.util.concurrent.ExecutionException;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.apache.kafka.common.header.Header;
import org.apache.kafka.common.header.internals.RecordHeader;
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
String TOPIC_FOR_PRODUCER_RECORD_EXAMPLE = "lib-events";

@Autowired
public LibEventProducer(KafkaTemplate<Integer, String> kafkaTemplate, ObjectMapper objectMapper) {
      this.kafkaTemplate = kafkaTemplate;
      this.objectMapper = objectMapper;
}


public void sendLibEventPr(LibEvent libEvent) throws JsonProcessingException {
      Integer key = libEvent.getLibraryEventId();
      String value = objectMapper.writeValueAsString(libEvent);


      ListenableFuture<SendResult<Integer, String>> listenableFuture =
          kafkaTemplate.send(buildProducerRecord(key,value));
      listenableFuture.addCallback(
          (new ListenableFutureCallback<>() {
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

private ProducerRecord<Integer, String> buildProducerRecord(Integer key, String value) {
      List<Header> recordHeaders = List.of(new RecordHeader("event-source", "scanner".getBytes()));
      return new ProducerRecord<>(TOPIC_FOR_PRODUCER_RECORD_EXAMPLE, null, key, value, recordHeaders);
}

private void handleFailure(Integer key, String value, Throwable ex) {
      log.error(
          "Message send failed with {}, \n\nKEY WAS {}, VALUE WAS {}", ex.getMessage(), key, value);
      try {
            throw ex;
      } catch (Throwable e) {
            log.error("Error in OnFailure {}", e.getMessage());
      }
}

private void handleSuccess(Integer key, String value, SendResult<Integer, String> result) {
      log.info(
          "Message sent successfully for key {} and value {} to partition {}",
          key,
          value,
          result.getRecordMetadata().partition());
}






//region <V1 ENDPOINT send methods>
private ProducerRecord<Integer, String> buildProducerRecordv1(Integer key, String value) {
      List<Header> recordHeaders = List.of(new RecordHeader("event-source", "scanner".getBytes()));
      return new ProducerRecord<>(TOPIC_FOR_PRODUCER_RECORD_EXAMPLE, null, key, value, recordHeaders);
}

// Synchronous

public SendResult<Integer, String> sendLibEventSynchronous(LibEvent libEvent)
    throws JsonProcessingException, ExecutionException, InterruptedException {
      Integer key = libEvent.getLibraryEventId();
      String value = objectMapper.writeValueAsString(libEvent);
      SendResult<Integer, String> sendResult = null;

      try {
            // this returns a SendResult<Integer, String>
            sendResult = kafkaTemplate.sendDefault(key, value).get();
      } catch (ExecutionException | InterruptedException e) {
            log.error(
                "Synch: Execution/Interrupted Exception: Message send failed with {}, \n\nKEY WAS {}, VALUE WAS {}",
                e.getMessage(),
                key,
                value);
            throw e;

      } catch (Exception e) {
            log.error(
                "Synch: Exception: Message send failed with {}, \n\nKEY WAS {}, VALUE WAS {}",
                e.getMessage(),
                key,
                value);
            e.printStackTrace();
      }

      return sendResult;
}

public void sendLibEventProdRecord(LibEvent libEvent) throws JsonProcessingException {
      Integer key = libEvent.getLibraryEventId();
      String value = objectMapper.writeValueAsString(libEvent);


      ListenableFuture<SendResult<Integer, String>> listenableFuture =
          kafkaTemplate.send(buildProducerRecordv1(key,value));
      listenableFuture.addCallback(
          (new ListenableFutureCallback<SendResult<Integer, String>>() {
                @Override
                public void onFailure(Throwable ex) {
                      handleFailurev1(key, value, ex);
                }

                @Override
                public void onSuccess(SendResult<Integer, String> result) {
                      handleSuccessv1(key, value, result);
                }
          }));
}



public void sendLibEvent(LibEvent libEvent) throws JsonProcessingException {
      Integer key = libEvent.getLibraryEventId();
      String value = objectMapper.writeValueAsString(libEvent);

      // note orig code uses Listenable future and callback, this is deprecated.
      // for an event to be sent template: default-topic: must be set in application-local.yml
      // Using deprecated code for now todo https://dzone.com/articles/converting-listenablefutures
    /*CompletableFuture<SendResult<Integer, String>> completableFuture =  kafkaTemplate.sendDefault(key, value);
    completableFuture.whenCompleteAsync(new BiConsumer<SendResult<Integer, String>, Throwable>() {
          @Override
          public void accept(SendResult<Integer, String> result, Throwable throwable) {
                handleSuccess(key, value, result);

          }
    });*/

      ListenableFuture<SendResult<Integer, String>> listenableFuture =
          kafkaTemplate.sendDefault(key, value);
      listenableFuture.addCallback(
          (new ListenableFutureCallback<SendResult<Integer, String>>() {
                @Override
                public void onFailure(Throwable ex) {
                      handleFailurev1(key, value, ex);
                }

                @Override
                public void onSuccess(SendResult<Integer, String> result) {
                      handleSuccessv1(key, value, result);
                }
          }));
}

private void handleFailurev1(Integer key, String value, Throwable ex) {
      log.error(
          "Message send failed with {}, \n\nKEY WAS {}, VALUE WAS {}", ex.getMessage(), key, value);
      try {
            throw ex;
      } catch (Throwable e) {
            log.error("Error in OnFailure {}", e.getMessage());
      }
}

private void handleSuccessv1(Integer key, String value, SendResult<Integer, String> result) {
      log.info(
          "Message sent successfully for key {} and value {} to partition {}",
          key,
          value,
          result.getRecordMetadata().partition());
}

//endregion
}
