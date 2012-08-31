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
package de.smartics.maven.plugin.buildmetadata.common;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

/**
 * Constants used in this package.
 *
 * @author <a href="mailto:robert.reiner@smartics.de">Robert Reiner</a>
 * @version $Revision: 8936 $
 */
public final class Constant
{
  // ********************************* Fields *********************************

  // --- constants ------------------------------------------------------------

  /**
   * The name of the project property that stores the URL to the SCM server.
   * <p>
   * The value of this constant is {@value}.
   * </p>
   */
  public static final String PROP_NAME_SCM_URL = "build.scmRevision.url";

  /**
   * The name of the project property that stores the revision number provided
   * by the SCM.
   * <p>
   * The value of this constant is {@value}.
   * </p>
   */
  public static final String PROP_NAME_SCM_REVISION_ID = "build.scmRevision.id";

  /**
   * The name of the project property that stores the date the revision number
   * was set.
   * <p>
   * The value of this constant is {@value}.
   * </p>
   */
  public static final String PROP_NAME_SCM_REVISION_DATE =
      "build.scmRevision.date";

  /**
   * The name of the project property that stores the information if the local
   * sources are modified.
   * <p>
   * The value of this constant is {@value}.
   * </p>
   */
  public static final String PROP_NAME_SCM_LOCALLY_MODIFIED =
      "build.scmLocallyModified";

  /**
   * The name of the project property that stores the files that are locally
   * modified.
   * <p>
   * The value of this constant is {@value}.
   * </p>
   */
  public static final String PROP_NAME_SCM_LOCALLY_MODIFIED_FILES =
      "build.scmLocallyModified.files";

  /**
   * The name of the project property that stores the build date.
   * <p>
   * The value of this constant is {@value}.
   * </p>
   */
  public static final String PROP_NAME_BUILD_DATE = "build.date";

  /**
   * The name of the project property that stores the build timestamp.
   * <p>
   * The value of this constant is {@value}.
   * </p>
   */
  public static final String PROP_NAME_BUILD_TIMESTAMP =
      "build.timestamp.millis";

  /**
   * The name of the project property that stores the pattern of the build date.
   * This way it is easy for the reading client to parse the build date.
   * <p>
   * The value of this constant is {@value}.
   * </p>
   */
  public static final String PROP_NAME_BUILD_DATE_PATTERN =
      "build.date.pattern";

  /**
   * The name of the project property that stores the version as read from the
   * POM.
   * <p>
   * The value of this constant is {@value}.
   * </p>
   */
  public static final String PROP_NAME_VERSION = "build.version";

  /**
   * The name of the project property that stores the full version that may
   * include the version, the build date, the build number and the revision
   * number.
   * <p>
   * The value of this constant is {@value}.
   * </p>
   */
  public static final String PROP_NAME_FULL_VERSION = "build.version.full";

  /**
   * The name of the project property that stores the build year.
   * <p>
   * The value of this constant is {@value}.
   * </p>
   */
  public static final String PROP_NAME_BUILD_YEAR = "build.year";

  /**
   * The name of the project property that stores the copyright year. The
   * copyright year is either the inception year (if inception and build year
   * are the same) or the period starting with the inception year and ending
   * with the build year.
   * <p>
   * The value of this constant is {@value}.
   * </p>
   */
  public static final String PROP_NAME_COPYRIGHT_YEAR = "build.copyright.year";

  /**
   * The default pattern for the (locale independent) build date.
   * <p>
   * The value of this constant is {@value}.
   * </p>
   */
  public static final String DEFAULT_DATE_PATTERN = "dd.MM.yyyy";

  /**
   * The name of the project property that stores the build user. This is the
   * person or system that run the build. It is either a configured value or the
   * value of the system property <code>user.name</code>.
   * <p>
   * The value of this constant is {@value}.
   * </p>
   */
  public static final String PROP_NAME_BUILD_USER = "build.user";

  /**
   * The name of the property that stores the name of the host the build has
   * been run on.
   * <p>
   * The value of this constant is {@value}.
   * </p>
   */
  public static final String PROP_NAME_HOSTNAME = "build.host.name";

  /**
   * The name of the property that stores the version of Maven being executed
   * for the build.
   * <p>
   * The value of this constant is {@value}.
   * </p>
   */
  public static final String PROP_NAME_MAVEN_VERSION = "build.maven.version";

  /**
   * The name of the property that stores the goals given on the command line
   * for the build.
   * <p>
   * The value of this constant is {@value}.
   * </p>
   */
  public static final String PROP_NAME_MAVEN_GOALS =
      "build.maven.execution.goals";

  /**
   * The name of the property that stores the command line to start the build.
   * <p>
   * The value of this constant is {@value}.
   * </p>
   */
  public static final String PROP_NAME_MAVEN_CMDLINE =
      "build.maven.execution.cmdline";

