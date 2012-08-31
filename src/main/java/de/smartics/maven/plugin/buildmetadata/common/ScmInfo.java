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

import java.io.File;

import org.apache.maven.scm.manager.ScmManager;
import org.apache.maven.settings.Server;
import org.apache.maven.settings.Settings;

import de.smartics.maven.plugin.buildmetadata.scm.maven.ScmAccessInfo;

/**
 * Bundles the SCM information to be passed to meta data providers.
 *
 * @author <a href="mailto:robert.reiner@smartics.de">Robert Reiner</a>
 * @version $Revision:591 $
 */
public class ScmInfo
{
  // ********************************* Fields *********************************

  // --- constants ------------------------------------------------------------

  // --- members --------------------------------------------------------------

  /**
   * The user's settings.
   */
  private final Settings settings;

  /**
   * The manager instance to access the SCM system. Provides access to the
   * repository and the provider information.
   */
  private final ScmManager scmManager;

  /**
   * Allows the user to choose which scm connection to use when connecting to
   * the scm. Can either be "connection" or "developerConnection".
   */
  private final String connectionType;

  /**
   * Used to specify the date format of the log entries that are retrieved from
   * your SCM system.
   */
  private final String scmDateFormat;

  /**
   * Input dir. Directory where the files under SCM control are located.
   */
  private final File basedir;

  /**
   * The user name (used by svn and starteam protocol).
   */
  private String userName;

  /**
   * The user password (used by svn and starteam protocol).
   */
  private String password;

  /**
   * The private key (used by java svn).
   */
  private String privateKey;

  /**
   * The passphrase (used by java svn).
   */
  private String passphrase;

  /**
   * The url of tags base directory (used by svn protocol).
   */
  private final String tagBase;

  /**
   * The range of the query in days to fetch change log entries from the SCM. If
   * no change logs have been found, the range is incremented up to
   * {@value ScmAccessInfo#DEFAULT_RETRY_COUNT} times. If no change log has been
   * found after these {@value ScmAccessInfo#DEFAULT_RETRY_COUNT} additional
   * queries, the revision number will not be set with a valid value.
   */
  private final int queryRangeInDays;

  /**
   * Flag to fail if local modifications have been found. The value is
   * <code>true</code> if the build should fail if there are modifications (any
   * files not in-sync with the remote repository), <code>false</code> if the
   * fact is only to be noted in the build properties.
   */
  private final boolean failOnLocalModifications;

  /**
   * The date pattern to use to format the build and revision dates. Please
   * refer to the <a href =
   * "http://java.sun.com/j2se/1.5.0/docs/api/java/text/SimpleDateFormat.html"
   * >SimpleDateFormat</a> class for valid patterns.
   */
  private final String buildDatePattern;

  /**
   * In offline mode the plugin will not generate revision information.
   */
  private final boolean offline;

  /**
   * Add SCM information if set to <code>true</code>, skip it, if set to
   * <code>false</code>. If you are not interested in SCM information, set this
   * to <code>false</code>.
   */
  private final boolean addScmInfo;

  /**
   * If it should be checked if the local files are up-to-date with the remote
   * files in the SCM repository. If the value is <code>true</code> the result
   * of the check, including the list of changed files, is added to the build
   * meta data.
   */
  private final boolean validateCheckout;

  // ****************************** Initializer *******************************

  // ****************************** Constructors ******************************

  /**
   * Default constructor.
   *
   * @param settings the user's settings.
   * @param scmManager the manager instance to access the SCM system.
   * @param connectionType the value for connectionType.
   * @param scmDateFormat the value for scmDateFormat.
   * @param basedir the value for basedir.
   * @param userName the user name (used by svn and starteam protocol).
   * @param password the user password (used by svn and starteam protocol).
   * @param privateKey the private key (used by java svn).
   * @param passphrase the passphrase (used by java svn).
   * @param tagBase the url of tags base directory (used by svn protocol).
   * @param queryRangeInDays the range of the query in days to fetch change log
   *          entries from the SCM.
   * @param failOnLocalModifications the value for failOnLocalModifications.
   * @param buildDatePattern the date pattern to use to format the build and
   *          revision dates.
   * @param offline the value for offline.
   * @param addScmInfo the value for addScmInfo.
   * @param validateCheckout the value for validateCheckout.
   * @note This argument list is quite long. The next time we touch this class,
   *       we should provide a builder.
   */
  public ScmInfo(final Settings settings, final ScmManager scmManager,
      final String connectionType, final String scmDateFormat,
      final File basedir, final String userName, final String password,
      final String privateKey, final String passphrase, final String tagBase,
      final int queryRangeInDays, final boolean failOnLocalModifications,
      final String buildDatePattern, final boolean offline,
      final boolean addScmInfo, final boolean validateCheckout)
  {
    this.settings = settings;
    this.scmManager = scmManager;
    this.connectionType = connectionType;
    this.scmDateFormat = scmDateFormat;
    this.basedir = basedir;
    this.userName = userName;
    this.password = password;
    this.privateKey = privateKey;
    this.passphrase = passphrase;
    this.tagBase = tagBase;
    this.queryRangeInDays = queryRangeInDays;
    this.failOnLocalModifications = failOnLocalModifications;
    this.buildDatePattern = buildDatePattern;
    this.offline = offline;
    this.addScmInfo = addScmInfo;
    this.validateCheckout = validateCheckout;
  }

