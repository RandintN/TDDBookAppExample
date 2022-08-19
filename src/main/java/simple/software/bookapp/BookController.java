package simple.software.bookapp;

import java.util.List;
import javax.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.util.UriComponents;
import org.springframework.web.util.UriComponentsBuilder;

@RestController
@RequiredArgsConstructor
@RequestMapping(path = "/api/books")
public class BookController {

  private final BookService bookService;

  @PostMapping
  public ResponseEntity<Void> createNewBook(@Valid @RequestBody BookRequest bookRequest, UriComponentsBuilder uriComponentsBuilder) {
    final Long id = bookService.createNewBook(bookRequest);
    UriComponents uriComponents = uriComponentsBuilder.path("/api/books/{id}").buildAndExpand(id);
    HttpHeaders headers = new HttpHeaders();
    headers.setLocation(uriComponents.toUri());

    return new ResponseEntity<>(headers, HttpStatus.CREATED);
  }

  @GetMapping
  public ResponseEntity<List<Book>> getAllBooks() {
    return ResponseEntity.ok(bookService.getAllBooks());
  }
  @GetMapping(path = "/{id}")
  public ResponseEntity<Book> getBookById(@PathVariable("id") Long id) {
    return ResponseEntity.ok(bookService.getBookById(id));
  }

  @PutMapping(path = "{id}")
  public ResponseEntity<Book> updateBook(@PathVariable Long id, @Valid @RequestBody BookRequest bookRequest) {
    return ResponseEntity
        .status(HttpStatus.CREATED)
        .body(bookService.updateBook(id, bookRequest));
  }

  @DeleteMapping(path = "{id}")
  public ResponseEntity<Void> deleteBook(@PathVariable Long id) {
    bookService.deleteBookById(id);
    return ResponseEntity.ok().build();
  }
}
