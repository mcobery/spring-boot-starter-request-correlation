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
package net.cobery.spring.request.correlation.generator;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.Test;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpSession;

/**
 * Tests the {@link DefaultIdGenerator} class.
 *
 * @author Jakub Narloch
 * @author Steven C. Saliman
 */
public class DefaultIdGeneratorTest {

  /** "generate" a session id and make sure it comes from the HttpSession. */
  @Test
  public void generateSessionId() {
    final MockHttpServletRequest request = new MockHttpServletRequest();
    String customSessionId = "CUSTOM_SESSION_ID";
    request.setSession(new MockHttpSession(null, customSessionId));

    final String sessionId = new DefaultIdGenerator().generateSessionId(request);

    assertThat(sessionId).isEqualTo(customSessionId);
  }

  /** Generate a new request id and make sure it returns a result. */
  @Test
  public void generateRequestId() {
    final MockHttpServletRequest request = new MockHttpServletRequest();
    request.setSession(new MockHttpSession(null, "customSessionId"));

    final String requestId = new DefaultIdGenerator().generateRequestId(request);

    assertThat(requestId).isNotNull();
  }
}
