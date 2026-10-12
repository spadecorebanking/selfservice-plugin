/**
 * Copyright since 2026 Mifos Initiative
 *
 * <p>This Source Code Form is subject to the terms of the Mozilla Public License, v. 2.0. If a copy
 * of the MPL was not distributed with this file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */
package org.apache.fineract.selfservice.config;

import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import tools.jackson.databind.ObjectMapper;
import tools.jackson.databind.json.JsonMapper;

/** Supplies the Jackson 3 mapper required by savings-plugin's KYC service on Boot 3. */
@Configuration(proxyBeanMethods = false)
public class SavingsPluginJacksonConfiguration {

  @Bean
  @ConditionalOnMissingBean(ObjectMapper.class)
  public ObjectMapper savingsPluginObjectMapper() {
    return JsonMapper.builder().findAndAddModules().build();
  }
}
