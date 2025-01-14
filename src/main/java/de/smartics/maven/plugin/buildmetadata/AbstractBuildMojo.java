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

import de.smartics.maven.plugin.buildmetadata.common.Property;
import de.smartics.maven.plugin.buildmetadata.common.ScmInfo;
import de.smartics.maven.plugin.buildmetadata.data.MetaDataProvider;
import de.smartics.maven.plugin.buildmetadata.data.MetaDataProviderBuilder;
import de.smartics.maven.plugin.buildmetadata.data.Provider;
import de.smartics.maven.plugin.buildmetadata.io.BuildPropertiesFileHelper;
import de.smartics.maven.plugin.buildmetadata.util.ManifestHelper;
import de.smartics.maven.plugin.buildmetadata.util.MojoFileUtils;
import de.smartics.maven.plugin.buildmetadata.util.SettingsDecrypter;

import org.apache.maven.execution.MavenSession;
import org.apache.maven.execution.RuntimeInformation;
import org.apache.maven.plugin.AbstractMojo;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugin.MojoFailureException;
import org.apache.maven.project.MavenProject;
import org.sonatype.plexus.components.sec.dispatcher.SecDispatcher;

import java.io.File;
import java.io.IOException;
import java.util.Collections;
import java.util.List;
import java.util.Properties;

/**
 * Base implementation for all build mojos.
 */
public abstract class AbstractBuildMojo extends AbstractMojo {
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
   * The name of the properties file to write. Per default this value is
   * overridden by packaging dependent locations. Please refer to
   * <a href="#activatePropertyOutputFileMapping" >
   * activatePropertyOutputFileMapping</a> for details.
   *
   * @parameter default-value=
   *            "${project.build.outputDirectory}/META-INF/build.properties"
   * @since 1.0
   */
  protected File propertiesOutputFile;

  /**
   * Used to activate the default mapping that writes the build properties of
   * deployable units to
   * <code>${project.build.directory}/${project.build.finalName}/META-INF/build.properties</code>
   * and for standard JAR files to
   * <code>${project.build.outputDirectory}/META-INF/build.properties</code>.
   * <p>
   * This property is used for the properties and XML build file.
   * </p>
   *
   * @parameter default-value=true
   * @since 1.1
   */
  private boolean activateOutputFileMapping;

  /**
   * Maps a packaging to a location for the build meta data properties file.
   * <p>
   * This mapping is especially useful for multi projects.
   * </p>
   *
   * @parameter
   * @since 1.1
   */
  private List<FileMapping> propertyOutputFileMapping;

  /**
   * Selects properties from plugin configurations.
   * <p>
   * Add a list of selectors like this:
   * </p>
   *
   * <pre>
      &lt;pluginSelectors&gt;
        &lt;selector&gt;
          &lt;groupId&gt;org.apache.maven.plugins&lt;/groupId&gt;
          &lt;artifactId&gt;maven-compiler-plugin&lt;/artifactId&gt;
          &lt;keyPrefix&gt;build.runtime.java.compiler.&lt;/keyPrefix&gt;
          &lt;properties&gt;
            &lt;property&gt;source&lt;/property&gt;
            &lt;property&gt;target&lt;/property&gt;
          &lt;/properties&gt;
        &lt;/selector&gt;
        &lt;selector&gt;
          ...
        &lt;/selector&gt;
      &lt;/pluginSelectors&gt;
   * </pre>
   * <ul>
   * <li><code>groupId</code> - the group identifier of the plugin to select
   * </li>
   * <li><code>artifactId</code> - the artifact identifier of the plugin to
   * select</li>
   * <li><code>keyPrefix</code> - the optional prefix to add to the config
   * property</li>
   * <li><code>properties</code> - the list of config properties to select</li>
   * </ul>
   * <p>
   * Currently only simple key value pairs are selectable.
   * </p>
   *
   * @parameter
   * @since 1.5
   */
  private List<PluginSelector> pluginSelectors;

  /**
   * Maps a packaging to a location for the build meta data XML file.
   * <p>
   * This mapping is especially useful for multi projects.
   * </p>
   *
   * @parameter
   * @since 1.3
   */
  private List<FileMapping> xmlOutputFileMapping;

  /**
   * The name of the XML report file to write. If you want to include the XML
   * file in the artifact, use
   * <code>${project.build.outputDirectory}/META-INF/buildmetadata.xml</code>.
   * <p>
   * The handling is not in an analogous manner as that of the properties file.
   * The reason is this: we want to keep the artifact as small as possible per
   * default. Therefore we include the <code>build.properties</code> and
   * generate the XML report (see property <code>createXmlReport</code> to the
   * target folder (and not inside <code>META-INF</code>). The XML file can be
   * stored to the artifact server (with a couple of other reports) by the use
   * of the <a href="http://www.smartics.eu/projectmetadata-maven-plugin">
   * projectmetadata-maven-plugin</a>.
   * </p>
   *
   * @parameter default-value= "${project.build.directory}/buildmetadata.xml"
   * @since 1.0
   */
  protected File xmlOutputFile;

