/**
 * Copyright since 2026 Mifos Initiative
 *
 * <p>This Source Code Form is subject to the terms of the Mozilla Public License, v. 2.0. If a copy
 * of the MPL was not distributed with this file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */
package org.apache.fineract.selfservice.testing.support;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.mock;

import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.jar.JarFile;
import org.apache.fineract.kyc.repository.KycVerificationRepository;
import org.apache.fineract.kyc.service.KycStatusDerivationService;
import org.apache.fineract.kyc.service.KycVerificationServiceImpl;
import org.apache.fineract.portfolio.client.domain.ClientRepositoryWrapper;
import org.apache.fineract.selfservice.config.SavingsPluginJacksonConfiguration;
import org.junit.jupiter.api.Test;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;

class SavingsPluginCompatibilityTest {

  @Test
  void fineractCommonsLangMethodsAreAvailable() throws Exception {
    var fallback = org.apache.commons.lang3.ObjectUtils.class.getMethod(
        "getIfNull", Object.class, Object.class);
    assertEquals("fallback", fallback.invoke(null, null, "fallback"));
    var mutable = new org.apache.commons.lang3.mutable.MutableObject<>("value");
    var getter = org.apache.commons.lang3.mutable.Mutable.class.getMethod("get");
    assertEquals("value", getter.invoke(mutable));
  }

  @Test
  void savingsPluginSignaturesResolveWithTheApplicationRuntime() throws Exception {
    Path jar =
        Path.of(
            KycVerificationServiceImpl.class.getProtectionDomain()
                .getCodeSource().getLocation().toURI());
    List<String> failures = new ArrayList<>();
    ClassLoader loader = getClass().getClassLoader();
    try (JarFile archive = new JarFile(jar.toFile())) {
      for (var entry : archive.stream()
          .filter(e -> e.getName().startsWith("org/apache/fineract/")
              && e.getName().endsWith(".class")
              && !e.getName().endsWith("package-info.class"))
          .toList()) {
        String name = entry.getName().replace('/', '.').replaceAll("\\.class$", "");
        try {
          Class<?> type = Class.forName(name, false, loader);
          type.getDeclaredConstructors();
          type.getDeclaredMethods();
          type.getDeclaredFields();
        } catch (LinkageError error) {
          failures.add(name + ": " + error);
        }
      }
    }
    assertTrue(
        failures.isEmpty(),
        () -> "Unresolved savings plugin classes:\n" + String.join("\n", failures));
  }

  @Test
  void kycServiceWiresWithBothJacksonVersions() throws Exception {
    try (var context = new AnnotationConfigApplicationContext()) {
      context.registerBean(
          com.fasterxml.jackson.databind.ObjectMapper.class,
          () -> new com.fasterxml.jackson.databind.ObjectMapper());
      context.registerBean(
          KycVerificationRepository.class, () -> mock(KycVerificationRepository.class));
      context.registerBean(
          ClientRepositoryWrapper.class, () -> mock(ClientRepositoryWrapper.class));
      context.registerBean(
          KycStatusDerivationService.class, () -> mock(KycStatusDerivationService.class));
      context.register(SavingsPluginJacksonConfiguration.class, KycVerificationServiceImpl.class);
      context.refresh();

      assertNotNull(context.getBean(KycVerificationServiceImpl.class));
      var modern = context.getBean(tools.jackson.databind.ObjectMapper.class);
      var legacy = context.getBean(com.fasterxml.jackson.databind.ObjectMapper.class);
      String json = legacy.writeValueAsString(java.util.Map.of("clientId", 42));
      assertEquals(42, modern.readTree(json).get("clientId").asInt());
    }
  }

}
