package nextstep.jwp.http.entity;

import java.util.Locale;

public enum HttpMethod {
    GET, POST, PUT, DELETE, PATCH;

    public static HttpMethod of(String methodName) {
        return HttpMethod.valueOf(methodName.toUpperCase(Locale.ROOT));
    }

    public boolean isSame(String methodName) {
        return name().equals(methodName.toUpperCase(Locale.ROOT));
    }
}
