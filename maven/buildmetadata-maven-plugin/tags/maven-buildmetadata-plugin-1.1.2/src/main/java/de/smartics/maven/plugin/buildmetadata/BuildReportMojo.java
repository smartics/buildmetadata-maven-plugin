/*
 * Copyright 2006-2011 smartics, Kronseder & Reiner GmbH
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

package de.smartics.maven.plugin.buildmetadata;

import java.io.File;
import java.util.List;
import java.util.Locale;
import java.util.ResourceBundle;

import org.apache.maven.doxia.sink.Sink;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.reporting.MavenReportException;

import de.smartics.maven.plugin.buildmetadata.common.Property;

/**
 * Generates a report about the meta data provided to the build.
 *
 * @goal buildmetadata-report
 * @phase site
 * @description Generates a report on the build meta data.
 * @requiresProject
 * @author <a href="mailto:robert.reiner@smartics.de">Robert Reiner</a>
 * @version $Revision$
 */
public class BuildReportMojo extends AbstractReportMojo
{
  // ********************************* Fields *********************************

  // --- constants ------------------------------------------------------------

  // --- members --------------------------------------------------------------

  /**
   * The name of the properties file to write. Per default this value is
   * overridden by packaging dependent locations. Please refer to <a
   * href="#activatePropertyOutputFileMapping"
   * >activatePropertyOutputFileMapping</a> for details.
   *
   * @parameter default-value=
   *            "${project.build.outputDirectory}/META-INF/build.properties"
   * @since 1.0
   */
  private File propertiesOutputFile;

  /**
   * Used to activate the default mapping that writes the build properties of
   * deployable units to
   * <code>${project.build.directory}/${project.build.finalName}/META-INF/build.properties</code>
   * and for standard JAR files to
   * <code>${project.build.outputDirectory}/META-INF/build.properties</code>.
   *
   * @parameter default-value=true
   * @since 1.1
   */
  private boolean activatePropertyOutputFileMapping;

  /**
   * Maps a packaging to a location for the build meta data properties file.
   * <p>
   * This mapping is especially useful for multi projects.
   * </p>
   *
   * @parameter
   * @since 1.1
   */
  protected List<FileMapping> propertyOutputFileMapping;

  /**
   * The list of a system properties or environment variables to be selected by
   * the user to include into the build meta data properties.
   * <p>
   * The name is the name of the property, the section is relevant for placing
   * the property in one of the following sections:
   * </p>
   * <ul>
   * <li><code>build.scm</code></li>
   * <li><code>build.dateAndVersion</code></li>
   * <li><code>build.runtime</code></li>
   * <li><code>build.java</code></li>
   * <li><code>build.maven</code></li>
   * <li><code>project</code></li>
   * <li><code>build.misc</code></li>
   * </ul>
   * <p>
   * If no valid section is given, the property is silently rendered in the
   * <code>build.misc</code> section.
   * </p>
   *
   * @parameter
   * @since 1.0
   */
  protected List<Property> properties;

  private boolean initialized;

  // ****************************** Initializer *******************************

  // ****************************** Constructors ******************************

  // ****************************** Inner Classes *****************************

  // ********************************* Methods ********************************

  // --- init -----------------------------------------------------------------

  // --- get&set --------------------------------------------------------------

  /**
   * {@inheritDoc}
   *
   * @see org.apache.maven.reporting.MavenReport#getName(java.util.Locale)
   */
  public String getName(final Locale locale)
  {
    return getBundle(locale).getString("report.name");
  }

  /**
   * {@inheritDoc}
   *
   * @see org.apache.maven.reporting.MavenReport#getDescription(java.util.Locale)
   */
  public String getDescription(final Locale locale)
  {
    return getBundle(locale).getString("report.description");
  }

  /**
   * {@inheritDoc}
   *
   * @see org.apache.maven.reporting.MavenReport#getOutputName()
   */
  public String getOutputName()
  {
    return "build-report";
  }

  // --- business -------------------------------------------------------------

  /**
   * {@inheritDoc}
   */
  @Override
  public void execute() throws MojoExecutionException
  {
    init();
    super.execute();
  }

  protected void init()
  {
    if (!initialized)
    {
      final PropertyOutputFileMapper mapper =
          new PropertyOutputFileMapper(project, propertyOutputFileMapping);
      this.propertyOutputFileMapping = mapper.initPropertyOutputFileMapping();
      this.propertiesOutputFile =
          mapper.getPropertiesOutputFile(activatePropertyOutputFileMapping,
              propertiesOutputFile);
      this.initialized = true;
    }
  }

  /**
   * {@inheritDoc}
   *
   * @see org.apache.maven.reporting.AbstractMavenReport#executeReport(java.util.Locale)
   */
  @Override
  protected void executeReport(final Locale locale) throws MavenReportException
  {
    super.executeReport(locale);

    final Sink sink = getSink();
    final ResourceBundle messages = getBundle(locale);
    final BuildReportRenderer renderer =
        new BuildReportRenderer(messages, sink, propertiesOutputFile,
            properties);
    renderer.renderReport();
  }

  /**
   * {@inheritDoc}
   * <p>
   * Returns <code>false</code> if the properties file that contains the build
   * information cannot be read.
   * </p>
   *
   * @see org.apache.maven.reporting.AbstractMavenReport#canGenerateReport()
   */
  @Override
  public boolean canGenerateReport()
  {
    init();
    return super.canGenerateReport() && propertiesOutputFile.canRead();
  }

  // --- object basics --------------------------------------------------------

}