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

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.Reader;
import java.io.Writer;
import java.util.Properties;

import org.apache.commons.io.IOUtils;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.project.MavenProject;
import org.codehaus.plexus.util.xml.Xpp3Dom;
import org.codehaus.plexus.util.xml.Xpp3DomBuilder;
import org.codehaus.plexus.util.xml.Xpp3DomWriter;

import de.smartics.maven.plugin.buildmetadata.Constant;

/**
 * The implementation using the Xpp3Dom implementation.
 * 
 * @author <a href="mailto:robert.reiner@smartics.de">Robert Reiner</a>
 * @version $Revision$
 */
public class Xpp3DomPomUpdater extends AbstractPomUpdater
{
  // ********************************* Fields *********************************

  // --- constants ------------------------------------------------------------

  // --- members --------------------------------------------------------------

  // ****************************** Initializer *******************************

  // ****************************** Constructors ******************************

  // ****************************** Inner Classes *****************************

  // ********************************* Methods ********************************

  // --- init -----------------------------------------------------------------

  // --- get&set --------------------------------------------------------------

  // --- business -------------------------------------------------------------

  /**
   * {@inheritDoc}
   * 
   * @see de.smartics.maven.plugin.buildmetadata.updater.PomUpdater#updatePom(org.apache.maven.project.MavenProject,
   *      java.util.Properties)
   */
  public void updatePom(final MavenProject project,
      final Properties buildMetaDataProperties) throws MojoExecutionException
  {
    final File pomFile = project.getFile();

    if (log.isInfoEnabled())
    {
      log.info("Updating '" + pomFile.getAbsolutePath() + "'...");
    }
    backupPomFile(project, pomFile);

    final Xpp3Dom pomDom = readDom(pomFile, buildMetaDataProperties);
    Writer writer = null;
    try
    {
      writer = new BufferedWriter(new FileWriter(pomFile));
      Xpp3DomWriter.write(writer, pomDom);
    }
    catch (final IOException e)
    {
      final String message = "Cannot write POM file '"
                             + pomFile
                             + "' to update build meta data properties.";
      throw createException(e, message);
    }
    finally
    {
      IOUtils.closeQuietly(writer);
    }
  }

  /**
   * Reads the POM without reading the parent POM or settings. The build date
   * and number is added to the properties of the POM.
   * 
   * @param pomFile the POM to read.
   * @param buildMetaDataProperties the properties to read from.
   * @return the DOM of the POM.
   * @throws MojoExecutionException on any problem reading the properties from
   *         the POM.
   */
  private Xpp3Dom readDom(final File pomFile,
      final Properties buildMetaDataProperties) throws MojoExecutionException
  {
    Reader reader = null;
    try
    {
      reader = new BufferedReader(new FileReader(pomFile));
      final Xpp3Dom dom = Xpp3DomBuilder.build(reader);
      Xpp3Dom node = dom.getChild("properties");
      boolean numberSet = false, dateSet = false, yearSet = false;
      if (node == null)
      {
        node = new Xpp3Dom("properties");
        dom.addChild(node);
      }
      else
      {
        final Xpp3Dom[] children = node.getChildren();

        for (int i = 0; i < children.length; i++)
        {
          final Xpp3Dom child = children[i];
          final String name = child.getName();
          if (Constant.PROP_NAME_BUILD_NUMBER_CURRENT.equals(name))
          {
            child.setValue(buildMetaDataProperties
                .getProperty(Constant.PROP_NAME_BUILD_NUMBER_CURRENT));
            numberSet = true;
          }
          else if (Constant.PROP_NAME_BUILD_DATE_CURRENT.equals(name))
          {
            child.setValue(buildMetaDataProperties
                .getProperty(Constant.PROP_NAME_BUILD_DATE_CURRENT));
            dateSet = true;
          }
          else if (Constant.PROP_NAME_BUILD_YEAR_CURRENT.equals(name))
          {
            child.setValue(buildMetaDataProperties
                .getProperty(Constant.PROP_NAME_BUILD_YEAR_CURRENT));
            yearSet = true;
          }
        }
      }

      if (!numberSet)
      {
        final Xpp3Dom prop = new Xpp3Dom(
            Constant.PROP_NAME_BUILD_NUMBER_CURRENT);
        prop.setValue(buildMetaDataProperties
            .getProperty(Constant.PROP_NAME_BUILD_NUMBER_CURRENT));
        node.addChild(prop);
      }
      if (!dateSet)
      {
        final Xpp3Dom prop = new Xpp3Dom(Constant.PROP_NAME_BUILD_DATE_CURRENT);
        prop.setValue(buildMetaDataProperties
            .getProperty(Constant.PROP_NAME_BUILD_DATE_CURRENT));
        node.addChild(prop);
      }
      if (!yearSet)
      {
        final Xpp3Dom prop = new Xpp3Dom(Constant.PROP_NAME_BUILD_YEAR_CURRENT);
        prop.setValue(buildMetaDataProperties
            .getProperty(Constant.PROP_NAME_BUILD_YEAR_CURRENT));
        node.addChild(prop);
      }

      return dom;
    }
    catch (final Exception e)
    {
      throw createException(e, "Cannot read POM.");
    }
    finally
    {
      IOUtils.closeQuietly(reader);
    }
  }

  // --- object basics --------------------------------------------------------

}
