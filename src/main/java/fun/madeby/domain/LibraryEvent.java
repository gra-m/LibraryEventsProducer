package fun.madeby.domain;

import fun.madeby.domain.Book;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@NoArgsConstructor
@Data //getters setters etc
@Builder //fluent api style
public class LibraryEvent {
private Integer libraryEventId;
private Book book;
}
