package fun.madeby.controller;

import fun.madeby.domain.Book;
import fun.madeby.domain.LibEvent;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.http.*;


//avoid conflict with 8080 by using random
@SpringBootTest(classes = {}, webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class Int_LibEventControllerTest {

@Autowired
TestRestTemplate restTemplate; // auto maps to RANDOM_PORT

@Test
void postLibEvent()   {
  //given

  Book book = Book.builder()
                  .bookId(123)
                  .bookAuthor("Dilip")
                  .bookName("Kefku using Sprang Boat")
                  .build();

  LibEvent libEvent = LibEvent.builder()
                              .libraryEventId(null)
                              .book(book)
                              .build();

  HttpHeaders headers = new HttpHeaders();
  headers.set("content-type", MediaType.APPLICATION_JSON.toString());
  HttpEntity<LibEvent> request = new HttpEntity<>(libEvent, headers);


  //when
  ResponseEntity<LibEvent> responseEntity =restTemplate.exchange("/v2/lib-event", HttpMethod.POST, request, LibEvent.class);


  //then
  Assertions.assertEquals(HttpStatus.CREATED, responseEntity.getStatusCode());
}
}
