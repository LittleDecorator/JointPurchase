package com.acme.util;

import com.acme.annotation.JsonAttribute;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.common.base.Charsets;
import com.google.common.io.CharStreams;
import org.springframework.core.MethodParameter;
import org.springframework.web.bind.support.WebDataBinderFactory;
import org.springframework.web.context.request.NativeWebRequest;
import org.springframework.web.method.support.HandlerMethodArgumentResolver;
import org.springframework.web.method.support.ModelAndViewContainer;

import javax.servlet.http.HttpServletRequest;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.Arrays;
import java.util.Map;

/**
 * Класс предоставляющий доступ к телу запроса.
 * Позволяет получать свойства из POST-запроса аналогично @PathParams
 */

public class CustomResolver implements HandlerMethodArgumentResolver{

    private static final String JSON_BODY_ATTRIBUTE = "JSON_REQUEST_BODY";

    @Override
    public boolean supportsParameter(MethodParameter methodParameter) {
        System.out.println("SupportCheck" + Arrays.toString(methodParameter.getAnnotatedElement().getDeclaredAnnotations()));
        System.out.println(methodParameter.getParameterAnnotation(JsonAttribute.class));
        return methodParameter.hasParameterAnnotation(JsonAttribute.class);
    }

    @Override
    public Object resolveArgument(MethodParameter methodParameter, ModelAndViewContainer modelAndViewContainer, NativeWebRequest nativeWebRequest, WebDataBinderFactory webDataBinderFactory) throws Exception {
        String body = getRequestBody(nativeWebRequest);
        Map<String, Object> map = new ObjectMapper().readValue(body, new TypeReference<Map<String, String>>(){});
        String key = methodParameter.getParameterAnnotation(JsonAttribute.class).value();
        return map.get(key);
    }

    private String getRequestBody(NativeWebRequest webRequest){
        HttpServletRequest servletRequest = webRequest.getNativeRequest(HttpServletRequest.class);
       String jsonBody = (String)servletRequest.getAttribute(JSON_BODY_ATTRIBUTE);

        if(jsonBody == null){
            try{
                String body = CharStreams.toString(new InputStreamReader( servletRequest.getInputStream(), Charsets.UTF_8));
                servletRequest.setAttribute(JSON_BODY_ATTRIBUTE, body);
                return body;
            } catch (IOException ex){
                throw new RuntimeException(ex);
            }
        }
        return jsonBody;
    }
}
