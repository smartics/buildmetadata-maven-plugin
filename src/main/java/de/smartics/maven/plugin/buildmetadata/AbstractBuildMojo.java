/*
 * Copyright 2006-2010 smartics, Kronseder & Reiner GmbH
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
import java.util.Properties;

import org.apache.maven.execution.MavenSession;
import org.apache.maven.execution.RuntimeInformation;
import org.apache.maven.plugin.AbstractMojo;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.project.MavenProject;

import de.smartics.maven.plugin.buildmetadata.common.ScmInfo;
import de.smartics.maven.plugin.buildmetadata.data.MetaDataProvider;
import de.smartics.maven.plugin.buildmetadata.data.MetaDataProviderBuilder;
import de.smartics.maven.plugin.buildmetadata.io.BuildPropertiesFileHelper;

/**
 * Base implementatio for al build mojos.
 *
 * @author <a href="mailto:robert.reiner@smartics.de">Robert Reiner</a>
 * @version $Revision: 9143 $
 */
public abstract class AbstractBuildMojo extends AbstractMojo
{
  // ********************************* Fields *********************************

  // --- constants ------------------------------------------------------------

  // --- members --------------------------------------------------------------

  // ... Mojo infrastructure ..................................................

  /**
   * The Maven project.
   *
   * @parameter expression="${project}"
   * @required
   * @readonly
   * @since 1.0
   */
  protected MavenProject project;

  /**
   * The Maven session instance.
   *
   * @parameter expression="${session}"
   * @required
   * @readonly
   */
  protected MavenSession session;

  /**
   * The runtime information of the Maven instance being executed for the build.
   *
   * @component
   * @since 1.0
   */
  protected RuntimeInformation runtime;

  /**
   * The name of the properties file to write. If you do not want to include the
   * properties file in the artifact, use
   * <code>${project.build.directory}/buildmetadata.properties</code>.
   * If the location is within a WAR or EAR file you may prefer to use
   * this location:
   * <code>${project.build.directory}/${project.build.finalName}/META-INF/build.properties</code>.
   *
   * @parameter default-value=
   *            "${project.build.outputDirectory}/META-INF/buildmetadata.properties"
   * @since 1.0
   */
  protected File propertiesOutputFile;

  /**
   * The name of the XML report file to write. If you want to include the XML
   * file in the artifact, use
   * <code>${project.build.outputDirectory}/META-INF/buildmetadata.xml</code>.
   *
   * @parameter default-value= "${project.build.directory}/buildmetadata.xml"
   * @since 1.0
   */
  protected File xmlOutputFile;

  /**
   * Flag to choose whether (<code>true</code>) or not (<code>false</code>) the
   * XML report should be created.
   *
   * @parameter default-value= "true"
   * @since 1.0
   */
  protected boolean createXmlReport;

  /**
   * The list of meta data providers to launch that contribute to the meta data.
   *
   * @parameter
   */
  protected List<Provider> providers;

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
   * <li><code>build.misc</code></li>
   * </ul>
   * <p>
   * If no valid section is given, the property is silently rendered in the
   * <code>build.misc</code> section.
   * </p>
   *
   * @parameter
   */
  protected List<Property> properties;

  /**
   * Flag to indicate whether or not the generated properties file should be
   * added to the projects filters.
   * <p>
   * Filters are only added temporarily (read in-memory during the build) and
   * are not written to the POM.
   * </p>
   *
   * @parameter expression="${buildMetaData.addToFilters}" default-value="true"
   * @since 1.0
   */
  protected boolean addToFilters;

  // ****************************** Initializer *******************************

  // ****************************** Constructors ******************************

  // ****************************** Inner Classes *****************************

  // ********************************* Methods ********************************

  // --- init -----------------------------------------------------------------

  // --- get&set --------------------------------------------------------------

  /**
   * Returns the Maven project.
   *
   * @return the Maven project.
   */
  public MavenProject getProject()
  {
    return project;
  }

  /**
   * Sets the Maven project.
   *
   * @param project the Maven project.
   */
  public void setProject(final MavenProject project)
  {
    this.project = project;
  }

  /**
   * Sets the Maven session.
   * <p>
   * Used for testing.
   * </p>
   *
   * @param session the Maven session.
   */
  public void setSession(final MavenSession session)
  {
    this.session = session;
  }

  /**
   * Sets the name of the properties file to write.
   * <p>
   * Used for testing.
   * </p>
   */
  public void setPropertiesOutputFile(final File propertiesOutputFile)
  {
    this.propertiesOutputFile = propertiesOutputFile;
  }

  // --- business -------------------------------------------------------------

  protected void provideBuildMetaData(final Properties buildMetaDataProperties,
      final ScmInfo scmInfo, final List<Provider> providers,
      final boolean runAtEndOfBuild) throws MojoExecutionException
  {
    if (providers != null && !providers.isEmpty())
    {
      final MetaDataProviderBuilder builder =
          new MetaDataProviderBuilder(project, session, runtime, scmInfo);
      for (final Provider providerConfig : providers)
      {
        if (providerConfig.isRunAtEndOfBuild() == runAtEndOfBuild)
        {
          final MetaDataProvider provider = builder.build(providerConfig);
          provider.provideBuildMetaData(buildMetaDataProperties);
        }
      }
    }
  }

  protected void updateMavenEnvironment(
      final Properties buildMetaDataProperties,
      final BuildPropertiesFileHelper helper)
  {
    final Properties projectProperties = helper.getProjectProperties(project);

    // Filters are only added temporarily and are not written to the POM...
    if (addToFilters)
    {
      project.getBuild().addFilter(propertiesOutputFile.getAbsolutePath());
    }
    projectProperties.putAll(buildMetaDataProperties);
  }

  // --- object basics --------------------------------------------------------

}
