package io.mallang.test.support.web;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpMethod;
import org.springframework.test.web.servlet.assertj.MockMvcTester;
import org.springframework.test.web.servlet.assertj.MvcTestResult;
import org.springframework.util.MultiValueMap;

import static org.springframework.http.HttpMethod.*;
import static org.springframework.http.MediaType.APPLICATION_JSON;

public abstract class WebMvcRequestTestSupport {

    @Autowired
    protected MockMvcTester client;

    @Autowired
    protected ObjectMapper objectMapper;

    protected MvcTestResult postJson(String uri, Object request) throws JsonProcessingException {
        return requestJson(POST, uri, request);
    }

    protected MvcTestResult putJson(String uri, Object request) throws JsonProcessingException {
        return requestJson(PUT, uri, request);
    }

    protected MvcTestResult patchJson(String uri, Object request) throws JsonProcessingException {
        return requestJson(PATCH, uri, request);
    }

    private MvcTestResult requestJson(HttpMethod method, String uri, Object request) throws JsonProcessingException {
        return client.method(method)
                     .uri(uri)
                     .contentType(APPLICATION_JSON)
                     .content(objectMapper.writeValueAsString(request))
                     .exchange();
    }

    protected MvcTestResult getWithParams(String uri, MultiValueMap<String, String> params) {
        return client.get()
                     .uri(uri)
                     .params(params).exchange();
    }
}