  /**
   * The name of the property that stores the Maven opts set in the environment.
   * <p>
   * The value of this constant is {@value}.
   * </p>
   */
  public static final String PROP_NAME_MAVEN_OPTS =
      "build.maven.execution.opts";

  /**
   * The name of the property that stores the Java opts set in the environment.
   * <p>
   * The value of this constant is {@value}.
   * </p>
   */
  public static final String PROP_NAME_JAVA_OPTS =
      "build.maven.execution.java.opts";

  /**
   * The name of the property that flags if the artifact is build within the
   * project that is the execution root.
   * <p>
   * The value of this constant is {@value}.
   * </p>
   */
  public static final String PROP_NAME_MAVEN_IS_EXECUTION_ROOT =
      "build.maven.execution.isRoot";

  /**
   * The name of the property that contains the name of the execution project.
   * <p>
   * The value of this constant is {@value}.
   * </p>
   */
  public static final String PROP_NAME_MAVEN_EXECUTION_PROJECT =
      "build.maven.execution.project";

  /**
   * The name of the property that contains the name of the filters being
   * registered for the build.
   * <p>
   * The value of this constant is {@value}.
   * </p>
   */
  public static final String PROP_NAME_MAVEN_FILTERS =
      "build.maven.execution.filters";

  /**
   * The prefix used to provide execution properties to the build properties
   * file.
   * <p>
   * The value of this constant is {@value}.
   * </p>
   */
  public static final String MAVEN_EXECUTION_PROPERTIES_PREFIX =
      "execution.property";

  /**
   * The name of the property that contains names of active profiles during the
   * build.
   * <p>
   * The value of this constant is {@value}.
   * </p>
   */
  public static final String PROP_NAME_MAVEN_ACTIVE_PROFILES =
      "build.maven.execution.profiles.active";

  /**
   * The prefix used to provide active profile information of the build.
   * <p>
   * The value of this constant is {@value}.
   * </p>
   */
  public static final String MAVEN_ACTIVE_PROFILE_PREFIX =
      "build.maven.execution.profile.active";

  /**
   * List of properties, organized in sections, relevant to the build report.
   * The order of properties in this list determines the order of the properties
   * listed in the report.
   */
  public static final List<Section> REPORT_PROPERTIES;

  // --- members --------------------------------------------------------------

  // ****************************** Initializer *******************************

  static
  {
    final List<Section> sections = new ArrayList<Section>();
    final Section scm =
        new Section("build.scm", PROP_NAME_SCM_REVISION_ID,
            PROP_NAME_SCM_REVISION_DATE, PROP_NAME_SCM_URL,
            PROP_NAME_SCM_LOCALLY_MODIFIED_FILES);
    sections.add(scm);

    final Section dateAndVersion =
        new Section("build.dateAndVersion", PROP_NAME_BUILD_DATE,
            PROP_NAME_BUILD_TIMESTAMP, PROP_NAME_FULL_VERSION);
    sections.add(dateAndVersion);

    final Section buildRuntime =
        new Section("build.runtime", PROP_NAME_HOSTNAME, PROP_NAME_BUILD_USER,
            PROP_NAME_JAVA_OPTS);
    sections.add(buildRuntime);

    final Section buildMaven =
        new Section("build.maven", PROP_NAME_MAVEN_VERSION,
            PROP_NAME_MAVEN_CMDLINE, PROP_NAME_MAVEN_GOALS,
            PROP_NAME_MAVEN_OPTS, PROP_NAME_MAVEN_EXECUTION_PROJECT,
            PROP_NAME_MAVEN_ACTIVE_PROFILES);
    sections.add(buildMaven);

    REPORT_PROPERTIES = Collections.unmodifiableList(sections);
  }

  // ****************************** Constructors ******************************

  /**
   * Constant pattern.
   */
  private Constant()
  {
  }

  // ****************************** Inner Classes *****************************

  /**
   * The section allows to group build meta data properties.
   */
  public static final class Section
  {
    /**
     * The resource key to access the title of the section.
     */
    private final String titleKey;

    /**
     * The build meta data properties to be displayed in this section.
     */
    private final List<String> properties;

    /**
     * Default constructor.
     *
     * @param titleKey the resource key to access the title of the section.
     * @param properties the build meta data properties to be displayed in this
     *          section.
     */
    private Section(final String titleKey, final String... properties)
    {
      this.titleKey = titleKey;
      this.properties = Arrays.asList(properties);
    }

    /**
     * Returns the resource key to access the title of the section.
     *
     * @return the resource key to access the title of the section.
     */
    public String getTitleKey()
    {
      return titleKey;
    }

    /**
     * Returns the build meta data properties to be displayed in this section.
     *
     * @return the build meta data properties to be displayed in this section.
     */
    public List<String> getProperties()
    {
      return properties;
    }
  }

  // ********************************* Methods ********************************

  // --- init -----------------------------------------------------------------

  // --- get&set --------------------------------------------------------------

  // --- business -------------------------------------------------------------

  // --- object basics --------------------------------------------------------
}
