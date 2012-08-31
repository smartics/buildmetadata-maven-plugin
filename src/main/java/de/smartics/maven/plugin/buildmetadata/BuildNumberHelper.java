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
package de.smartics.maven.plugin.buildmetadata;

import java.util.Properties;

import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugin.logging.Log;

/**
 * Helper to create the build number information.
 * 
 * @author <a href="mailto:robert.reiner@smartics.de">Robert Reiner</a>
 * @version $Revision:591 $
 */
public class BuildNumberHelper
{
  // ********************************* Fields *********************************

  // --- constants ------------------------------------------------------------

  // --- members --------------------------------------------------------------

  /**
   * The logger to use.
   */
  private final Log log;

  /**
   * The flag to indicate whether (<code>true</code>) or not (<code>false</code>
   * )the build number is to be created.
   */
  private final boolean createBuildNumber;

  /**
   * The flag to indicate whether (<code>true</code>) or not (<code>false</code>
   * )the build number is to be incremented.
   */
  private final boolean incrementBuildNumber;

  // ****************************** Initializer *******************************

  // ****************************** Constructors ******************************

  /**
   * Default constructor.
   * 
   * @param log the logger to use.
   * @param createBuildNumber the flag to indicate whether (<code>true</code>)
   *          or not (<code>false</code> )the build number is to be created.
   * @param incrementBuildNumber the flag to indicate whether (<code>true</code>
   *          ) or not (<code>false</code> )the build number is to be
   *          incremented.
   */
  public BuildNumberHelper(final Log log, final boolean createBuildNumber,
      final boolean incrementBuildNumber)
  {
    this.log = log;
    this.createBuildNumber = createBuildNumber;
    this.incrementBuildNumber = incrementBuildNumber;
  }

  // ****************************** Inner Classes *****************************

  // ********************************* Methods ********************************

  // --- init -----------------------------------------------------------------

  // --- get&set --------------------------------------------------------------

  // --- business -------------------------------------------------------------

  /**
   * Creates the build number and adds it to the property sets.
   * 
   * @param projectProperties the project properties.
   * @param buildMetaDataProperties the build meta data properties.
   * @throws MojoExecutionException if the build number creation failed.
   */
  public void createBuildNumber(
      final Properties projectProperties,
      final Properties buildMetaDataProperties) throws MojoExecutionException
  {
    if (createBuildNumber)
    {
      final String newBuildNumberString =
          createNewBuildNumber(projectProperties);
      buildMetaDataProperties.setProperty(Constant.PROP_NAME_BUILD_NUMBER,
          newBuildNumberString);
      projectProperties.setProperty(Constant.PROP_NAME_BUILD_NUMBER_CURRENT,
          newBuildNumberString);
      projectProperties.setProperty(Constant.PROP_NAME_BUILD_NUMBER,
          newBuildNumberString);
    }
  }

  /**
   * Creates the new build number. If the flag to increment the build number is
   * set to <code>false</code>, the number will be the old build number.
   * Otherwise it is incremented by <code>1</code>.
   * 
   * @param projectProperties the project properties to read the old build
   *          number from. If there is no build number, the new build number
   *          starts with <code>1</code>.
   * @return the build number to use for this build.
   * @throws MojoExecutionException on any problem creating the build number.
   */
  private String createNewBuildNumber(final Properties projectProperties)
      throws MojoExecutionException
  {
    final Long currentBuildNumber = fetchCurrentBuildNumber(projectProperties);
    final Long newBuildNumber =
        incrementBuildNumber ? new Long(currentBuildNumber.longValue() + 1L)
            : currentBuildNumber;
    final String newBuildNumberString = String.valueOf(newBuildNumber);
    return newBuildNumberString;
  }

  /**
   * Reads the build number from the POM properties. If there is no build number
   * stored, this method will generate one.
   * 
   * @param projectProperties the project properties to read the build number
   *          from.
   * @return the build number from the POM or <code>1</code> if there is no
   *         build number in the POM.
   * @throws MojoExecutionException if the build number cannot be parsed.
   */
  private Long fetchCurrentBuildNumber(final Properties projectProperties)
      throws MojoExecutionException
  {
    final String numberString =
        projectProperties.getProperty(Constant.PROP_NAME_BUILD_NUMBER_CURRENT);
    if (numberString != null)
    {
      try
      {
        final Long number = Long.decode(numberString);
        return number;
      }
      catch (final NumberFormatException e)
      {
        final String message =
            "Cannot parse build number '" + numberString
                + "'. This number is required to be a long.";
        throw MojoUtils.createException(log, e, message);
      }
    }
    return new Long(0L);
  }

  // --- object basics --------------------------------------------------------

}
