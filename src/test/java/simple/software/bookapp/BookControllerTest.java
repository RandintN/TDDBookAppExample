package simple.software.bookapp;

import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.is;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.header;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.fasterxml.jackson.databind.ObjectMapper;
import java.util.List;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.MockMvc;

@ExtendWith(SpringExtension.class)
@WebMvcTest(BookController.class)
class BookControllerTest {

  @Autowired
  private MockMvc mockMvc;

  @Autowired
  private ObjectMapper objectMapper;

  @MockBean
  private BookService bookServiceMock;

  @Captor
  private ArgumentCaptor<BookRequest> argumentCaptor;

  @Test
  void postingANewBookShouldCreateNewBookInDatabase() throws Exception {
    // Arrange
    BookRequest bookRequest = new BookRequest();
    bookRequest.setTitle("Java 17");
    bookRequest.setIsbn("1337");
    bookRequest.setAuthor("Robson Cassiano");
    when(bookServiceMock.createNewBook(argumentCaptor.capture())).thenReturn(1L);

    // Act
    this.mockMvc.perform(post("/api/books")
            .contentType(MediaType.APPLICATION_JSON)
            .content(objectMapper.writeValueAsString(bookRequest))).andExpect(status().isCreated())
        .andExpect(header().exists("Location"))
        .andExpect(header().string("Location", "http://localhost/api/books/1"));

    // Assert
    assertThat(argumentCaptor.getValue().getAuthor()).isEqualTo("Robson Cassiano");
    assertThat(argumentCaptor.getValue().getTitle()).isEqualTo("Java 17");
    assertThat(argumentCaptor.getValue().getIsbn()).isEqualTo("1337");
  }

  @Test
  void allBooksEndpointShouldReturnTwoBooks() throws Exception {
    when(bookServiceMock.getAllBooks()).thenReturn(
        List.of(createBook(1L, "Java 17", "Duke", "1337"),
            createBook(2L, "Kotlin", "Jetbrains", "777")));

    this.mockMvc.perform(get("/api/books")).andExpect(status().isOk())
        .andExpect(content().contentType("application/json"))
        .andExpect(jsonPath("$", hasSize(2)))
        .andExpect(jsonPath("$[0].title", is("Java 17")))
        .andExpect(jsonPath("$[0].author", is("Duke")))
        .andExpect(jsonPath("$[0].isbn", is("1337")))
        .andExpect(jsonPath("$[0].id", is(1)));
  }

  @Test
  void getBookWithIdShouldReturnOneBook() throws Exception {
    when(bookServiceMock.getBookById(1L)).thenReturn(createBook(1L, "Java 17", "Duke", "1337"));

    this.mockMvc.perform(get("/api/books/1")).andExpect(status().isOk())
        .andExpect(content().contentType("application/json"))
        .andExpect(jsonPath("$.title", is("Java 17")))
        .andExpect(jsonPath("$.author", is("Duke")))
        .andExpect(jsonPath("$.isbn", is("1337")))
        .andExpect(jsonPath("$.id", is(1)));
  }

  @Test
  void getBookWithUnknownIdShouldReturn404() throws Exception {
    when(bookServiceMock.getBookById(42L)).thenThrow(
        new BookNotFoundException("This book doesn't exist"));

    this.mockMvc
        .perform(get("/api/books/42"))
        .andExpect(status().isNotFound());
  }

  @Test
  void updateBookWithKnowIdShouldUpdateBook() throws Exception {
    final BookRequest bookRequest = new BookRequest();
    bookRequest.setAuthor("Robson");
    bookRequest.setIsbn("1337");
    bookRequest.setTitle("Java 17");

    when(bookServiceMock.updateBook(eq(1L), argumentCaptor.capture())).thenReturn(
        createBook(1L, "Java 17", "Robson", "1337"));

    this.mockMvc.perform(put("/api/books/1")
            .contentType(MediaType.APPLICATION_JSON)
            .content(objectMapper.writeValueAsString(bookRequest)))
        .andExpect(status().isCreated())
        .andExpect(content().contentType(MediaType.APPLICATION_JSON))
        .andExpect(jsonPath("$.title", is("Java 17")))
        .andExpect(jsonPath("$.author", is("Robson")))
        .andExpect(jsonPath("$.isbn", is("1337")))
        .andExpect(jsonPath("$.id", is(1)));

    assertThat(argumentCaptor.getValue().getAuthor()).isEqualTo("Robson");
    assertThat(argumentCaptor.getValue().getTitle()).isEqualTo("Java 17");
    assertThat(argumentCaptor.getValue().getIsbn()).isEqualTo("1337");
  }

  @Test
  void updateBookWithUnknownIdShouldReturn404() throws Exception {
    final BookRequest bookRequest = new BookRequest();
    bookRequest.setAuthor("Robson");
    bookRequest.setIsbn("35d415f");
    bookRequest.setTitle("Java 17");

    when(bookServiceMock.updateBook(eq(420L), argumentCaptor.capture())).thenThrow(
        new BookNotFoundException("The book doesn't exist"));

    this.mockMvc.perform(put("/api/books/420").contentType(MediaType.APPLICATION_JSON)
            .content(objectMapper.writeValueAsString(bookRequest)))
        .andExpect(status().isNotFound());
  }

  private Book createBook(Long id, String title, String author, String isbn) {
    final Book book = new Book();
    book.setIsbn(isbn);
    book.setAuthor(author);
    book.setTitle(title);
    book.setId(id);
    return book;
  }
}