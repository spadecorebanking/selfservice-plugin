/**
 * Copyright since 2026 Mifos Initiative
 *
 * <p>This Source Code Form is subject to the terms of the Mozilla Public License, v. 2.0. If a copy
 * of the MPL was not distributed with this file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */
package org.apache.fineract.selfservice.testing.support;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import jakarta.persistence.EntityManagerFactory;
import jakarta.persistence.spi.PersistenceUnitTransactionType;
import java.io.IOException;
import java.nio.file.Path;
import java.util.Map;
import org.eclipse.persistence.jpa.PersistenceProvider;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;
import org.springframework.orm.jpa.persistenceunit.MutablePersistenceUnitInfo;

class SelfServicePersistenceCompatibilityTest {

  @TempDir Path directory;

  @Test
  void initializesJpaProviderWithTheManagedPersistenceApi() throws IOException {
    MutablePersistenceUnitInfo unit =
        new MutablePersistenceUnitInfo() {
          @Override
          public ClassLoader getClassLoader() {
            return SelfServicePersistenceCompatibilityTest.class.getClassLoader();
          }

          @Override
          public ClassLoader getNewTempClassLoader() {
            return getClassLoader();
          }
        };
    unit.setPersistenceUnitName("selfservice-runtime-compatibility");
    unit.setPersistenceUnitRootUrl(directory.toUri().toURL());
    unit.setTransactionType(PersistenceUnitTransactionType.RESOURCE_LOCAL);
    unit.setExcludeUnlistedClasses(true);

    // Initialize the provider as Fineract does, without opening a database connection.
    EntityManagerFactory factory =
        new PersistenceProvider()
            .createContainerEntityManagerFactory(
                unit,
                Map.of(
                    "eclipselink.weaving", "false",
                    "eclipselink.target-database",
                    "org.eclipse.persistence.platform.database.PostgreSQLPlatform"));
    assertNotNull(factory);
    try {
      assertTrue(factory.isOpen());
    } finally {
      factory.close();
    }
  }
}
