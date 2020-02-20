/*
 * Copyright Camunda Services GmbH and/or licensed to Camunda Services GmbH
 * under one or more contributor license agreements. See the NOTICE file
 * distributed with this work for additional information regarding copyright
 * ownership. Camunda licenses this file to you under the Apache License,
 * Version 2.0; you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.camunda.bpm.run.test.config.cors;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.List;

import org.camunda.bpm.run.property.CamundaBpmRunCorsProperty;
import org.junit.Test;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.TestPropertySource;

@org.junit.Ignore
@TestPropertySource(properties = {CamundaBpmRunCorsProperty.PREFIX + ".allowed-origins=http://other.origin:8081"})
public class CorsConfigurationEnabledAllowedOriginConfiguredTest extends AbstractCorsConfigurationTest {

  @Test
  public void shouldPassSameOriginRequest() {
    // given
    // same origin
    String origin = "http://localhost:" + localPort;

    HttpHeaders headers = new HttpHeaders();
    headers.add(HttpHeaders.ORIGIN, origin);

    // when
    ResponseEntity<List> response = testRestTemplate.exchange("/rest/task", HttpMethod.GET, new HttpEntity<>(headers), List.class);

    // then
    assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
    assertThat(response.getHeaders().getAccessControlAllowOrigin()).contains(origin);
  }

  @Test
  public void shouldFailCrossOriginRequestFromNotAllowedOrigin() {
    // given
    // cross origin and not allowed
    String origin = "http://other.origin";

    HttpHeaders headers = new HttpHeaders();
    headers.add("Origin", origin);

    // when
    ResponseEntity<List> response = testRestTemplate.exchange("/rest/task", HttpMethod.GET, new HttpEntity<>(headers), List.class);

    // then
    assertThat(response.getStatusCode()).isEqualTo(HttpStatus.FORBIDDEN);
    assertThat(response.getHeaders().getAccessControlAllowOrigin()).isNull();
  }

  @Test
  public void shouldPassCrossOriginRequestFromAllowedOrigin() {
    // given
    // cross origin but allowed
    String origin = "http://other.origin:8081";

    HttpHeaders headers = new HttpHeaders();
    headers.add("Origin", origin);

    // when
    ResponseEntity<List> response = testRestTemplate.exchange("/rest/task", HttpMethod.GET, new HttpEntity<>(headers), List.class);

    // then
    assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
    assertThat(response.getHeaders().getAccessControlAllowOrigin()).contains(origin);
  }
}