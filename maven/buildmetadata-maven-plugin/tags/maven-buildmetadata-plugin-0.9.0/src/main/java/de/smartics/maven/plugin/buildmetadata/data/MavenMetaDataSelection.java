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

package de.smartics.maven.plugin.buildmetadata.data;

/**
 * Defines the Maven information to be included in the build meta data.
 *
 * @author <a href="mailto:robert.reiner@smartics.de">Robert Reiner</a>
 * @version $Revision:591 $
 */
public class MavenMetaDataSelection
{
  // ********************************* Fields *********************************

  // --- constants ------------------------------------------------------------

  // --- members --------------------------------------------------------------

  /**
   * Add environment variables if set to <code>true</code>, skip it, if set to
   * <code>false</code>. If you are not interested in the environment variables
   * of the host (e.g. for security reasons, set this to <code>false</code>).
   */
  private boolean addEnvInfo;

  /**
   * Add Maven execution information (commandline, goals, profiles, etc.) if set
   * to <code>true</code>, skip it, if set to <code>false</code>. If you are not
   * interested in execution information, set this to <code>false</code>.
   */
  private boolean addMavenExecutionInfo;

  /**
   * While the command line may be useful to refer to for a couple of reasons,
   * displaying it with the build properties is a security issue. Some plugins
   * allow to read passwords as properties from the command line and this
   * sensible data will be shown.
   */
  private boolean hideCommandLineInfo;

  /**
   * While the MAVEN_OPTS may be useful to refer to for a couple of reasons,
   * displaying them with the build properties is a security issue. Some plugins
   * allow to read passwords as properties from the command line and this
   * sensible data will be shown.
   */
  private boolean hideMavenOptsInfo;

  /**
   * While the JAVA_OPTS may be useful to refer to for a couple of reasons,
   * displaying them with the build properties is a security issue. Some plugins
   * allow to read passwords as properties from the command line and this
   * sensible data will be shown.
   * <p>
   * Therefore the JAVA_OPTS are hidden by default (<code>true</code>). To
   * include this information, use a value of <code>false</code>.
   * </p>
   */
  private boolean hideJavaOptsInfo;

  // ****************************** Initializer *******************************

  // ****************************** Constructors ******************************

  // ****************************** Inner Classes *****************************

  // ********************************* Methods ********************************

  // --- init -----------------------------------------------------------------

  // --- get&set --------------------------------------------------------------

  /**
   * Returns the value for addMavenExecutionInfo.
   * <p>
   * Add Maven execution information (commandline, goals, profiles, etc. ) if
   * set to <code>true</code>, skip it, if set to <code>false</code>. If you are
   * not interested in execution information, set this to <code>false</code>.
   *
   * @return the value for addMavenExecutionInfo.
   */
  public boolean isAddMavenExecutionInfo()
  {
    return addMavenExecutionInfo;
  }

  /**
   * Sets the value for addMavenExecutionInfo.
   * <p>
   * Add Maven execution information (commandline, goals, profiles, etc. ) if
   * set to <code>true</code>, skip it, if set to <code>false</code>. If you are
   * not interested in execution information, set this to <code>false</code>.
   *
   * @param addMavenExecutionInfo the value for addMavenExecutionInfo.
   */
  public void setAddMavenExecutionInfo(final boolean addMavenExecutionInfo)
  {
    this.addMavenExecutionInfo = addMavenExecutionInfo;
  }

  /**
   * Returns the value for hideCommandLineInfo.
   * <p>
   * While the command line may be useful to refer to for a couple of reasons,
   * displaying it with the build properties is a security issue. Some plugins
   * allow to read passwords as properties from the command line and this
   * sensible data will be shown.
   *
   * @return the value for hideCommandLineInfo.
   */
  public boolean isHideCommandLineInfo()
  {
    return hideCommandLineInfo;
  }

  /**
   * Sets the value for hideCommandLineInfo.
   * <p>
   * While the command line may be useful to refer to for a couple of reasons,
   * displaying it with the build properties is a security issue. Some plugins
   * allow to read passwords as properties from the command line and this
   * sensible data will be shown.
   *
   * @param hideCommandLineInfo the value for hideCommandLineInfo.
   */
  public void setHideCommandLineInfo(final boolean hideCommandLineInfo)
  {
    this.hideCommandLineInfo = hideCommandLineInfo;
  }

  /**
   * Returns the value for hideMavenOptsInfo.
   * <p>
   * While the MAVEN_OPTS may be useful to refer to for a couple of reasons,
   * displaying them with the build properties is a security issue. Some plugins
   * allow to read passwords as properties from the command line and this
   * sensible data will be shown.
   *
   * @return the value for hideMavenOptsInfo.
   */
  public boolean isHideMavenOptsInfo()
  {
    return hideMavenOptsInfo;
  }

  /**
   * Sets the value for hideMavenOptsInfo.
   * <p>
   * While the MAVEN_OPTS may be useful to refer to for a couple of reasons,
   * displaying them with the build properties is a security issue. Some plugins
   * allow to read passwords as properties from the command line and this
   * sensible data will be shown.
   *
   * @param hideMavenOptsInfo the value for hideMavenOptsInfo.
   */
  public void setHideMavenOptsInfo(final boolean hideMavenOptsInfo)
  {
    this.hideMavenOptsInfo = hideMavenOptsInfo;
  }

  /**
   * Returns the value for hideJavaOptsInfo.
   * <p>
   * While the JAVA_OPTS may be useful to refer to for a couple of reasons,
   * displaying them with the build properties is a security issue. Some plugins
   * allow to read passwords as properties from the command line and this
   * sensible data will be shown.
   * <p>
   * Therefore the JAVA_OPTS are hidden by default (<code>true</code>). To
   * include this information, use a value of <code>false</code>.
   * </p>
   *
   * @return the value for hideJavaOptsInfo.
   */
  public boolean isHideJavaOptsInfo()
  {
    return hideJavaOptsInfo;
  }

  /**
   * Sets the value for hideJavaOptsInfo.
   * <p>
   * While the JAVA_OPTS may be useful to refer to for a couple of reasons,
   * displaying them with the build properties is a security issue. Some plugins
   * allow to read passwords as properties from the command line and this
   * sensible data will be shown.
   * <p>
   * Therefore the JAVA_OPTS are hidden by default (<code>true</code>). To
   * include this information, use a value of <code>false</code>.
   * </p>
   *
   * @param hideJavaOptsInfo the value for hideJavaOptsInfo.
   */
  public void setHideJavaOptsInfo(final boolean hideJavaOptsInfo)
  {
    this.hideJavaOptsInfo = hideJavaOptsInfo;
  }

  /**
   * Sets the value for addEnvInfo.
   * <p>
   * Add environment variables if set to <code>true</code>, skip it, if set to
   * <code>false</code>. If you are not interested in the environment variables
   * of the host (e.g. for security reasons, set this to <code>false</code>).
   *
   * @param addEnvInfo the value for addEnvInfo.
   */
  public void setAddEnvInfo(final boolean addEnvInfo)
  {
    this.addEnvInfo = addEnvInfo;
  }

  /**
   * Returns the value for addEnvInfo.
   * <p>
   * Add environment variables if set to <code>true</code>, skip it, if set to
   * <code>false</code>. If you are not interested in the environment variables
   * of the host (e.g. for security reasons, set this to <code>false</code>).
   *
   * @return the value for addEnvInfo.
   */
  public boolean isAddEnvInfo()
  {
    return addEnvInfo;
  }

  // --- business -------------------------------------------------------------

  // --- object basics --------------------------------------------------------

}