  /**
   * The location of the Manifest file to add the buildmetadata properties to.
   *
   * @parameter default-value=
   *            "${project.build.outputDirectory}/META-INF/MANIFEST.MF"
   * @since 1.5
   */
  protected File manifestFile;

  /**
   * The name of the section within the Manifest file to write the buildmetadata
   * properties to. Use "<code>Main</code>" to write the the main section.
   *
   * @parameter default-value="BuildMetaData"
   * @since 1.5
   */
  protected String manifestSection;

  /**
   * Flag to choose whether (<code>true</code>) or not (<code>false</code>) the
   * Manifest file should be created.
   *
   * @parameter default-value= "false"
   * @since 1.5
   */
  protected boolean createManifestFile;

  /**
   * Flag to choose whether (<code>true</code>) or not (<code>false</code>) the
   * <code>build.properties</code> file should be created.
   * <p>
   * This will adjust the path of the <code>propertiesOutputFile</code> to
   * <code>${project.build.directory}/build.properties</code>.
   * </p>
   *
   * @parameter default-value= "true"
   * @since 1.2
   */
  protected boolean createPropertiesReport;

  /**
   * Flag to choose whether (<code>true</code>) or not (<code>false</code>) the
   * XML report should be created.
   *
   * @parameter default-value= "true"
   * @since 1.0
   */
  protected boolean createXmlReport;

  /**
   * Flag to choose whether (<code>true</code>) or not (<code>false</code>) to
   * write protect the generated buildmetadata files. Protecting them allows to
   * projects that copy files from different resources together to not override
   * them by other plugins.
   *
   * @parameter default-value= "false"
   * @since 1.5
   */
  protected boolean writeProtectFiles;

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

  /**
   * The branch or tag version on the remote server to compare against. If
   * unset, the SCM status will be used to determine the differences.
   * <p>
   * For SVN, leave it blank. For Git, set the version on the remote server (the
   * project's SCM URL points to).
   * </p>
   *
   * @parameter
   * @since 1.4.0
   */
  protected String remoteVersion;

  /**
   * Helper to decrypt passwords from the settings.
   *
   * @component role=
   *            "org.sonatype.plexus.components.sec.dispatcher.SecDispatcher"
   * @since 1.4.0
   */
  private SecDispatcher securityDispatcher;

  /**
   * The location of the <code>settings-security.xml</code>.
   *
   * @parameter expression="${user.home}/.m2/settings-security.xml"
   * @required
   * @since 1.4.0
   */
  private String settingsSecurityLocation;

  /**
   * Helper to decrypt passwords from the settings handling the location of the
   * <code>settings-security.xml</code> file.
   *
   * @since 1.4.0
   */
  protected SettingsDecrypter settingsDecrypter;

  /**
   * A simple flag to skip the generation of the build information. If set on
   * the command line use <code>-DbuildMetaData.skip</code>.
   * <p>
   * Supported by the build point mojo since version 1.5.
   * </p>
   *
   * @parameter expression="${buildMetaData.skip}" default-value="false"
   * @since 1.0
   */
  protected boolean skip;

  /**
   * Flag to indicate that the buildmetadata must only be added for the root
   * project. Modules will be skipped.
   *
   * @parameter default-value= "false"
   * @since 1.5.0
   */
  protected boolean skipModules;

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
  public final MavenProject getProject() {
    return project;
  }

