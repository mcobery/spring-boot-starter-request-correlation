package net.saliman.spring.request.correlation.demo;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

/**
 * A Feign client we can use to prove that correlating ids propagate to it.
 *
 * @author Steven C. Saliman
 */
@FeignClient(name="local", url = "localhost:10344")
interface DemoFeignClient {

    @RequestMapping(value = "/ids", method = RequestMethod.GET)
    String getCorrelatingIds();
}
