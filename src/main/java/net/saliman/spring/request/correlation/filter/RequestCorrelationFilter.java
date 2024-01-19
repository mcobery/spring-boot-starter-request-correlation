/*
 * Copyright (c) 2017 the original author or authors
 * <p/>
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * <p/>
 * http://www.apache.org/licenses/LICENSE-2.0
 * <p/>
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package net.saliman.spring.request.correlation.filter;

import jakarta.servlet.Filter;
import jakarta.servlet.FilterChain;
import jakarta.servlet.FilterConfig;
import jakarta.servlet.ServletException;
import jakarta.servlet.ServletRequest;
import jakarta.servlet.ServletResponse;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletRequestWrapper;
import jakarta.servlet.http.HttpServletResponse;
import net.saliman.spring.request.correlation.api.CorrelationIdGenerator;
import net.saliman.spring.request.correlation.api.RequestCorrelation;
import net.saliman.spring.request.correlation.api.RequestCorrelationInterceptor;
import net.saliman.spring.request.correlation.support.RequestCorrelationConsts;
import net.saliman.spring.request.correlation.support.RequestCorrelationProperties;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.util.Assert;
import org.springframework.util.ObjectUtils;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Enumeration;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

/**
 * The entry point for the request correlation. This filter intercepts any
 * incoming request and in case that it
 * does not contain the correlation header creates new identifier and stores it
 * both as the request header
 * and also as a attribute.
 *
 * @author Jakub Narloch
 * @author Souris Stathis
 */
public class RequestCorrelationFilter implements Filter {

	/**
	 * Logger instance used by this class.
	 */
	private static final Logger logger = LoggerFactory.getLogger(RequestCorrelationFilter.class);

	/**
	 * The request generator used for generating new identifiers.
	 */
	private final CorrelationIdGenerator correlationIdGenerator;

	/**
	 * List of optional interceptors.
	 */
	private final List<RequestCorrelationInterceptor> interceptors;

	/**
	 * The request correlation properties.
	 */
	private final RequestCorrelationProperties properties;

