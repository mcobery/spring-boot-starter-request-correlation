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
package com.tipsymcstagger.spring.request.correlation.demo;

import com.tipsymcstagger.spring.request.correlation.api.EnableRequestCorrelation;
import com.tipsymcstagger.spring.request.correlation.support.RequestCorrelationConsts;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.cloud.openfeign.EnableFeignClients;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

/**
 * A demo application that integration tests can use to make sure correlating ids work as they
 * should.
 *
 * @author Steven C. Saliman
 */
@RestController
@EnableAutoConfiguration
@EnableRequestCorrelation
@EnableFeignClients
public class DemoApplication {

  @Autowired private RestTemplate template;

  @Autowired private DemoFeignClient feignClient;

  @Autowired private WebClient.Builder webClientBuilder;

  /**
   * A basic endpoint that gets the correlating request headers and returns them in a concatenated
   * string. This can be called directly to make sure the incoming filter sets default ids, or by
   * other rest endpoints to make sure ids propagate to outgoing calls.
   *
   * @param sessionId The session id from the request headers.
   * @param requestId The request id from the request headers.
   * @return the session id and request id as a single string.
   */
  @RequestMapping(value = "/ids", method = RequestMethod.GET)
  public ResponseEntity<String> headerEcho(
      @RequestHeader(value = RequestCorrelationConsts.SESSION_HEADER_NAME) String sessionId,
      @RequestHeader(value = RequestCorrelationConsts.REQUEST_HEADER_NAME) String requestId) {

    return ResponseEntity.ok(sessionId + ":" + requestId);
  }

  /**
   * An endpoint that uses a RestTemplate to call our "ids" URL to make sure the correlating id
   * propagates to the outgoing RestTemplate based requests. It compares the correlating ids from
   * the incoming request header with the ones returned from the "ids" endpoint to prove that the
   * incoming ids were propagated when we made the outgoing request.
   *
   * @param sessionId The session id from the request headers.
   * @param requestId The request id from the request headers.
   * @return OK if all is well, INTERNAL SERVER ERROR, if the correlated ids of the outgoing request
   *     don't match the incoming request.
   */
  @RequestMapping(value = "/rest", method = RequestMethod.GET)
  public ResponseEntity<String> propagateRestTemplate(
      @RequestHeader(value = RequestCorrelationConsts.SESSION_HEADER_NAME) String sessionId,
      @RequestHeader(value = RequestCorrelationConsts.REQUEST_HEADER_NAME) String requestId) {

    final String expectedResponse = sessionId + ":" + requestId;
    // Call the "ids" endpoint to see if the incoming headers are automatically added to the
    // RestTemplate based request.
    final String response = template.getForObject(url("/ids"), String.class);
    // If the response differs, it means the header didn't propagate and the incoming filter
    // assigned new ids.
    if (!expectedResponse.equals(response)) {
      return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
          .body("Correlation id mismatch!");
    }
    return ResponseEntity.ok(response);
  }

  /**
   * An endpoint that uses a WebClient to call our "ids" URL to make sure the correlating id
   * propagates to the outgoing WebClient based requests. It compares the correlating ids from the
   * incoming request header with the ones returned from the "ids" endpoint to prove that the
   * incoming ids were propagated when we made the outgoing request.
   *
   * @param sessionId The session id from the request headers.
   * @param requestId The request id from the request headers.
   * @return OK if all is well, INTERNAL SERVER ERROR, if the correlated ids of the outgoing request
   *     don't match the incoming request.
   */
  @RequestMapping(value = "/webclient", method = RequestMethod.GET)
  public ResponseEntity<String> propagateWebClient(
      @RequestHeader(value = RequestCorrelationConsts.SESSION_HEADER_NAME) String sessionId,
      @RequestHeader(value = RequestCorrelationConsts.REQUEST_HEADER_NAME) String requestId) {

    final String expectedResponse = sessionId + ":" + requestId;
    // Call the "ids" endpoint to see if the incoming headers are automatically added to the
    // WebClient based request.
    WebClient client = webClientBuilder.baseUrl(url("/")).build();
    String response = client.get().uri("/ids").retrieve().bodyToMono(String.class).block();
    // If the response differs, it means the header didn't propagate and the incoming filter
    // assigned new ids.
    if (!expectedResponse.equals(response)) {
      return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
          .body("Correlation id mismatch!");
    }
    return ResponseEntity.ok(response);
  }

  /**
   * An endpoint that uses a Feign client to call our "ids" URL to make sure the correlating id
   * propagates to the outgoing Feign based requests. It compares the correlating ids from the
   * incoming request header with the ones returned from the "ids" endpoint to prove that the
   * incoming ids were propagated when we made the outgoing request.
   *
   * @param sessionId The session id from the request headers.
   * @param requestId The request id from the request headers.
   * @return OK if all is well, INTERNAL SERVER ERROR, if the correlated ids of the outgoing request
   *     don't match the incoming request.
   */
  @RequestMapping(value = "/feign", method = RequestMethod.GET)
  public ResponseEntity<String> propagateFeignClient(
      @RequestHeader(value = RequestCorrelationConsts.SESSION_HEADER_NAME) String sessionId,
      @RequestHeader(value = RequestCorrelationConsts.REQUEST_HEADER_NAME) String requestId) {

    final String expectedResponse = sessionId + ":" + requestId;
    // Call the "ids" endpoint to see if the incoming headers are automatically added to the
    // Feign based request.
    final String response = feignClient.getCorrelatingIds();
    // If the response differs, it means the header didn't propagate and the incoming filter
    // assigned new ids.
    if (!expectedResponse.equals(response)) {
      return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
          .body("Correlation id mismatch!");
    }
    return ResponseEntity.ok(response);
  }

  /**
   * Helper method to set the URL for outgoing requests.
   *
   * @param path the path to add to the base URL of the application.
   * @return a URL to use.
   */
  private String url(String path) {

    return ServletUriComponentsBuilder.fromCurrentRequest().replacePath(path).toUriString();
  }
}
