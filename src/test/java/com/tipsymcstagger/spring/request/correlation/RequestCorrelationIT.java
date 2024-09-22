/*
 * Copyright (c) 2015-2024 the original author or authors
 *
 * Licensed under the Apache License, Version 2.0 (the "License");  you may not use this file except
 * in compliance with the License. You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software distributed under the License
 * is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express
 * or implied.  See the License for the specific language governing permissions and limitations
 * under the License.
 */
package com.tipsymcstagger.spring.request.correlation;

import static org.assertj.core.api.Assertions.assertThat;

import com.tipsymcstagger.spring.request.correlation.demo.DemoApplication;
import com.tipsymcstagger.spring.request.correlation.demo.DemoConfiguration;
import com.tipsymcstagger.spring.request.correlation.support.RequestCorrelationConsts;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.web.client.RestTemplate;

/**
 * Integration test to make sure we get correlating ids where we need them.
 *
 * @author Jakub Narloch
 */
@SpringBootTest(
    classes = {DemoApplication.class, DemoConfiguration.class},
    webEnvironment = SpringBootTest.WebEnvironment.DEFINED_PORT)
@ActiveProfiles("test")
public class RequestCorrelationIT {

  @Value("${local.server.port}")
  private int port;

  // Rest Template that can make our requests from outside the Spring application.  It is not a
  // bean because we don't need (and don't want) headers being added to requests coming from the
  // tests.
  private RestTemplate restTemplate;

  @BeforeEach
  public void setUp() {
    restTemplate = new RestTemplate();
  }

  /**
   * Calls the "ids" URL with no headers to prove that we'll set headers to a new values. We can't
   * really verify much except that the ids got set.
   */
  @Test
  public void getIdsNoHeaders() {
    final String response = restTemplate.getForObject(url("/ids"), String.class);
    assertThat(response).isNotNull();
    final String[] responseArr = response.split(":");
    assertThat(responseArr).hasSize(2).doesNotContainNull();
  }

  /**
   * Calls the "ids" URL with existing correlating ids to prove that we won't replace them with new
   * ones.
   */
  @Test
  public void getIdsWithHeaders() {
    HttpHeaders headers = new HttpHeaders();
    headers.add(RequestCorrelationConsts.SESSION_HEADER_NAME, "customSessionId");
    headers.add(RequestCorrelationConsts.REQUEST_HEADER_NAME, "customRequestId");
    HttpEntity<Void> request = new HttpEntity<>(headers);
    String response =
        restTemplate.exchange(url("/ids"), HttpMethod.GET, request, String.class, 1).getBody();
    assertThat(response).isNotNull().isEqualTo("customSessionId:customRequestId");
  }

  /**
   * Calls the rest template endpoint with no headers. That endpoint then calls the "ids" endpoint
   * using a RestTemplate to prove that the headers propagate on outgoing RestTemplate requests. The
   * heart of the test is in the rest template endpoint which verifies the contents of the ids. We
   * know we're good as long as we get an "OK" status and not an error.
   */
  @Test
  public void testRestTemplate() {
    final String response = restTemplate.getForObject(url("/rest"), String.class);
    assertThat(response).isNotNull();
    final String[] responseArr = response.split(":");
    assertThat(responseArr).hasSize(2).doesNotContainNull();
  }

  /**
   * Calls the WebClient endpoint with no headers. That endpoint then calls the "ids" endpoint using
   * a WebClient to prove that the headers propagate on outgoing WebClient requests. The heart of
   * the test is in the WebClient endpoint which verifies the contents of the ids. We know we're
   * good as long as we get an "OK" status and not an error.
   */
  @Test
  public void testWebClient() {
    final String response = restTemplate.getForObject(url("/webclient"), String.class);
    assertThat(response).isNotNull();
    final String[] responseArr = response.split(":");
    assertThat(responseArr).hasSize(2).doesNotContainNull();
  }

  /**
   * Calls the Feign endpoint with no headers. That endpoint then calls the "ids" endpoint using a
   * Feign client to prove that the headers propagate on outgoing Feign client requests. The heart
   * of the test is in the Feign endpoint which verifies the contents of the ids. We know we're good
   * as long as we get an "OK" status and not an error.
   */
  @Test
  public void testFeign() {
    final String response = restTemplate.getForObject(url("/feign"), String.class);
    assertThat(response).isNotNull();
    final String[] responseArr = response.split(":");
    assertThat(responseArr).hasSize(2).doesNotContainNull();
  }

  private String url(String path) {

    return String.format("http://127.0.0.1:%d/%s", port, path);
  }
}
