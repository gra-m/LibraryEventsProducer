package fun.madeby.libraryeventsproducer.domain;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@NoArgsConstructor
@Data //getters setters etc
@Builder //fluent api style
public class LibraryEvent {
private Integer LibraryEventId;
private Book book;
}
