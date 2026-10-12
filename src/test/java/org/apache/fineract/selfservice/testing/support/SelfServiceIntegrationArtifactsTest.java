/**
 * Copyright since 2026 Mifos Initiative
 *
 * <p>This Source Code Form is subject to the terms of the Mozilla Public License, v. 2.0. If a copy
 * of the MPL was not distributed with this file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */
package org.apache.fineract.selfservice.testing.support;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

class SelfServiceIntegrationArtifactsTest {

  @TempDir Path directory;

  @Test
  void usesMavenArtifactPathRegardlessOfVersion() throws IOException {
    Path jar = Files.createFile(directory.resolve("selfservice-plugin-1.16.0-SNAPSHOT.jar"));
    assertEquals(
        jar.toAbsolutePath(), SelfServiceIntegrationArtifacts.requirePluginJar(jar.toString()));
  }

  @Test
  void rejectsMissingArtifactBeforeStartingContainers() {
    assertThrows(
        IllegalStateException.class,
        () ->
            SelfServiceIntegrationArtifacts.requirePluginJar(
                directory.resolve("missing.jar").toString()));
    assertThrows(
        IllegalStateException.class,
        () -> SelfServiceIntegrationArtifacts.requirePluginJar(directory.toString()));
  }

  @Test
  void requiresMavenArtifactConfiguration() {
    assertThrows(
        IllegalStateException.class,
        () -> SelfServiceIntegrationArtifacts.requirePluginJar(null));
    assertThrows(
        IllegalStateException.class,
        () -> SelfServiceIntegrationArtifacts.requirePluginJar(" "));
  }
}