  /**
   * Sets the Maven project.
   *
   * @param project the Maven project.
   */
  public final void setProject(final MavenProject project) {
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
  public final void setSession(final MavenSession session) {
    this.session = session;
  }

  /**
   * Sets the name of the properties file to write.
   * <p>
   * Used for testing.
   * </p>
   *
   * @param propertiesOutputFile the name of the properties file to write.
   */
  public final void setPropertiesOutputFile(final File propertiesOutputFile) {
    this.propertiesOutputFile = propertiesOutputFile;
  }

  /**
   * Returns the value for createPropertiesReport.
   * <p>
   * Flag to choose whether (<code>true</code>) or not (<code>false</code>) the
   * <code>build. properties</code> file should be created.
   * </p>
   * <p>
   * This will adjust the path of the <code>propertiesOutputFile</code> to
   * <code>${project.build.directory}/build.properties</code>.
   * </p>
   *
   * @return the value for createPropertiesReport.
   */
  public boolean isCreatePropertiesReport() {
    return createPropertiesReport;
  }

  /**
   * Sets the value for createPropertiesReport.
   * <p>
   * Flag to choose whether (<code>true</code>) or not (<code>false</code>) the
   * <code>build. properties</code> file should be created.
   * </p>
   * <p>
   * This will adjust the path of the <code>propertiesOutputFile</code> to
   * <code>${project.build.directory}/build.properties</code>.
   * </p>
   *
   * @param createPropertiesReport the value for createPropertiesReport.
   */
  public void setCreatePropertiesReport(final boolean createPropertiesReport) {
    this.createPropertiesReport = createPropertiesReport;
  }

  @SuppressWarnings("unchecked")
  protected List<PluginSelector> getPluginSelectors() {
    return pluginSelectors != null ? pluginSelectors : Collections.EMPTY_LIST;
  }

  // --- business -------------------------------------------------------------

  @Override
  public void execute() throws MojoExecutionException, MojoFailureException {
    final String propertiesFileName =
        calcFileName(propertiesOutputFile, "build.properties");
    if (createPropertiesReport) {
      settingsDecrypter = securityDispatcher != null
          ? new SettingsDecrypter(securityDispatcher, settingsSecurityLocation)
          : null;

      final PropertyOutputFileMapper mapperProperties =
          new PropertyOutputFileMapper(project, propertyOutputFileMapping,
              propertiesFileName);
      final File rootFolderJar = propertiesOutputFile.getParentFile();
      this.propertyOutputFileMapping =
          mapperProperties.initOutputFileMapping(rootFolderJar);
      this.propertiesOutputFile =
          mapperProperties.getPropertiesOutputFile(activateOutputFileMapping,
              propertiesOutputFile);
    } else {
      // The properties file is required for project filtering even if only
      // the XML file is requested by the user.
      propertiesOutputFile =
          new File(project.getBuild().getDirectory(), propertiesFileName);
    }
    MojoFileUtils.ensureExists(propertiesOutputFile.getParentFile());

    if (createXmlReport) {
      final String xmlFileName =
          calcFileName(xmlOutputFile, "buildmetadata.xml");
      final PropertyOutputFileMapper mapperXml =
          new PropertyOutputFileMapper(project, xmlOutputFileMapping,
              xmlFileName);
      final File rootFolderJar = xmlOutputFile.getParentFile();
      this.xmlOutputFileMapping =
          mapperXml.initOutputFileMapping(rootFolderJar);
      this.xmlOutputFile =
          mapperXml.getPropertiesOutputFile(activateOutputFileMapping,
              xmlOutputFile);
      MojoFileUtils.ensureExists(xmlOutputFile.getParentFile());
    }
  }

  static String calcFileName(final File file,
      final String defaultName) {
    final String fileName;
    if (file != null) {
      fileName = file.getName();
    } else {
      fileName = defaultName;
    }
    return fileName;
  }

  /**
   * Adds the information as build properties for each provider.
   *
   * @param buildMetaDataProperties the build meta data properties to add to.
   * @param scmInfo the information for the SCM provided to the build plugin.
   * @param providers the providers to iterate over.
   * @param runAtEndOfBuild checks if the provider is configured to be run at
   *        the end of the build. If a provider matches this value, it is run.
   * @throws MojoExecutionException on any problem running on the providers.
   */
  protected final void provideBuildMetaData(
      final Properties buildMetaDataProperties, final ScmInfo scmInfo,
      final List<Provider> providers, final boolean runAtEndOfBuild)
          throws MojoExecutionException {
    if (providers != null && !providers.isEmpty()) {
      final MetaDataProviderBuilder builder =
          new MetaDataProviderBuilder(project, session, runtime, scmInfo);
      for (final Provider providerConfig : providers) {
        if (providerConfig.isRunAtEndOfBuild() == runAtEndOfBuild) {
          final MetaDataProvider provider = builder.build(providerConfig);
          provider.provideBuildMetaData(buildMetaDataProperties);
        }
      }
    }
  }

  /**
   * Updates the Maven runtime with build properties.
   *
   * @param buildMetaDataProperties the properties to add to the Maven project
   *        properties.
   * @param helper the project helper to use.
   * @throws MojoExecutionException if a requested Manifest file cannot be
   *         created.
   */
  protected final void updateMavenEnvironment(
      final Properties buildMetaDataProperties,
      final BuildPropertiesFileHelper helper) throws MojoExecutionException {
    final Properties projectProperties = helper.getProjectProperties(project);

    // Filters are only added temporarily and are not written to the POM...
    if (addToFilters) {
      project.getBuild().addFilter(propertiesOutputFile.getAbsolutePath());
    }

    if (createManifestFile) {
      final ManifestHelper manifestHelper =
          new ManifestHelper(manifestFile, manifestSection);
      try {
        MojoFileUtils.ensureExists(manifestFile.getParentFile());
        manifestHelper.createManifest(buildMetaDataProperties);
      } catch (final IOException e) {
        throw new MojoExecutionException(
            "Cannot create Manifest file: " + manifestFile.getAbsolutePath(),
            e);
      }
    }

    projectProperties.putAll(buildMetaDataProperties);
  }

  /**
   * Checks if the execution of the mojo should be skipped.
   *
   * @return <code>true</code> if skipping is requested, <code>false</code>
   *         otherwise.
   */
  protected boolean doSkip() {
    return skip || (skipModules && !project.isExecutionRoot());
  }

  // --- object basics --------------------------------------------------------

}
