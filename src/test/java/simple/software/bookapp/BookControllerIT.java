package simple.software.bookapp;

import static org.assertj.core.api.Assertions.assertThat;

import com.fasterxml.jackson.databind.JsonNode;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.junit.jupiter.SpringExtension;

@ExtendWith(SpringExtension.class)
@SpringBootTest(webEnvironment = WebEnvironment.RANDOM_PORT)
class BookControllerIT {
  @LocalServerPort
  int randomServerPort;

  private TestRestTemplate testRestTemplate;

  @BeforeEach
  void setUp() {
    this.testRestTemplate = new TestRestTemplate();
  }

  @Test
  void deletingKnowEntityShouldReturn404AfterDeletion() {
    long bookId = 1L;
    String baseUrl = "http://localhost:" + randomServerPort;

    final ResponseEntity<JsonNode> firstResult = this.testRestTemplate.getForEntity(
        baseUrl + "/api/books/" + bookId, JsonNode.class);

    assertThat(firstResult.getStatusCode()).isEqualTo(HttpStatus.OK);

    this.testRestTemplate.delete(baseUrl + "/api/books/" + bookId, JsonNode.class);

    final ResponseEntity<JsonNode> secondResult = this.testRestTemplate.getForEntity(
        baseUrl + "/api/books/" + bookId, JsonNode.class);

    assertThat(secondResult.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);
  }
}