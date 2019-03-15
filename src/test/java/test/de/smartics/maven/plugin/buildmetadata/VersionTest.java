/*
 * Copyright 2006-2019 smartics, Kronseder & Reiner GmbH
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not
 * use this file except in compliance with the License. You may obtain a copy of
 * the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
 * License for the specific language governing permissions and limitations under
 * the License.
 */
package test.de.smartics.maven.plugin.buildmetadata;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import org.apache.maven.artifact.versioning.ArtifactVersion;
import org.apache.maven.artifact.versioning.DefaultArtifactVersion;
import org.junit.Test;

/**
 * Tests our assumption with the parsing of version by the
 * DefaultArtifactVersion class.
 */
public class VersionTest {
  // ********************************* Fields *********************************

  // --- constants ------------------------------------------------------------

  // --- members --------------------------------------------------------------

  // ****************************** Inner Classes *****************************

  // ********************************* Methods ********************************

  // --- prepare --------------------------------------------------------------

  // --- helper ---------------------------------------------------------------

  // --- tests ----------------------------------------------------------------

  @SuppressWarnings("unchecked")
  @Test
  public void compare() {
    final ArtifactVersion v1 =
        new DefaultArtifactVersion("1.2.3-20190315153459");
    final ArtifactVersion v2 =
        new DefaultArtifactVersion("1.2.3-20190315153460");

    assertEquals(v1.getMajorVersion(), 1);
    assertEquals(v1.getMinorVersion(), 2);
    assertEquals(v1.getIncrementalVersion(), 3);
    assertEquals(v1.getBuildNumber(), 0);
    assertEquals(v1.getQualifier(), "20190315153459");
    assertTrue(v1.compareTo(v2) < 0);
  }
}