  // ****************************** Inner Classes *****************************

  // ********************************* Methods ********************************

  // --- init -----------------------------------------------------------------

  // --- get&set --------------------------------------------------------------

  /**
   * Returns the manager instance to access the SCM system. Provides access to
   * the repository and the provider information.
   *
   * @return the manager instance to access the SCM system.
   */
  public ScmManager getScmManager()
  {
    return scmManager;
  }

  /**
   * Returns the value for connectionType.
   * <p>
   * Allows the user to choose which scm connection to use when connecting to
   * the scm. Can either be "connection" or "developerConnection".
   *
   * @return the value for connectionType.
   */
  public String getConnectionType()
  {
    return connectionType;
  }

  /**
   * Returns the value for scmDateFormat.
   * <p>
   * Used to specify the date format of the log entries that are retrieved from
   * your SCM system.
   *
   * @return the value for scmDateFormat.
   */
  public String getScmDateFormat()
  {
    return scmDateFormat;
  }

  /**
   * Returns the value for basedir.
   * <p>
   * Input dir. Directory where the files under SCM control are located.
   *
   * @return the value for basedir.
   */
  public File getBasedir()
  {
    return basedir;
  }

  /**
   * Returns the user name (used by svn and starteam protocol).
   *
   * @return the user name (used by svn and starteam protocol).
   */
  public String getUserName()
  {
    return userName;
  }

  /**
   * Returns the user password (used by svn and starteam protocol).
   *
   * @return the user password (used by svn and starteam protocol).
   */
  public String getPassword()
  {
    return password;
  }

  /**
   * Returns the private key (used by java svn).
   *
   * @return the private key (used by java svn).
   */
  public String getPrivateKey()
  {
    return privateKey;
  }

  /**
   * Returns the passphrase (used by java svn).
   *
   * @return the passphrase (used by java svn).
   */
  public String getPassphrase()
  {
    return passphrase;
  }

  /**
   * Returns the url of tags base directory (used by svn protocol).
   *
   * @return the url of tags base directory (used by svn protocol).
   */
  public String getTagBase()
  {
    return tagBase;
  }

  /**
   * Returns the range of the query in days to fetch change log entries from the
   * SCM. If no change logs have been found, the range is incremented up to
   * {@value ScmAccessInfo#DEFAULT_RETRY_COUNT} times. If no change log has been
   * found after these {@value ScmAccessInfo#DEFAULT_RETRY_COUNT} additional
   * queries, the revision number will not be set with a valid value.
   *
   * @return the range of the query in days to fetch change log entries from the
   *         SCM.
   */
  public int getQueryRangeInDays()
  {
    return queryRangeInDays;
  }

  /**
   * Returns the value for failOnLocalModifications.
   * <p>
   * Flag to fail if local modifications have been found. The value is
   * <code>true</code> if the build should fail if there are modifications (any
   * files not in-sync with the remote repository), <code>false</code> if the
   * fact is only to be noted in the build properties.
   *
   * @return the value for failOnLocalModifications.
   */
  public boolean isFailOnLocalModifications()
  {
    return failOnLocalModifications;
  }

  /**
   * Returns the date pattern to use to format the build and revision dates.
   * Please refer to the <a href =
   * "http://java.sun.com/j2se/1.5.0/docs/api/java/text/SimpleDateFormat.html"
   * >SimpleDateFormat</a> class for valid patterns.
   *
   * @return the date pattern to use to format the build and revision dates.
   */
  public String getBuildDatePattern()
  {
    return buildDatePattern;
  }

  /**
   * Returns the value for offline.
   * <p>
   * In offline mode the plugin will not generate revision information.
   *
   * @return the value for offline.
   */
  public boolean isOffline()
  {
    return offline;
  }

  /**
   * Returns the value for addScmInfo.
   * <p>
   * Add SCM information if set to <code>true</code>, skip it, if set to
   * <code>false</code>. If you are not interested in SCM information, set this
   * to <code>false</code>.
   *
   * @return the value for addScmInfo.
   */
  public boolean isAddScmInfo()
  {
    return addScmInfo;
  }

  /**
   * Returns the value for validateCheckout.
   * <p>
   * If it should be checked if the local files are up-to-date with the remote
   * files in the SCM repository. If the value is <code>true</code> the result
   * of the check, including the list of changed files, is added to the build
   * meta data.
   *
   * @return the value for validateCheckout.
   */
  public boolean isValidateCheckout()
  {
    return validateCheckout;
  }

  // --- business -------------------------------------------------------------

  /**
   * Fetches the server information from the settings for the specified host.
   *
   * @param host the host whose access information is fetched from the settings
   *          file.
   */
  public void configureByServer(final String host)
  {
    final Server server = settings.getServer(host);
    if (server != null)
    {
      if (userName == null)
      {
        userName = this.settings.getServer(host).getUsername();
      }

      if (password == null)
      {
        password = this.settings.getServer(host).getPassword();
      }

      if (privateKey == null)
      {
        privateKey = this.settings.getServer(host).getPrivateKey();
      }

      if (passphrase == null)
      {
        passphrase = this.settings.getServer(host).getPassphrase();
      }
    }
  }

  // --- object basics --------------------------------------------------------

}
