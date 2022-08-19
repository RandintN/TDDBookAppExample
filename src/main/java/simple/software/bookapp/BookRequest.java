package simple.software.bookapp;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class BookRequest {
  @NotEmpty
  private String author;

  @NotEmpty
  @Size(max = 36)
  private String isbn;

  @NotEmpty
  private String title;

}
