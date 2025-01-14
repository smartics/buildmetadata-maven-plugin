/*
 * Copyright 2006-2025 smartics, Kronseder & Reiner GmbH
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
package de.smartics.maven.plugin.buildmetadata.data;

import org.apache.maven.plugin.MojoExecutionException;

import java.util.Properties;

/**
 * Interface to add build meta data information.
 */
public interface MetaDataProvider {
  // ********************************* Fields *********************************

  // --- constants ------------------------------------------------------------

  // ****************************** Initializer *******************************

  // ****************************** Inner Classes *****************************

  // ********************************* Methods ********************************

  // --- get&set --------------------------------------------------------------

  // --- business -------------------------------------------------------------

  /**
   * Adds the information as build properties.
   *
   * @param buildMetaDataProperties the build meta data properties to add to.
   * @throws MojoExecutionException on any problem encountered.
   */
  void provideBuildMetaData(Properties buildMetaDataProperties)
      throws MojoExecutionException;

  // --- object basics --------------------------------------------------------

}
