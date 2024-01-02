/*
 * Copyright 2006-2024 smartics, Kronseder & Reiner GmbH
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
package de.smartics.maven.plugin.buildmetadata.maven;

import de.smartics.maven.plugin.buildmetadata.PluginSelector;

import org.apache.commons.lang.StringUtils;
import org.apache.maven.model.Plugin;
import org.apache.maven.project.MavenProject;
import org.codehaus.plexus.util.xml.Xpp3Dom;

import java.util.Properties;

/**
 * Fetches selectively properties from Maven plugin configurations.
 */
public class MavenPluginProperties {
  // ********************************* Fields *********************************

  // --- constants ------------------------------------------------------------

  // --- members --------------------------------------------------------------

  private final MavenProject project;

  // ****************************** Initializer *******************************

  // ****************************** Constructors ******************************

  public MavenPluginProperties(final MavenProject project) {
    this.project = project;
  }

  // ****************************** Inner Classes *****************************

  // ********************************* Methods ********************************

  // --- init -----------------------------------------------------------------

  // --- get&set --------------------------------------------------------------

  // --- business -------------------------------------------------------------

  /**
   * Fetches the properties for the given names.
   *
   * @param selector the selector to match a plugin.
   * @return the properties. May be empty, but is never <code>null</code>.
   */
  public Properties fetchProperties(final PluginSelector selector) {
    final Properties properties = new Properties();
    for (final Plugin plugin : project.getBuild().getPlugins()) {
      if (selector.matches(plugin)) {
        final Object configuration = plugin.getConfiguration();
        if (configuration instanceof Xpp3Dom) {
          addProperties(properties, selector, (Xpp3Dom) configuration);
        }
      }
    }

    return properties;
  }

  private void addProperties(final Properties properties,
      final PluginSelector selector, final Xpp3Dom configuration) {
    final String giConf = configuration.getName();
    if ("configuration".equals(giConf)) {
      for (final String propertyName : selector.getPropertyNames()) {
        final Xpp3Dom child = configuration.getChild(propertyName);
        if (child != null) {
          final String value = child.getValue();
          if (StringUtils.isNotBlank(value)) {
            final String key = selector.createPropertyName(propertyName);
            properties.put(key, value);
          }
        }
      }
    }
  }

  // --- object basics --------------------------------------------------------

}
