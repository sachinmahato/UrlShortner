import com.schwab.agentic.shortener.model.UrlMapping;
import com.schwab.agentic.shortener.repository.UrlRepository;
import com.schwab.agentic.shortener.service.UrlShortenerService;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;

class UrlShortenerServiceTest {

    @Test
    void testShortenUrl() {
        UrlRepository repository = Mockito.mock(UrlRepository.class);
        Mockito.when(repository.save(any(UrlMapping.class))).thenAnswer(invocation -> invocation.getArgument(0));

        UrlShortenerService service = new UrlShortenerService(repository);
        UrlMapping result = service.shortenUrl("https://example.com");

        assertNotNull(result);
        assertEquals("https://example.com", result.getOriginalUrl());
        assertNotNull(result.getShortCode());
    }
}