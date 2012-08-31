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

import org.apache.maven.plugin.logging.Log;

import de.smartics.maven.plugin.buildmetadata.updater.stax.StaxPomUpdater;

/**
 * Generates POM updater.
 * 
 * @author <a href="mailto:robert.reiner@smartics.de">Robert Reiner</a>
 * @version $Revision$
 */
public class PomUpdaterFactory
{
  // ********************************* Fields *********************************

  // --- constants ------------------------------------------------------------

  // --- members --------------------------------------------------------------

  /**
   * The singleton.
   */
  private static final PomUpdaterFactory INSTANCE = new PomUpdaterFactory();

  // ****************************** Initializer *******************************

  // ****************************** Constructors ******************************

  // ****************************** Inner Classes *****************************

  // ********************************* Methods ********************************

  // --- init -----------------------------------------------------------------

  // --- get&set --------------------------------------------------------------

  /**
   * Returns the singleton factory.
   * 
   * @return the singleton.
   */
  public static PomUpdaterFactory getInstance()
  {
    return INSTANCE;
  }

  // --- business -------------------------------------------------------------

  /**
   * Creates the instance.
   * 
   * @param log the logger to use and to set to the created updater.
   * @return the created instance.
   */
  public PomUpdater createInstance(final Log log)
  {
    final String className = System.getProperty("buildmetadata.updater.class");
    PomUpdater updater = null;
    if (className != null)
    {
      try
      {
        final Class clazz = Class.forName(className);
        updater = (PomUpdater) clazz.newInstance();
      }
      catch (final Exception e)
      {
        if (log.isWarnEnabled())
        {
          log.warn("Cannot create POM updater '" + className + "'.", e);
        }
      }
    }

    // Set a default if none is set.
    if (updater == null)
    {
      // updater = new Xpp3DomPomUpdater();
      updater = new StaxPomUpdater();
    }

    updater.setLog(log);
    return updater;
  }

  // --- object basics --------------------------------------------------------
}