	/**
	 * Creates new instance of {@link CorrelationIdGenerator} class.
	 *
	 * @param correlationIdGenerator the request id generator
	 * @param interceptors the correlation interceptors
	 * @param properties the request properties
	 * @throws IllegalArgumentException if {@code requestIdGenerator} is {@code
	 * null}
	 * or {@code interceptors} is {@code null}
	 * or {@code properties} is {@code null}
	 */
	public RequestCorrelationFilter(CorrelationIdGenerator correlationIdGenerator,
	                                List<RequestCorrelationInterceptor> interceptors, RequestCorrelationProperties properties) {
		Assert.notNull(correlationIdGenerator, "Parameter 'correlationIdGenerator' can not be null.");
		Assert.notNull(interceptors, "Parameter 'interceptors' can not be null.");
		Assert.notNull(properties, "Parameter 'properties' can not be null.");

		this.correlationIdGenerator = correlationIdGenerator;
		this.interceptors = interceptors;
		this.properties = properties;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void init(FilterConfig filterConfig) throws ServletException {
		// empty method
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void destroy() {
		// empty method
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) throws IOException, ServletException {

		if ( request instanceof HttpServletRequest && response instanceof HttpServletResponse ) {
			doHttpFilter((HttpServletRequest)request, (HttpServletResponse)response, chain);
		} else {
			// otherwise just pass through
			chain.doFilter(request, response);
		}
	}

	/**
	 * Performs 'enrichment' of incoming HTTP request.
	 *
	 * @param request the http servlet request
	 * @param response the http servlet response
	 * @param chain the filter processing chain
	 * @throws IOException if any error occurs
	 * @throws ServletException if any error occurs
	 */
	private void doHttpFilter(HttpServletRequest request, HttpServletResponse response, FilterChain chain) throws IOException, ServletException {

		// retrieves the correlationId
		String sessionId = getSessionId(request);

		// verifies the correlation id was set
		if ( StringUtils.isBlank(sessionId) ) {
			sessionId = generateSessionId(request);
			logger.debug("Session correlation id was not present, generating new one: {}", sessionId);
		}

		String requestId = getRequestId(request);
		// verifies the correlation id was set
		if ( StringUtils.isBlank(requestId) ) {
			requestId = generateRequestId(request);
			logger.debug("Request correlation id was not present, generating new one: {}", requestId);
		}

		// triggers interceptors
		triggerInterceptors(sessionId, requestId);

		// instantiates new request correlation
		final RequestCorrelation requestCorrelation = new DefaultRequestCorrelation(sessionId, requestId);

		// populates the attribute
		final ServletRequest req = enrichRequest(request, requestCorrelation);

		try {
			// proceeds with execution
			chain.doFilter(req, response);
		} finally {
			triggerInterceptorsCleanup(sessionId, requestId);
		}
	}

	/**
	 * Retrieves the correlation id from the request, if present.
	 *
	 * @param request the http servlet request
	 * @return the correlation id
	 */
	private String getSessionId(HttpServletRequest request) {

		return request.getHeader(properties.getSessionHeaderName());
	}

	/**
	 * Retrieves the correlation id from the request, if present.
	 *
	 * @param request the http servlet request
	 * @return the correlation id
	 */
	private String getRequestId(HttpServletRequest request) {

		return request.getHeader(properties.getRequestHeaderName());
	}

	/**
	 * Generates new correlation id.
	 *
	 * @return the correlation id
	 */
	private String generateSessionId(HttpServletRequest request) {

		return correlationIdGenerator.generateSessionId(request);
	}

	/**
	 * Generates new correlation id.
	 *
	 * @return the correlation id
	 */
	private String generateRequestId(HttpServletRequest request) {

		return correlationIdGenerator.generateRequestId(request);
	}

	/**
	 * Triggers the configured interceptors.
	 *
	 * @param sessionId the correlation id
	 */
	private void triggerInterceptors(String sessionId, String requestId) {

		for ( RequestCorrelationInterceptor interceptor : interceptors ) {
			interceptor.afterCorrelationIdSet(sessionId, requestId);
		}
	}

	/**
	 * Triggers the configured interceptors cleanUp methods.
	 *
	 * @param sessionId the correlation id
	 */
	private void triggerInterceptorsCleanup(String sessionId, String requestId) {

		for ( RequestCorrelationInterceptor interceptor : interceptors ) {
			interceptor.cleanUp(sessionId, requestId);
		}
	}

	/**
	 * "Enriches" the request.
	 *
	 * @param request the http servlet request
	 * @param correlationId the correlation id
	 * @return the servlet request
	 */
	private ServletRequest enrichRequest(HttpServletRequest request, RequestCorrelation correlationId) {

		final CorrelatedServletRequest req = new CorrelatedServletRequest(request);
		req.setAttribute(RequestCorrelationConsts.ATTRIBUTE_NAME, correlationId);
		req.setHeader(properties.getSessionHeaderName(), correlationId.getSessionId());
		req.setHeader(properties.getRequestHeaderName(), correlationId.getRequestId());
		return req;
	}

	/**
	 * An http servlet wrapper that allows to register additional HTTP headers.
	 *
	 * @author Jakub Narloch
	 */
	private static class CorrelatedServletRequest extends HttpServletRequestWrapper {

		/**
		 * Map with additional customizable headers.
		 */
		private final Map<String, String> additionalHeaders = new ConcurrentHashMap<>();

		/**
		 * Creates a ServletRequest adaptor wrapping the given request object.
		 *
		 * @param request The request to wrap
		 * @throws IllegalArgumentException if the request is null
		 */
		public CorrelatedServletRequest(HttpServletRequest request) {
			super(request);
		}

		/**
		 * Sets the header value.
		 *
		 * @param key the header name
		 * @param value the header value
		 */
		public void setHeader(String key, String value) {

			this.additionalHeaders.put(key, value);
		}

		@Override
		public String getHeader(String name) {
			if ( additionalHeaders.containsKey(name) ) {
				return additionalHeaders.get(name);
			}
			return super.getHeader(name);
		}

		@Override
		public Enumeration<String> getHeaders(String name) {

			final List<String> values = new ArrayList<>();
			if ( additionalHeaders.containsKey(name) ) {
				values.add(additionalHeaders.get(name));
			} else {
				values.addAll(Collections.list(super.getHeaders(name)));
			}
			return Collections.enumeration(values);
		}

		@Override
		public Enumeration<String> getHeaderNames() {

			final Set<String> names = new HashSet<>();
			names.addAll(additionalHeaders.keySet());
			names.addAll(Collections.list(super.getHeaderNames()));
			return Collections.enumeration(names);
		}
	}
}
