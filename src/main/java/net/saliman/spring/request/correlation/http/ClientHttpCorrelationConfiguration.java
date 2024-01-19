/*
 * Copyright (c) 2017 the original author or authors
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
package net.saliman.spring.request.correlation.http;

import net.saliman.spring.request.correlation.support.RequestCorrelationProperties;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.client.ClientHttpRequestInterceptor;
import org.springframework.http.client.support.InterceptingHttpAccessor;
import org.springframework.ui.ModelMap;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.context.request.WebRequestInterceptor;

import java.util.ArrayList;
import java.util.List;

/**
 * Configures any {@link org.springframework.web.client.RestTemplate} bean by adding additional request interceptor.
 *
 * @author Jakub Narloch
 */
@Configuration
@ConditionalOnClass(InterceptingHttpAccessor.class)
@ConditionalOnProperty(value = "request.correlation.client.http.enabled", matchIfMissing = true)
public class ClientHttpCorrelationConfiguration {

    @Autowired(required = false)
    private List<InterceptingHttpAccessor> clients = new ArrayList<>();

    @Bean
    public InitializingBean clientsCorrelationInitializer(final RequestCorrelationProperties properties) {

        return new InitializingBean() {
            @Override
            public void afterPropertiesSet() throws Exception {

                if(clients != null) {
                    for(InterceptingHttpAccessor client : clients) {
                        final List<ClientHttpRequestInterceptor> interceptors = new ArrayList<>(client.getInterceptors());
                        interceptors.add(new ClientHttpRequestCorrelationInterceptor(properties));
                        client.setInterceptors(interceptors);
                    }
                }
            }
        };
    }

    @Bean
    public WebRequestInterceptor x() {
        return new WebRequestInterceptor() {

            /**
             * Intercept the execution of a request handler <i>before</i> its invocation.
             * <p>Allows for preparing context resources (such as a Hibernate Session)
             * and expose them as request attributes or as thread-local objects.
             *
             * @param request the current web request
             * @throws Exception in case of errors
             */
            @Override
            public void preHandle(WebRequest request) throws Exception {
                System.out.println("HERE!!!!!!!!!!!!!!!!!!!!");
            }

            /**
             * Intercept the execution of a request handler <i>after</i> its successful
             * invocation, right before view rendering (if any).
             * <p>Allows for modifying context resources after successful handler
             * execution (for example, flushing a Hibernate Session).
             *
             * @param request the current web request
             * @param model   the map of model objects that will be exposed to the view
             *                (may be {@code null}). Can be used to analyze the exposed model
             *                and/or to add further model attributes, if desired.
             * @throws Exception in case of errors
             */
            @Override
            public void postHandle(WebRequest request, ModelMap model) throws Exception {

            }

            /**
             * Callback after completion of request processing, that is, after rendering
             * the view. Will be called on any outcome of handler execution, thus allows
             * for proper resource cleanup.
             * <p>Note: Will only be called if this interceptor's {@code preHandle}
             * method has successfully completed!
             *
             * @param request the current web request
             * @param ex      exception thrown on handler execution, if any
             * @throws Exception in case of errors
             */
            @Override
            public void afterCompletion(WebRequest request, Exception ex) throws Exception {

            }
        };
    }
}
