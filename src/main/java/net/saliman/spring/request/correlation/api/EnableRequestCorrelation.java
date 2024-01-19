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
package net.saliman.spring.request.correlation.api;

import net.saliman.spring.request.correlation.feign.FeignCorrelationConfiguration;
import net.saliman.spring.request.correlation.http.ClientHttpCorrelationConfiguration;
import net.saliman.spring.request.correlation.filter.RequestCorrelationConfiguration;
import net.saliman.spring.request.correlation.webclient.WebClientCorrelationConfiguration;
import org.springframework.context.annotation.Import;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.reactive.function.client.WebClient;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Inherited;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Enables automatic request correlation by assigning each incoming request to an application a
 * session identifier and unique request identifier.  These identifiers will then be propagated to
 * outgoing requests from the application through 'X-Session-Id' and 'X-Request-Id' headers.
 * <p>
 * By default, the session identifier will be HTTP session id, and the request id will be generated
 * using a random {@code UUID}.
 * <p>
 * The header will be automatically propagated through any Spring configured {@link RestTemplate}
 * bean,  {@link WebClient} (through a {@link WebClient.Builder} bean), or Feign client.
 *
 * @author Jakub Narloch
 * @author Steven C. Saliman
 *
 * @see RequestCorrelation
 * @see RequestCorrelationConfiguration
 * @see CorrelationIdGenerator
 */
@Documented
@Inherited
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.TYPE)
@Import({
        RequestCorrelationConfiguration.class,
        ClientHttpCorrelationConfiguration.class,
        WebClientCorrelationConfiguration.class,
        FeignCorrelationConfiguration.class
})
public @interface EnableRequestCorrelation {

}
