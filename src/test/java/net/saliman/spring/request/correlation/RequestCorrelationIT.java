/*
 * Copyright (c) 2015 the original author or authors
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package net.saliman.spring.request.correlation;

//import com.netflix.loadbalancer.BaseLoadBalancer;
//import com.netflix.loadbalancer.ILoadBalancer;
//import com.netflix.loadbalancer.Server;
import net.saliman.spring.request.correlation.demo.DemoApplication;
import net.saliman.spring.request.correlation.demo.DemoConfiguration;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;
//import org.springframework.cloud.netflix.ribbon.RibbonClient;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.web.client.RestTemplate;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

/**
 * Integration test to make sure we get correlating ids where we need them.
 *
 * @author Jakub Narloch
 */
@SpringBootTest(classes = { DemoApplication.class, DemoConfiguration.class},
        webEnvironment = SpringBootTest.WebEnvironment.DEFINED_PORT)
@RunWith(SpringRunner.class)
public class RequestCorrelationIT {

    @Value("${local.server.port}")
    private int port;

//    @Autowired
    private final RestTemplate restTemplate = new RestTemplate();

    /**
     * Calls the root URL with no headers to prove that we'll set headers.
     */
    @Test
    public void test() {

        // when
        final String response = restTemplate.getForObject(url("/ids"), String.class);
        assertNotNull(response);
        final String[] responseArr = response.split(":");
        assertEquals(2, responseArr.length);
        assertNotNull(responseArr[0]);
        assertNotNull(responseArr[1]);
    }

    // TODO: test where header is set on the way in.  We probably don't need an injected template.

    // TODO: Add WebClient interceptor and tests.

    /**
     * Calls the rest endpoint with no headers to prove that we set headers when we get the call.
     * The controller then calls the root endpoint, this time with headers to prove they cascade.
     */
    @Test
    public void testRestTemplate() {

        // when
        final String response = restTemplate.getForObject(url("/rest"), String.class);
        assertNotNull(response);
        final String[] responseArr = response.split(":");
        assertEquals(2, responseArr.length);
        assertNotNull(responseArr[0]);
        assertNotNull(responseArr[1]);
    }

    /**
     * Calls the rest endpoint with no headers to prove that we set headers when we get the call.
     * The controller then calls the root endpoint, this time with headers to prove they cascade.
     */
    @Test
    public void testWebClient() {

        // when
        final String response = restTemplate.getForObject(url("/webclient"), String.class);
        assertNotNull(response);
        final String[] responseArr = response.split(":");
        assertEquals(2, responseArr.length);
        assertNotNull(responseArr[0]);
        assertNotNull(responseArr[1]);
    }

    /**
     * Calls the rest endpoint with no headers to prove that we set headers when we get the call.
     * The controller then calls the feign client to prove that feign propagates the headers.
     */
    @Test
    public void testFeign() {

        // when
        final String response = restTemplate.getForObject(url("/feign"), String.class);
        assertNotNull(response);
        final String[] responseArr = response.split(":");
        assertEquals(2, responseArr.length);
        assertNotNull(responseArr[0]);
        assertNotNull(responseArr[1]);
    }

    private String url(String path) {

        return String.format("http://127.0.0.1:%d/%s", port, path);
    }

}
