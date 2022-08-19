package simple.software.bookapp;

import com.github.javafaker.Faker;
import java.util.UUID;
import java.util.stream.IntStream;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class BookInitializer implements CommandLineRunner {

  private final BookRepository bookRepository;

  @Override
  public void run(String... args) {
    log.info("Starting to initialize sample data...");

    final Faker faker = new Faker();

    IntStream.range(0, 100).forEach(b -> {
      final Book book = new Book();
      book.setAuthor(faker.book().author());
      book.setTitle(faker.book().title());
      book.setIsbn(UUID.randomUUID().toString());

      bookRepository.save(book);
    });

    log.info("...Finished with data initialization.");
  }
}
