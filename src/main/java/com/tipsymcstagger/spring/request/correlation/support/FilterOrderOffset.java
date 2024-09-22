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
package com.tipsymcstagger.spring.request.correlation.support;

import org.springframework.core.Ordered;

/**
 * This enum defines valid offsets for the filter order. The actual filter order will be the stated
 * order plus the chosen offset. For example, a filter order of 102 and an offset of
 * HIGHEST_PRECEDENCE will result in a filter that is 102 after the highest precedence filter.
 *
 * @author Steven C. Saliman
 */
public enum FilterOrderOffset {
  HIGHEST_PRECEDENCE(Ordered.HIGHEST_PRECEDENCE),
  LOWEST_PRECEDENCE(Ordered.LOWEST_PRECEDENCE),
  ZERO(0);

  public final int offset;

  FilterOrderOffset(int offset) {
    this.offset = offset;
  }
}
