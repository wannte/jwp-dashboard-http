package nextstep.jwp.http;

import java.util.ArrayList;
import java.util.List;
import nextstep.jwp.http.entity.HttpBody;
import nextstep.jwp.http.entity.HttpCookie;
import nextstep.jwp.http.entity.HttpHeaders;
import nextstep.jwp.http.entity.HttpStatus;
import nextstep.jwp.http.entity.HttpVersion;

public class HttpResponse {
    private HttpVersion httpVersion;
    private HttpStatus httpStatus;
    private HttpHeaders httpHeaders;
    private HttpBody httpBody;

    public HttpResponse() {
        this(HttpVersion.HTTP_1_1, HttpStatus.OK, new HttpHeaders(), HttpBody.empty());
    }

    public HttpResponse(HttpStatus httpStatus, HttpHeaders httpHeaders, HttpBody httpBody) {
        this(HttpVersion.HTTP_1_1, httpStatus, httpHeaders, httpBody);
    }

    public HttpResponse(HttpVersion httpVersion, HttpStatus httpStatus, HttpHeaders httpHeaders,
                        HttpBody httpBody) {
        this.httpVersion = httpVersion;
        this.httpStatus = httpStatus;
        this.httpHeaders = httpHeaders;
        this.httpBody = httpBody;
    }

    public HttpVersion httpVersion() {
        return httpVersion;
    }

    public HttpStatus httpStatus() {
        return httpStatus;
    }

    public HttpHeaders httpHeaders() {
        return httpHeaders;
    }

    public HttpBody httpBody() {
        return httpBody;
    }

    public void setHttpStatus(HttpStatus httpStatus) {
        this.httpStatus = httpStatus;
    }

    public void setHttpBody(String contentType, String body) {
        httpHeaders.addHeader("Content-Type", contentType);
        httpHeaders.addHeader("Content-Length", String.valueOf(body.getBytes().length));
        httpBody = HttpBody.of(body);
    }

    private void setLocation(String location) {
        httpHeaders.addHeader("Location", location);
    }

    public void setCookie(HttpCookie cookie) {
        cookie.getCookies().keySet()
                .forEach(key -> addHeader("Set-Cookie", cookie.asString(key)));
    }

    public void addHeader(String name, String value) {
        httpHeaders.addHeader(name, value);
    }

    public String asString() {
        List<String> output = new ArrayList<>();

        output.add(httpVersion.protocol() + " " + httpStatus + " ");
        output.addAll(httpHeaders.asString());
        output.add("");
        output.add(httpBody.body());

        return String.join("\r\n", output);
    }

    public void redirect(String path) {
        setHttpStatus(HttpStatus.FOUND);
        setLocation(path);
    }
}
