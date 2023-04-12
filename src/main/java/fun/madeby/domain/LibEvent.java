package fun.madeby.domain;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@NoArgsConstructor
@Data
@Builder
public class LibEvent {
private Integer libraryEventId;
private LibraryEventType libEventType;
private Book book;
}
