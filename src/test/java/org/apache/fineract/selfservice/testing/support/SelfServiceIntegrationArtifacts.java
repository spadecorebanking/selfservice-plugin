/**
 * Copyright since 2026 Mifos Initiative
 *
 * <p>This Source Code Form is subject to the terms of the Mozilla Public License, v. 2.0. If a copy
 * of the MPL was not distributed with this file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */
package org.apache.fineract.selfservice.testing.support;

import java.nio.file.Files;
import java.nio.file.Path;

final class SelfServiceIntegrationArtifacts {

  private SelfServiceIntegrationArtifacts() {}

  static Path requirePluginJar(String configuredPath) {
    if (configuredPath == null || configuredPath.isBlank()) {
      throw new IllegalStateException(
          "Missing selfservice.it.plugin-jar. Run integration tests through Maven verify.");
    }
    Path jar = Path.of(configuredPath).toAbsolutePath();
    if (!Files.isRegularFile(jar)) {
      throw new IllegalStateException("Packaged self-service plugin JAR does not exist: " + jar);
    }
    return jar;
  }
}
