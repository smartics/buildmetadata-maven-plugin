/*
 * Copyright 2006-2009 smartics, Kronseder & Reiner GmbH
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
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
package de.smartics.maven.plugin.buildmetadata.updater;

import java.util.Properties;

import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugin.logging.Log;
import org.apache.maven.project.MavenProject;

/**
 * Interface to update POMs.
 * 
 * @author <a href="mailto:robert.reiner@smartics.de">Robert Reiner</a>
 * @version $Revision$
 */
public interface PomUpdater
{
  // ********************************* Fields *********************************

  // --- constants ------------------------------------------------------------

  // ****************************** Initializer *******************************

  // ****************************** Inner Classes *****************************

  // ********************************* Methods ********************************

  // --- get&set --------------------------------------------------------------

  /**
   * Sets the logger to use by the updater.
   * 
   * @param log the logger to use.
   */
  void setLog(final Log log);

  // --- business -------------------------------------------------------------

  /**
   * Updates the POM with the new properties.
   * 
   * @param project the access to project information.
   * @param buildMetaDataProperties the information of the build to add to the
   *        POM.
   * @throws MojoExecutionException on any problem encountered while creating
   *         the backup or while updating the project properties (by writing the
   *         POM).
   */
  void updatePom(MavenProject project, Properties buildMetaDataProperties)
      throws MojoExecutionException;

  // --- object basics --------------------------------------------------------

}
