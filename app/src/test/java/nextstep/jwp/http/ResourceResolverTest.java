package nextstep.jwp.http;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Objects;
import nextstep.jwp.exception.MethodNotAllowedException;
import nextstep.jwp.exception.NotFoundException;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;

class ResourceResolverTest {

    @ParameterizedTest
    @CsvSource({"index.html", "js/scripts.js", "css/style.css"})
    void checkIfUriHasResourceExtension(String uri) {
        boolean actual = ResourceResolver.checkIfUriHasResourceExtension(uri);

        assertTrue(actual);
    }

    @Test
    @DisplayName("정의되지 않은 리소스 extension - false")
    void checkIfUriHasResourceExtensionFalse() {
        boolean actual = ResourceResolver.checkIfUriHasResourceExtension("notExtension.notExtension");

        assertFalse(actual);
    }

    @Test
    @DisplayName("정상 리소스 요청")
    void resolveResourceRequest() throws IOException {
        String uri = "/index.html";
        HttpRequest httpRequest = new HttpRequest("GET", uri);

        final URL resource = ResourceResolver.class.getClassLoader().getResource("static" + uri);
        final Path path = new File(Objects.requireNonNull(resource).getPath()).toPath();

        String responseBody = Files.readString(path);
        String contentType = Files.probeContentType(path);

        String actual = ResourceResolver.resolveResourceRequest(httpRequest);

        assertThat(actual).isEqualTo(HttpResponse.ok(contentType, responseBody));
    }

    @Test
    @DisplayName("존재 하지 않는 리소스 요청 - 에러 발생")
    void resolveResourceRequestNotExisting(){
        String uri = "/notExisting.html";
        HttpRequest httpRequest = new HttpRequest("GET", uri);

        assertThatThrownBy(
                () -> ResourceResolver.resolveResourceRequest(httpRequest)
        ).isInstanceOf(NotFoundException.class);
    }

    @Test
    @DisplayName("Post 리소스 요청 - 에러 발생")
    void resolveResourceRequestWithPost() {
        String uri = "/index.html";
        HttpRequest httpRequest = new HttpRequest("POST", uri);

        assertThatThrownBy(
                () -> ResourceResolver.resolveResourceRequest(httpRequest)
        ).isInstanceOf(MethodNotAllowedException.class);
    }
}
