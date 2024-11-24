package com.example.flowerstore.config;

import com.example.flowerstore.dto.response.ApiResponse;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.core.MethodParameter;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.server.ServerHttpRequest;
import org.springframework.http.server.ServerHttpResponse;
import org.springframework.http.server.ServletServerHttpResponse;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.servlet.mvc.method.annotation.ResponseBodyAdvice;

@ControllerAdvice
public class ConfigResponse implements ResponseBodyAdvice<Object> {

    @Override
    public boolean supports(MethodParameter methodParameter, Class converterType) {
        String requestUri = methodParameter.getContainingClass().getName();
        return !requestUri.contains("springdoc") && 
               !requestUri.contains("swagger") && 
               !requestUri.contains("openapi");
    }

    @Override
    public Object beforeBodyWrite(Object body, MethodParameter returnType, MediaType selectedContentType,
                                Class selectedConverterType, ServerHttpRequest request,
                                ServerHttpResponse response) {
        if (body instanceof ApiResponse || 
            request.getURI().getPath().contains("api-docs") || 
            request.getURI().getPath().contains("swagger-ui")) {
            return body;
        }

        int status = HttpStatus.OK.value();
        if (response instanceof ServletServerHttpResponse) {
            HttpServletResponse servletResponse = ((ServletServerHttpResponse) response).getServletResponse();
            status = servletResponse.getStatus();
        }

        if (status >= 400) {
            return body;
        }

        return new ApiResponse<>(
                status,
                "Call API successfully",
                body
        );
    }
}
