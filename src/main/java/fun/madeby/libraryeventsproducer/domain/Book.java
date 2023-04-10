package fun.madeby.libraryeventsproducer.domain;


import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@NoArgsConstructor
@Data
@Builder
public class Book {
private Integer BookId;
private String bookName;
private String bookAuthor;
}
