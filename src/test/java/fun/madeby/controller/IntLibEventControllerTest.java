package fun.madeby.controller;

import static org.junit.jupiter.api.Assertions.assertEquals;

import fun.madeby.domain.Book;
import fun.madeby.domain.LibEvent;
import java.util.HashMap;
import java.util.Map;
import org.apache.kafka.clients.consumer.Consumer;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.common.serialization.IntegerDeserializer;
import org.apache.kafka.common.serialization.StringDeserializer;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.Timeout;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.web.servlet.ServletWebServerFactoryAutoConfiguration;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.http.*;
import org.springframework.kafka.core.DefaultKafkaConsumerFactory;
import org.springframework.kafka.test.EmbeddedKafkaBroker;
import org.springframework.kafka.test.context.EmbeddedKafka;
import org.springframework.kafka.test.utils.KafkaTestUtils;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.TestPropertySource;

// avoid conflict with 8080 by using random // TestPropSource overrides the paths for kafka brokers
// in application-local.yml

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@EmbeddedKafka (topics = "lib-events", controlledShutdown = true, partitions = 3)
@DirtiesContext // to destroy
@TestPropertySource(
    properties = {
      "spring.kafka.producer.bootstrap-servers=${spring.embedded.kafka.brokers}",
      "spring.kafka.admin.properties.bootstrap.servers=${spring.embedded.kafka.brokers}"
    })
public class IntLibEventControllerTest {

  @Autowired
  private TestRestTemplate restTemplate; // auto maps to RANDOM_PORT

  @Autowired
  private EmbeddedKafkaBroker embeddedKafkaBroker;
  private Consumer<Integer, String> consumer;


@BeforeEach
  void setUp() {
  
    Map<String, Object> configs =
        new HashMap<>(KafkaTestUtils.consumerProps("group1", "true", embeddedKafkaBroker));
    Consumer<Integer, String> consumer =
        new DefaultKafkaConsumerFactory<>(
                configs, new IntegerDeserializer(), new StringDeserializer())
            .createConsumer();
    embeddedKafkaBroker.consumeFromAllEmbeddedTopics(consumer);
  }

  @AfterEach
  void tearDown() {
    consumer.close();
  }

  @Test
  @Timeout(5)
  void postLibEvent() throws InterruptedException {
    // given

    Book book =
        Book.builder().bookId(123).bookAuthor("Dilip").bookName("Kafka Using Spring Boot").build();

    LibEvent libEvent = LibEvent.builder().libraryEventId(null).book(book).build();

    HttpHeaders headers = new HttpHeaders();
    headers.set("content-type", MediaType.APPLICATION_JSON.toString());
    HttpEntity<LibEvent> request = new HttpEntity<>(libEvent, headers);

    // when
    ResponseEntity<LibEvent> responseEntity =
        restTemplate.exchange("/v2/lib-event", HttpMethod.POST, request, LibEvent.class);

    // then
    assertEquals(HttpStatus.CREATED, responseEntity.getStatusCode());
    ConsumerRecord<Integer, String> consumerRecord =
        KafkaTestUtils.getSingleRecord(consumer, "lib-events");
    // Thread.sleep(3000);
    String expectedRecord =
        "{\"libraryEventId\":null,\"libraryEventType\":\"NEW\",\"book\":{\"bookId\":456,\"bookName\":\"Kafka Using Spring Boot\",\"bookAuthor\":\"Dilip\"}}";
    assertEquals(expectedRecord, consumerRecord.value());
  }
}
