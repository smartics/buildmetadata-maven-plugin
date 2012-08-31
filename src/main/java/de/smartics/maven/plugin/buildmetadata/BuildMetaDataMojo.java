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

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.Enumeration;
import java.util.List;
import java.util.Locale;
import java.util.Properties;

import org.apache.commons.io.IOUtils;
import org.apache.maven.execution.MavenSession;
import org.apache.maven.execution.RuntimeInformation;
import org.apache.maven.plugin.AbstractMojo;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugin.MojoFailureException;
import org.apache.maven.project.MavenProject;
import org.apache.maven.scm.manager.ScmManager;
import org.apache.maven.settings.Settings;
import org.codehaus.plexus.util.StringUtils;

import de.smartics.maven.plugin.buildmetadata.common.Constant;
import de.smartics.maven.plugin.buildmetadata.common.MojoUtils;
import de.smartics.maven.plugin.buildmetadata.common.ScmInfo;
import de.smartics.maven.plugin.buildmetadata.data.HostMetaData;
import de.smartics.maven.plugin.buildmetadata.data.MavenMetaData;
import de.smartics.maven.plugin.buildmetadata.data.ScmMetaData;
import de.smartics.maven.plugin.buildmetadata.scm.maven.ScmAccessInfo;
import de.smartics.maven.util.LoggingUtils;

/**
 * Provides the build information defined in {@link Constant}. This information
 * is also written to a properties file.
 *
 * @goal provide-buildmetadata
 * @phase initialize
 * @requiresProject
 * @description Provides a build meta data to the build process.
 * @author <a href="mailto:robert.reiner@smartics.de">Robert Reiner</a>
 * @version $Revision$
 */
