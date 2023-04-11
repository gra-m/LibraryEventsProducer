package fun.madeby.controller;

import com.fasterxml.jackson.core.JsonProcessingException;
import fun.madeby.domain.LibEvent;
import fun.madeby.producer.LibEventProducer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class LibEventController {
  private final LibEventProducer libEventProducer;

  @Autowired
  LibEventController(LibEventProducer libEventProducer) {
    this.libEventProducer = libEventProducer;
  }

  @PostMapping("/v1/lib-event")
  public ResponseEntity<LibEvent> postLibraryEvent(@RequestBody LibEvent libEvent)
      throws JsonProcessingException {
    // invoke kafka producer
    libEventProducer.sendLibEvent(libEvent);
    return ResponseEntity.status(HttpStatus.CREATED).body(libEvent);
  }
}
