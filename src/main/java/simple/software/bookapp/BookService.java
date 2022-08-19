package simple.software.bookapp;

import java.util.List;
import java.util.Optional;
import javax.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class BookService {

  private final BookRepository bookRepository;

  public Long createNewBook(BookRequest bookRequest) {
    Book book = new Book();
    book.setAuthor(bookRequest.getAuthor());
    book.setTitle(bookRequest.getTitle());
    book.setIsbn(bookRequest.getIsbn());

    book = bookRepository.save(book);
    return book.getId();
  }

  public List<Book> getAllBooks() {
    return bookRepository.findAll();
  }

  public Book getBookById(Long id) {
    return bookRepository.findById(id).orElseThrow(
        () -> new BookNotFoundException(String.format("This book with id %s doesn't exist", id)));
  }

  @Transactional
  @Modifying
  public Book updateBook(Long id, BookRequest bookToUpdateRequest) {
    final Optional<Book> bookFromDatabase = bookRepository.findById(id);

    if (bookFromDatabase.isEmpty()) {
      throw new BookNotFoundException(
          String.format("Book with id %s not found on the database", id));
    }

    final Book bookToUpdate = bookFromDatabase.get();

    bookToUpdate.setAuthor(bookToUpdateRequest.getAuthor());
    bookToUpdate.setTitle(bookToUpdateRequest.getTitle());
    bookToUpdate.setIsbn(bookToUpdateRequest.getIsbn());

    return bookRepository.saveAndFlush(bookToUpdate);
  }

  public void deleteBookById(Long id) {
    bookRepository.deleteById(id);
  }
}