public class BuildMetaDataMojo extends AbstractMojo
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
  private MavenProject project;

  /**
   * The Maven session instance.
   *
   * @parameter expression="${session}"
   * @required
   * @readonly
   */
  private MavenSession session;

  /**
   * The user's settings.
   *
   * @parameter expression="${settings}"
   * @required
   * @readonly
   * @since 1.0
   */
  private Settings settings;

  /**
   * The runtime information of the Maven instance being executed for the build.
   *
   * @component
   * @since 1.0
   */
  private RuntimeInformation runtime;

  /**
   * If set to <code>true</code>, build properties will be generate even if they
   * already exist in the target folder.
   *
   * @parameter default-value="false"
   * @since 1.0
   */
  private boolean forceNewProperties;

  /**
   * In offline mode the plugin will not generate revision information.
   *
   * @parameter default-value="${settings.offline}"
   * @required
   * @readonly
   * @since 1.0
   */
  private boolean offline;

  /**
   * Add SCM information if set to <code>true</code>, skip it, if set to
   * <code>false</code>. If you are not interested in SCM information, set this
   * to <code>false</code>.
   * <p>
   * For security reasons you may want to remove the properties file from the
   * META-INF folder. Please refer to <code>propertiesOutputFile</code> property.
   * </p>
   *
   * @parameter expression="${buildMetaData.addScmInfo}" default-value="true"
   * @since 1.0
   */
  private boolean addScmInfo;

  /**
   * Add host information if set to <code>true</code>, skip it, if set to
   * <code>false</code>. If you are not interested in host information (e.g. for
   * security reasons, set this to <code>false</code>.
   * <p>
   * For security reasons you may want to remove the properties file from the
   * META-INF folder. Please refer to <code>propertiesOutputFile</code> property.
   * </p>
   *
   * @parameter expression="${buildMetaData.addHostInfo}" default-value="true"
   * @since 1.0
   */
  private boolean addHostInfo;

  /**
   * Add environment variables if set to <code>true</code>, skip it, if set to
   * <code>false</code>. If you are not interested in the environment variables
   * of the host (e.g. for security reasons, set this to <code>false</code>.
   * <p>
   * For security reasons you may want to remove the properties file from the
   * META-INF folder. Please refer to <code>propertiesOutputFile</code> property.
   * </p>
   *
   * @parameter expression="${buildMetaData.addEnvInfo}" default-value="false"
   * @since 1.0
   */
  private boolean addEnvInfo;

  /**
   * Add Maven execution information (all properties starting with
   * <code>build.maven.execution</code>, like commandline, goals, profiles,
   * etc.) if set to <code>true</code>, skip it, if set to <code>false</code>.
   * If you are not interested in execution information, set this to
   * <code>false</code>.
   * <p>
   * For security reasons you may want to remove the properties file from the
   * META-INF folder. Please refer to <code>propertiesOutputFile</code> property.
   * </p>
   *
   * @parameter expression="${buildMetaData.addMavenExecutionInfo}"
   *            default-value="true"
   * @since 1.0
   */
  private boolean addMavenExecutionInfo;

  /**
   * A simple flag to skip the generation of the build information. If set on
   * the command line use <code>-DbuildMetaData.skip</code>.
   *
   * @parameter expression="${buildMetaData.skip}" default-value="false"
   * @since 1.0
   */
  private boolean skip;

  /**
   * Specifies the log level used for this plugin.
   * <p>
   * Allowed values are <code>SEVERE</code>, <code>WARNING</code>,
   * <code>INFO</code> and <code>FINEST</code>.
   * </p>
   *
   * @parameter expression="${buildMetaData.logLevel}"
   * @since 1.0
   */
  private String logLevel;

  /**
   * The manager instance to access the SCM system. Provides access to the
   * repository and the provider information.
   *
   * @component
   * @since 1.0
   */
  private ScmManager scmManager;

  /**
   * Allows the user to choose which scm connection to use when connecting to
   * the scm. Can either be "connection" or "developerConnection".
   *
   * @parameter default-value="connection"
   * @required
   * @since 1.0
   */
  private String connectionType;

  // ... core information .....................................................

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
  private boolean addToFilters;

  /**
   * The date pattern to use to format the build and revision dates. Please
   * refer to the <a href =
   * "http://java.sun.com/j2se/1.5.0/docs/api/java/text/SimpleDateFormat.html"
   * >SimpleDateFormat</a> class for valid patterns.
   *
   * @parameter expression="${buildMetaData.buildDate.pattern}"
   *            default-value="dd.MM.yyyy"
   * @since 1.0
   */
  protected String buildDatePattern = Constant.DEFAULT_DATE_PATTERN;

  /**
   * The property to query for the build user.
   *
   * @parameter default-value="username"
   * @since 1.0
   */
  private String buildUserPropertyName;

  /**
   * The name of the properties file to write. If you do not want to include the
   * properties file in the artifact, use
   * <code>${project.build.directory}/buildmetadata.properties</code>.
   *
   * @parameter default-value=
   *            "${project.build.outputDirectory}/META-INF/buildmetadata.properties"
   * @since 1.0
   */
  private File propertiesOutputFile;

  // ... build information related ............................................

  /**
   * Flag to add the build date to the full version separated by a '-'. If
   * <code>true</code> the build date is added, if <code>false</code> it is not.
   *
   * @parameter expression="${buildMetaData.addBuildDateToFullVersion}"
   *            default-value="true"
   * @since 1.0
   */
  private boolean addBuildDateToFullVersion;

  // ... svn related ..........................................................

  /**
   * Used to specify the date format of the log entries that are retrieved from
   * your SCM system.
   *
   * @parameter expression="${changelog.dateFormat}"
   *            default-value="yyyy-MM-dd HH:mm:ss"
   * @required
   * @since 1.0
   */
  private String scmDateFormat;

  /**
   * Input dir. Directory where the files under SCM control are located.
   *
   * @parameter expression="${basedir}"
   * @required
   * @since 1.0
   */
  private File basedir;

  /**
   * The user name (used by svn and starteam protocol).
   *
   * @parameter expression="${username}"
   * @since 1.0
   */
  private String userName;

  /**
   * The user password (used by svn and starteam protocol).
   *
   * @parameter expression="${password}"
   * @since 1.0
   */
  private String password;

  /**
   * The private key (used by java svn).
   *
   * @parameter expression="${privateKey}"
   * @since 1.0
   */
  private String privateKey;

  /**
   * The passphrase (used by java svn).
   *
   * @parameter expression="${passphrase}"
   * @since 1.0
   */
  private String passphrase;

  /**
   * The url of tags base directory (used by svn protocol).
   *
   * @parameter expression="${tagBase}"
   * @since 1.0
   */
  private String tagBase;

  /**
   * Flag to add the revision number to the full version separated by an 'r'. If
   * <code>true</code> the revision number is added, if <code>false</code> it is
   * not.
   *
   * @parameter expression="${buildMetaData.addReleaseNumberToFullVersion}"
   *            default-value="true"
   * @since 1.0
   */
  private boolean addReleaseNumberToFullVersion;

  /**
   * Flag to add the tag <code>-locally-modified</code> to the full version
   * string to make visible that this artifact has been created with locally
   * modified sources. This is often the case while the artifact is built while
   * still working on an issue before it is committed to the SCM repository.
   *
   * @parameter expression="${buildMetaData.addLocallyModifiedTagToFullVersion}"
   *            default-value="true"
   * @since 1.0
   */
  private boolean addLocallyModifiedTagToFullVersion;

  /**
   * The range of the query in days to fetch change log entries from the SCM. If
   * no change logs have been found, the range is incremented up to
   * {@value ScmAccessInfo#DEFAULT_RETRY_COUNT} times. If no change log has been
   * found after these {@value ScmAccessInfo#DEFAULT_RETRY_COUNT} additional
   * queries, the revision number will not be set with a valid value.
   *
   * @parameter expression="${buildMetaData.queryRangeInDays}"
   *            default-value="30"
   * @since 1.0
   */
  private int queryRangeInDays;

  /**
   * Flag to fail if local modifications have been found. The value is
   * <code>true</code> if the build should fail if there are modifications (any
   * files not in-sync with the remote repository), <code>false</code> if the
   * fact is only to be noted in the build properties.
   *
   * @parameter default-value="false"
   * @since 1.0
   */
  private boolean failOnLocalModifications;

  // ****************************** Initializer *******************************

  // ****************************** Constructors ******************************

  // ****************************** Inner Classes *****************************

  private static final class SortedProperties extends Properties
  {
    private static final long serialVersionUID = 1L;

    @SuppressWarnings("all")
    public synchronized Enumeration<Object> keys()
    {
      final Enumeration<Object> keysEnum = super.keys();
      final List keyList = new ArrayList<Object>();
      while (keysEnum.hasMoreElements())
      {
        keyList.add(keysEnum.nextElement());
      }
      Collections.sort(keyList);
      return Collections.enumeration(keyList);
    }
  }

  // ********************************* Methods ********************************

  // --- init -----------------------------------------------------------------

  // --- get&set --------------------------------------------------------------

  /**
   * Returns the maven project.
   *
   * @return the maven project.
   */
  public MavenProject getProject()
  {
    return project;
  }

  /**
   * Sets the maven project.
   *
   * @param project the maven project.
   */
  public void setProject(final MavenProject project)
  {
    this.project = project;
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

  /**
   * {@inheritDoc}
   */
  public void execute() throws MojoExecutionException, MojoFailureException
  {
    if (!skip)
    {
      LoggingUtils.configureLogger(getLog(), logLevel);

      final Properties projectProperties = getProjectProperties();
      if (!isBuildPropertiesAlreadySet(projectProperties))
      {
        final Properties buildMetaDataProperties = new Properties();
        if (isBuildPropertiesToBeRebuild())
        {
          final Date buildDate = new Date();

          provideBuildUser(projectProperties, buildMetaDataProperties);
          final MavenMetaData mavenMetaData =
              new MavenMetaData(project, session, runtime, addEnvInfo,
                  addMavenExecutionInfo);
          mavenMetaData.provideBuildMetaData(buildMetaDataProperties);
          final ScmInfo scmInfo =
              new ScmInfo(settings, scmManager, connectionType, scmDateFormat,
                  basedir, userName, password, privateKey, passphrase, tagBase,
                  queryRangeInDays, failOnLocalModifications, buildDatePattern);
          final ScmMetaData scmMetaData =
              new ScmMetaData(project, scmInfo, offline, addScmInfo);
          scmMetaData.provideBuildMetaData(buildMetaDataProperties);

          if (addHostInfo)
          {
            final HostMetaData hostMetaData = new HostMetaData();
            hostMetaData.provideBuildMetaData(buildMetaDataProperties);
          }

          final String buildDateString =
              createBuildDate(buildMetaDataProperties, buildDate);
          createBuildYear(buildMetaDataProperties, buildDate);
          createBuildVersion(buildMetaDataProperties, buildDate,
              buildDateString);

          writePropertiesFile(buildMetaDataProperties);
        }
        else
        {
          getLog().info("Reusing previously built metadata file.");
          readBuildPropertiesFile(buildMetaDataProperties);
        }

        updateMavenEnvironment(projectProperties, buildMetaDataProperties);
      }
    }
    else
    {
      getLog().info("Skipping buildmetadata collection since skip=true.");
    }
  }

  private void updateMavenEnvironment(final Properties projectProperties,
      final Properties buildMetaDataProperties)
  {
    // Filters are only added temporarily and are not written to the POM...
    if (addToFilters)
    {
      project.getBuild().addFilter(propertiesOutputFile.getAbsolutePath());
    }
    projectProperties.putAll(buildMetaDataProperties);
  }

  private void readBuildPropertiesFile(final Properties buildMetaDataProperties)
    throws MojoExecutionException
  {
    InputStream inStream = null;
    try
    {
      inStream =
          new BufferedInputStream(new FileInputStream(propertiesOutputFile));
      buildMetaDataProperties.load(inStream);
    }
    catch (final IOException e)
    {
      throw new MojoExecutionException("Cannot read provided properties file: "
                                       + propertiesOutputFile.getAbsolutePath());
    }
    finally
    {
      IOUtils.closeQuietly(inStream);
    }
  }

  private boolean isBuildPropertiesToBeRebuild()
  {
    return forceNewProperties || !propertiesOutputFile.exists();
  }

  private boolean isBuildPropertiesAlreadySet(final Properties projectProperties)
  {
    return projectProperties.getProperty(Constant.PROP_NAME_FULL_VERSION) != null;
  }

  /**
   * Provides the name of the user running the build. The value is either
   * specified in the project properties or is taken from the Java system
   * properties (<code>user.name</code>).
   *
   * @param projectProperties the project properties.
   * @param buildMetaDataProperties the build meta data properties.
   */
  private void provideBuildUser(final Properties projectProperties,
      final Properties buildMetaDataProperties)
  {
    String userNameValue = System.getProperty("user.name");
    if ((buildUserPropertyName != null))
    {
      final String value = projectProperties.getProperty(buildUserPropertyName);
      if (!StringUtils.isBlank(value))
      {
        userNameValue = value;
      }
    }

    if (userNameValue != null)
    {
      buildMetaDataProperties.setProperty(Constant.PROP_NAME_BUILD_USER,
          userNameValue);
    }
  }

  /**
   * Creates and adds the build date information.
   *
   * @param buildMetaDataProperties the build meta data properties.
   * @param buildDate the date of the build.
   * @return the formatted build date.
   */
  private String createBuildDate(final Properties buildMetaDataProperties,
      final Date buildDate)
  {
    final DateFormat format =
        new SimpleDateFormat(buildDatePattern, Locale.ENGLISH);
    final String buildDateString = format.format(buildDate);
    final String timestamp = String.valueOf(buildDate.getTime());

    buildMetaDataProperties.setProperty(Constant.PROP_NAME_BUILD_DATE,
        buildDateString);
    buildMetaDataProperties.setProperty(Constant.PROP_NAME_BUILD_DATE_PATTERN,
        this.buildDatePattern);
    buildMetaDataProperties.setProperty(Constant.PROP_NAME_BUILD_TIMESTAMP,
        timestamp);

    return buildDateString;
  }

  /**
   * Adds the build year information.
   *
   * @param buildMetaDataProperties the build meta data properties.
   * @param buildDate the build date to create the build year information.
   */
  private void createBuildYear(final Properties buildMetaDataProperties,
      final Date buildDate)
  {
    final DateFormat yearFormat = new SimpleDateFormat("yyyy", Locale.ENGLISH);
    final String buildYearString = yearFormat.format(buildDate);
    buildMetaDataProperties.setProperty(Constant.PROP_NAME_BUILD_YEAR,
        buildYearString);
  }

  /**
   * Adds the version information of the artifact.
   *
   * @param buildMetaDataProperties the build meta data properties.
   * @param buildDate the date of the build.
   * @param buildDateString the formatted date string.
   */
  private void createBuildVersion(final Properties buildMetaDataProperties,
      final Date buildDate, final String buildDateString)
  {
    final String version = project.getVersion();
    buildMetaDataProperties.setProperty(Constant.PROP_NAME_VERSION, version);
    buildMetaDataProperties.setProperty(Constant.PROP_NAME_BUILD_DATE,
        buildDateString);

    final String fullVersion =
        createFullVersion(buildMetaDataProperties, buildDate);
    buildMetaDataProperties.setProperty(Constant.PROP_NAME_FULL_VERSION,
        fullVersion);
  }

  /**
   * Fetches the project properties and if <code>null</code> returns a new empty
   * properties instance that is associated with the project.
   *
   * @return the properties of the project.
   */
  private Properties getProjectProperties()
  {
    Properties projectProperties = project.getProperties();
    if (projectProperties == null)
    {
      projectProperties = new Properties();
      project.getModel().setProperties(projectProperties);
    }

    return projectProperties;
  }

  /**
   * Writes the build meta data properties to the target file.
   *
   * @param buildMetaDataProperties the properties to write.
   * @return the reference to the written file.
   * @throws MojoExecutionException on any problem encountered while writing the
   *           properties.
   */
  private File writePropertiesFile(final Properties buildMetaDataProperties)
    throws MojoExecutionException
  {
    final File buildMetaDataFile = createBuildMetaDataFile();
    if (getLog().isInfoEnabled())
    {
      getLog()
          .info(
              "Writing properties '" + buildMetaDataFile.getAbsolutePath()
                  + "'...");
    }

    OutputStream out = null;
    try
    {
      out = new BufferedOutputStream(new FileOutputStream(buildMetaDataFile));
      final String comments = "Created by maven-buildmetadata-plugin.";
      final Properties sortedBuildMetaDataProperties =
          createSorted(buildMetaDataProperties);
      sortedBuildMetaDataProperties.store(out, comments);
    }
    catch (final FileNotFoundException e)
    {
      final String message =
          "Cannot find file '" + buildMetaDataFile
              + "' to write properties to.";
      throw MojoUtils.createException(getLog(), e, message);
    }
    catch (final IOException e)
    {
      final String message =
          "Cannot write properties to file '" + buildMetaDataFile + "'.";
      throw MojoUtils.createException(getLog(), e, message);
    }
    finally
    {
      IOUtils.closeQuietly(out);
    }

    return buildMetaDataFile;
  }

  private Properties createSorted(final Properties buildMetaDataProperties)
  {
    final SortedProperties sortedProperties = new SortedProperties();
    sortedProperties.putAll(buildMetaDataProperties);
    return sortedProperties;
  }

  /**
   * Creates the full version string which may include the date, the build, and
   * the revision.
   *
   * @param buildMetaDataProperties the generated build meta data properties.
   * @param buildDate the date of the current build.
   * @return the full version string.
   */
  private String createFullVersion(final Properties buildMetaDataProperties,
      final Date buildDate)
  {
    final String version = project.getVersion();

    final DateFormat format = new SimpleDateFormat("yyyyMMdd", Locale.ENGLISH);
    final String datePart = format.format(buildDate);
    final String revisionId =
        buildMetaDataProperties.getProperty(Constant.PROP_NAME_SCM_REVISION_ID);

    final String versionPrefix, versionSuffix;
    if (version.endsWith("-SNAPSHOT"))
    {
      versionPrefix = version.substring(0, version.lastIndexOf('-'));
      versionSuffix = "-SNAPSHOT";
    }
    else
    {
      versionPrefix = version;
      versionSuffix = "";
    }

    final String modified;
    if (addLocallyModifiedTagToFullVersion
        && "true".equals(buildMetaDataProperties
            .getProperty(Constant.PROP_NAME_SCM_LOCALLY_MODIFIED)))
    {
      modified = "-locally-modified";
    }
    else
    {
      modified = "";
    }

    final String fullVersion =
        versionPrefix
            + (addBuildDateToFullVersion ? '-' + datePart : "")
            + (addReleaseNumberToFullVersion
               && StringUtils.isNotBlank(revisionId) ? "r" + revisionId : "")
            + modified + versionSuffix;

    return fullVersion;
  }

  /**
   * Creates the properties file for the build meta data. If the directory to
   * place it in is not present, it will be created.
   *
   * @return the file to write the build properties to.
   * @throws MojoExecutionException if the output directory is not present and
   *           cannot be created.
   */
  private File createBuildMetaDataFile() throws MojoExecutionException
  {
    final File outputDirectory = propertiesOutputFile.getParentFile();
    if (!outputDirectory.exists())
    {
      final boolean created = outputDirectory.mkdirs();
      if (!created)
      {
        throw new MojoExecutionException("Cannot create output directory '"
                                         + outputDirectory + "'.");
      }
    }
    return propertiesOutputFile;
  }

  // --- object basics --------------------------------------------------------

}
