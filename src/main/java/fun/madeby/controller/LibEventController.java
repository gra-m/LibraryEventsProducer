package fun.madeby.controller;

import com.fasterxml.jackson.core.JsonProcessingException;
import fun.madeby.domain.LibEvent;
import fun.madeby.producer.LibEventProducer;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.kafka.support.SendResult;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import java.util.concurrent.ExecutionException;


@RestController
@Slf4j
public class LibEventController {
  private final LibEventProducer libEventProducer;

  @Autowired
  LibEventController(LibEventProducer libEventProducer) {
    this.libEventProducer = libEventProducer;
  }




@PostMapping("/v1/lib-event-synchronous")

  public ResponseEntity<LibEvent> postLibraryEventSynchronous(@RequestBody LibEvent libEvent)
      throws JsonProcessingException, ExecutionException, InterruptedException {
    // invoke kafka producer synchronous method
    log.info("Before sync sendLibEvent");
    SendResult<Integer, String> sendResult = libEventProducer.sendLibEventSynchronous(libEvent);
    log.info("Send result is {} ", sendResult.toString());
    log.info("After sync sendLibEvent");

    return ResponseEntity.status(HttpStatus.CREATED).body(libEvent);}


//Asynchronous
  @PostMapping("/v1/lib-event")
  public ResponseEntity<LibEvent> postLibraryEvent(@RequestBody LibEvent libEvent)
      throws JsonProcessingException {
    // invoke kafka producer
    log.info("Before async sendLibEvent");
    libEventProducer.sendLibEvent(libEvent);
    log.info("After async sendLibEvent");

    return ResponseEntity.status(HttpStatus.CREATED).body(libEvent);
  }

  //Asynchronous
@PostMapping("/v1/lib-event-prod-record")
public ResponseEntity<LibEvent> postLibraryEventProducerRecord(@RequestBody LibEvent libEvent)
    throws JsonProcessingException {
  // invoke kafka producer
  log.info("Before async sendLibEventProdRecord");
  libEventProducer.sendLibEventProdRecord(libEvent);
  log.info("After async sendLibEventProdRecord");

  return ResponseEntity.status(HttpStatus.CREATED).body(libEvent);
}
}
