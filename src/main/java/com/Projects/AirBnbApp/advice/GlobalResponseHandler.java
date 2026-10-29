
package com.Projects.AirBnbApp.advice;

import org.jspecify.annotations.Nullable;
import org.springframework.core.MethodParameter;
import org.springframework.http.MediaType;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.http.server.ServerHttpRequest;
import org.springframework.http.server.ServerHttpResponse;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.servlet.mvc.method.annotation.ResponseBodyAdvice;

import java.util.List;

@RestControllerAdvice
public class GlobalResponseHandler
        implements ResponseBodyAdvice<Object> {

    private static final List<String> ALLOWED_ROUTES =
            List.of("/v3/api-docs", "/actuator", "/error");

    @Override
    public boolean supports(
            MethodParameter returnType,
            Class<? extends HttpMessageConverter<?>> converterType) {
        return true;
    }

    @Override
    public @Nullable Object beforeBodyWrite(
            @Nullable Object body,
            MethodParameter returnType,
            MediaType selectedContentType,
            Class<? extends HttpMessageConverter<?>> selectedConverterType,
            ServerHttpRequest request,
            ServerHttpResponse response) {

        String path = request.getURI().getPath();

        boolean isAllowed = ALLOWED_ROUTES.stream()
                .anyMatch(path::contains);

        if (body == null || body instanceof ApiResponce<?> || isAllowed) {
            return body;
        }

        return new ApiResponce<>(body);
    }
}
