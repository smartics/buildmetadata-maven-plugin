/*
 * Copyright 2006-2009 smartics, Kronseder & Reiner GmbH
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
package de.smartics.maven.plugin.buildmetadata.updater;

import java.io.File;
import java.io.IOException;

import org.apache.commons.io.FileUtils;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugin.logging.Log;
import org.apache.maven.project.MavenProject;

/**
 * Base implementation of the updater interface.
 * 
 * @author <a href="mailto:robert.reiner@smartics.de">Robert Reiner</a>
 * @version $Revision$
 */
public abstract class AbstractPomUpdater implements PomUpdater
{
  // ********************************* Fields *********************************

  // --- constants ------------------------------------------------------------

  // --- members --------------------------------------------------------------

  /**
   * The logger to use.
   */
  protected Log log;

  // ****************************** Initializer *******************************

  // ****************************** Constructors ******************************

  /**
   * Default constructor.
   */
  protected AbstractPomUpdater()
  {
  }

  // ****************************** Inner Classes *****************************

  // ********************************* Methods ********************************

  // --- init -----------------------------------------------------------------

  // --- get&set --------------------------------------------------------------

  /**
   * {@inheritDoc}
   * 
   * @see de.smartics.maven.plugin.buildmetadata.updater.PomUpdater#setLog(org.apache.maven.plugin.logging.Log)
   */
  public void setLog(final Log log)
  {
    this.log = log;
  }

  // --- business -------------------------------------------------------------

  /**
   * Creates a backup of the POM file.
   * 
   * @param project the reference to project information.
   * @param pomFile the POM file to backup.
   * @throws MojoExecutionException on any problem encountered while copying the
   *         file.
   * @todo do not hardcode the traget dir!
   */
  protected void backupPomFile(final MavenProject project, final File pomFile)
      throws MojoExecutionException
  {
    final File backupPomFile = new File(project.getBasedir(),
        "target/pom-backup.xml");

    try
    {
      FileUtils.copyFile(pomFile, backupPomFile);
    }
    catch (final IOException e)
    {
      final String message = "Cannot copy POM file '"
                             + pomFile
                             + "' to '"
                             + backupPomFile
                             + "'.";
      throw createException(e, message);
    }
  }

  /**
   * Logs and creates the given exception.
   * 
   * @param e the original exception to throw.
   * @param message the message to log and add to the mojo exception.
   * @return the exception that wraps the given exception.
   */
  protected MojoExecutionException createException(final Throwable e,
      final String message)
  {
    if (log.isWarnEnabled())
    {
      log.warn(message, e);
    }

    return new MojoExecutionException(message, e);
  }

  // --- object basics --------------------------------------------------------

}
