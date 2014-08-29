/*
 * Copyright 2006-2014 smartics, Kronseder & Reiner GmbH
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

import org.apache.maven.settings.Server;
import org.apache.maven.settings.Settings;
import org.sonatype.plexus.components.sec.dispatcher.SecDispatcherException;

import de.smartics.maven.plugin.buildmetadata.util.SettingsDecrypter;

/**
 * The SCM connection information for authentication.
 *
 * @author <a href="mailto:robert.reiner@smartics.de">Robert Reiner</a>
 * @version $Revision:591 $
 */
public final class ScmCredentials
{
  // ********************************* Fields *********************************

  // --- constants ------------------------------------------------------------

  // --- members --------------------------------------------------------------

  /**
   * Helper to decrypt encrypted passwords.
   */
  private final SettingsDecrypter settingsDecrypter;

  /**
   * The user's settings.
   */
  private final Settings settings;

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
   * The pass phrase (used by java svn).
   */
  private String passPhrase;

  // ****************************** Initializer *******************************

  // ****************************** Constructors ******************************

  /**
   * Default constructor.
   *
   * @param settingsDecrypter a Helper to decrypt encrypted passwords. May be
   *          <code>null</code> if no decryption is required.
   * @param settings the settings to fetch SCM information.
   * @param userName the user name (used by svn and starteam protocol).
   * @param password the user password (used by svn and starteam protocol).
   * @param privateKey the private key (used by java svn).
   * @param passphrase the passphrase (used by java svn).
   */
  public ScmCredentials(final SettingsDecrypter settingsDecrypter,
      final Settings settings, final String userName, // NOPMD
      final String password, final String privateKey, final String passphrase)
  {
    this.settingsDecrypter = settingsDecrypter;
    this.settings = settings;
    this.userName = userName;
    this.password = decrypt(settingsDecrypter, password);
    this.privateKey = privateKey;
    this.passPhrase = passphrase;
  }

  // ****************************** Inner Classes *****************************

  // ********************************* Methods ********************************

  // --- init -----------------------------------------------------------------

  private static String decrypt(final SettingsDecrypter settingsDecrypter,
      final String possiblyEncrypted)
  {
    if (settingsDecrypter != null)
    {
      try
      {
        final String decrypted = settingsDecrypter.decrypt(possiblyEncrypted);
        return decrypted;
      }
      catch (final SecDispatcherException e)
      {
        // ok, continue with the unencrypted...
      }
    }
    return possiblyEncrypted;
  }

  // --- get&set --------------------------------------------------------------

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
   * Returns the pass phrase (used by java svn).
   *
   * @return the pass phrase (used by java svn).
   */
  public String getPassPhrase()
  {
    return passPhrase;
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
        userName = settings.getServer(host).getUsername();
      }

      if (password == null)
      {
        password =
            decrypt(settingsDecrypter, settings.getServer(host).getPassword());
      }

      if (privateKey == null)
      {
        privateKey = settings.getServer(host).getPrivateKey();
      }

      if (passPhrase == null)
      {
        passPhrase = settings.getServer(host).getPassphrase();
      }
    }
  }

  // --- object basics --------------------------------------------------------

}
