package net.saliman.spring.request.correlation.demo;

import net.saliman.spring.request.correlation.api.EnableRequestCorrelation;
import net.saliman.spring.request.correlation.support.RequestCorrelationConsts;
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

    @Autowired
    private RestTemplate template;

    @Autowired
    private DemoFeignClient feignClient;

    @Autowired
    private WebClient.Builder webClientBuilder;


    /**
     * A basic endpoint that gets the correlating request headers and returns them in a concatenated
     * string.
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
     * An endpoint that uses a RestTemplate to call our base URL to make sure the correlating id
     * propagates to the Base URL.
     *
     * @param sessionId The session id from the request headers.
     * @param requestId The request id from the request headers.
     * @return OK if all is well.
     */
    @RequestMapping(value = "/rest", method = RequestMethod.GET)
    public ResponseEntity<String> propagateRestTemplate(
            @RequestHeader(value = RequestCorrelationConsts.SESSION_HEADER_NAME) String sessionId,
            @RequestHeader(value = RequestCorrelationConsts.REQUEST_HEADER_NAME) String requestId) {

        final String expectedResponse = sessionId + ":" + requestId;
        String x = url("/ids");
        final String response = template.getForObject(url("/ids"), String.class);
        // If the response differs, it means the header didn't propagate and the incoming filter
        // assigned new ids.
        if ( !expectedResponse.equals(response) ) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Correlation id mismatch!");
        }
        return ResponseEntity.ok(response);
    }

    /**
     * An endpoint that uses a WebClient to call our base URL to make sure the correlating id
     * propagates to the Base URL.
     *
     * @param sessionId The session id from the request headers.
     * @param requestId The request id from the request headers.
     * @return OK if all is well.
     */
    @RequestMapping(value = "/webclient", method = RequestMethod.GET)
    public ResponseEntity<String> propagateWebClient(
            @RequestHeader(value = RequestCorrelationConsts.SESSION_HEADER_NAME) String sessionId,
            @RequestHeader(value = RequestCorrelationConsts.REQUEST_HEADER_NAME) String requestId) {

        final String expectedResponse = sessionId + ":" + requestId;
        WebClient client = webClientBuilder
                .baseUrl(url("/"))
                .build();
        String response = client.get().uri("/ids")
                .retrieve().bodyToMono(String.class)
                .block();
        // If the response differs, it means the header didn't propagate and the incoming filter
        // assigned new ids.
        if ( !expectedResponse.equals(response) ) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Correlation id mismatch!");
        }
        return ResponseEntity.ok(response);
    }


    /**
     * An endpoint that uses a Feign client to call our base URL to make sure the correlating id
     * propagates to the Base URL.
     *
     * @param sessionId The session id from the request headers.
     * @param requestId The request id from the request headers.
     * @return OK if all is well.
     */
    @RequestMapping(value = "/feign", method = RequestMethod.GET)
    public ResponseEntity propagateFeignClient(
            @RequestHeader(value = RequestCorrelationConsts.SESSION_HEADER_NAME) String sessionId,
            @RequestHeader(value = RequestCorrelationConsts.REQUEST_HEADER_NAME) String requestId) {

        final String expectedResponse = sessionId + ":" + requestId;
        final String response = feignClient.getCorrelatingIds();
        // If the response differs, it means the header didn't propagate and the incoming filter
        // assigned new ids.
        if ( !expectedResponse.equals(response) ) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Correlation id mismatch!");
        }
        return ResponseEntity.ok(response);
    }

    private String url(String path) {

        return ServletUriComponentsBuilder.fromCurrentRequest().replacePath(path).toUriString();
    }
}

