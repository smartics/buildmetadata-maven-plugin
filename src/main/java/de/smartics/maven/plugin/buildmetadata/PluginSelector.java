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
package de.smartics.maven.plugin.buildmetadata;

import org.apache.maven.model.Plugin;

import java.util.List;

/**
 * Selects plugins.
 */
public class PluginSelector {
  // ********************************* Fields *********************************

  // --- constants ------------------------------------------------------------

  // --- members --------------------------------------------------------------

  /**
   * The group identifier of the plugin to match.
   */
  private String groupId;

  /**
   * the artifact identifier of the plugin to match.
   */
  private String artifactId;

  /**
   * The prefix to prepend to the fetched config properties.
   */
  private String keyPrefix;

  /**
   * The list of property names to select from the specified plugin's
   * configuration.
   */
  private List<String> properties;

  // ****************************** Initializer *******************************

  // ****************************** Constructors ******************************

  /**
   * Injection constructor.
   */
  public PluginSelector() {}

  /**
   * Convenience constructor to specify no property name prefix.
   *
   * @param groupId the group identifier of the plugin to match.
   * @param artifactId the artifact identifier of the plugin to match.
   */
  public PluginSelector(final String groupId, final String artifactId) {
    this(groupId, artifactId, null);
  }

  /**
   * Default constructor.
   *
   * @param groupId the group identifier of the plugin to match.
   * @param artifactId the artifact identifier of the plugin to match.
   * @param keyPrefix the prefix to prepend to the fetched config properties.
   */
  public PluginSelector(final String groupId, final String artifactId,
      final String keyPrefix) {
    this.groupId = groupId;
    this.artifactId = artifactId;
    this.keyPrefix = keyPrefix;
  }

  // ****************************** Inner Classes *****************************

  // ********************************* Methods ********************************

  // --- init -----------------------------------------------------------------

  // --- get&set --------------------------------------------------------------

  public List<String> getPropertyNames() {
    return properties;
  }

  // --- business -------------------------------------------------------------

  /**
   * Checks if the plugin matches the descriptor.
   *
   * @param plugin the plugin to match.
   * @return <code>true</code> if the plugin is selected, <code>false</code>
   *         otherwise.
   * @throws NullPointerException if {@code plugin} is <code>null</code>.
   */
  public boolean matches(final Plugin plugin) {
    return (artifactId.equals(plugin.getArtifactId())
        && groupId.equals(plugin.getGroupId()));
  }

  /**
   * Prepends the property name prefix to the given property name.
   *
   * @param propertyName the original property name.
   * @return the prefixed name, if prefix is not <code>null</code>. Otherwise
   *         the property name is returned unaltered.
   */
  public String createPropertyName(final String propertyName) {
    if (keyPrefix != null) {
      return keyPrefix + propertyName;
    }

    return propertyName;
  }

  // --- object basics --------------------------------------------------------

}
