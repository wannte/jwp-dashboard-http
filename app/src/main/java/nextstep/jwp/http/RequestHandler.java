package nextstep.jwp.http;

import static nextstep.jwp.http.ResourceResolver.checkIfUriHasResourceExtension;
import static nextstep.jwp.http.ResourceResolver.resolveResourceRequest;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;
import java.util.Objects;
import nextstep.jwp.web.ControllerAdvice;
import nextstep.jwp.web.RequestMapper;
import nextstep.jwp.web.controller.Controller;
import nextstep.jwp.web.exceptionhandler.ExceptionHandler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class RequestHandler implements Runnable {

    private static final Logger log = LoggerFactory.getLogger(RequestHandler.class);

    private final Socket connection;

    public RequestHandler(Socket connection) {
        this.connection = Objects.requireNonNull(connection);
    }

    @Override
    public void run() {
        log.debug("New Client Connect! Connected IP : {}, Port : {}", connection.getInetAddress(),
                connection.getPort());

        try (final InputStream inputStream = connection.getInputStream();
                final OutputStream outputStream = connection.getOutputStream()) {

            HttpRequest httpRequest = HttpRequestReader.read(inputStream);

            HttpResponse httpResponse = new HttpResponse();

            handle(httpRequest, httpResponse);

            String output = httpResponse.asString();
            log.debug("outputStream => {}", output);
            outputStream.write(output.getBytes());
            outputStream.flush();
        } catch (IllegalStateException exception) {
            log.info("IllegalStateException {}", exception.getMessage());
        } catch (IOException exception) {
            log.error("Exception stream", exception);
        } finally {
            close();
        }
    }

    private void handle(HttpRequest httpRequest, HttpResponse httpResponse) throws IOException {
        try {
            if (checkIfUriHasResourceExtension(httpRequest.uri())) {
                resolveResourceRequest(httpRequest, httpResponse);
                return;
            }

            Controller controller = RequestMapper.findController(httpRequest);
            controller.doService(httpRequest, httpResponse);

        } catch (Exception exception) {
            ExceptionHandler handler = ControllerAdvice.findExceptionHandler(exception);
            handler.handle(httpRequest, httpResponse);
        }
    }

    private void close() {
        try {
            connection.close();
        } catch (IOException exception) {
            log.error("Exception closing socket", exception);
        }
    }
}
