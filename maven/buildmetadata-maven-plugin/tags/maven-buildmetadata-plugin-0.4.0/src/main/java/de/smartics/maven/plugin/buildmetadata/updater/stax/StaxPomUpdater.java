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
package de.smartics.maven.plugin.buildmetadata.updater.stax;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Properties;

import javax.xml.namespace.QName;
import javax.xml.stream.FactoryConfigurationError;
import javax.xml.stream.XMLEventFactory;
import javax.xml.stream.XMLEventReader;
import javax.xml.stream.XMLEventWriter;
import javax.xml.stream.XMLInputFactory;
import javax.xml.stream.XMLOutputFactory;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.events.EndElement;
import javax.xml.stream.events.StartElement;
import javax.xml.stream.events.XMLEvent;

import org.apache.commons.io.IOUtils;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.project.MavenProject;

import de.smartics.maven.plugin.buildmetadata.Constant;
import de.smartics.maven.plugin.buildmetadata.updater.AbstractPomUpdater;

/**
 * The StAX implementation of the POM updater.
 * 
 * @author <a href="mailto:robert.reiner@smartics.de">Robert Reiner</a>
 * @version $Revision$
 */
public class StaxPomUpdater extends AbstractPomUpdater
{
  // ********************************* Fields *********************************

  // --- constants ------------------------------------------------------------

  /**
   * The generic identifier of the properties element.
   * <p>
   * The value of the constant is {@value}.
   */
  private static final String GI_PROPERTIES = "properties";

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

    final File tmpFile = createUpdatedPom(project.getBasedir(), pomFile,
        buildMetaDataProperties);
    if (pomFile.delete())
    {
      if (!tmpFile.renameTo(pomFile))
      {
        final String message = "Cannot move new POM file '"
                               + tmpFile.getAbsolutePath()
                               + "' to original location '"
                               + pomFile.getAbsolutePath()
                               + "'.";
        if (log.isWarnEnabled())
        {
          log.warn(message);
        }
        throw new MojoExecutionException(message);
      }
    }
    else
    {
      final String message = "Cannot delete original POM file '"
                             + pomFile.getAbsolutePath()
                             + "'.";
      if (log.isWarnEnabled())
      {
        log.warn(message);
      }
      throw new MojoExecutionException(message);
    }
  }

  /**
   * Creates a separate file that contains the new content.
   * 
   * @param baseDir the base directory of the project.
   * @param pomFile the POM file with the project information to read.
   * @param buildMetaDataProperties the information with which the POM file is
   *        updated.
   * @return the created temporary file that will be moved the the original
   *         position by the caller.
   * @throws MojoExecutionException on any problem encountered.
   */
  private File createUpdatedPom(final File baseDir, final File pomFile,
      final Properties buildMetaDataProperties) throws MojoExecutionException
  {
    InputStream in = null;
    OutputStream out = null;
    XMLEventReader parser = null;
    XMLEventWriter writer = null;
    try
    {
      final File tmpPomFile = new File(baseDir, "target/pom-tmp.xml");
      in = new BufferedInputStream(new FileInputStream(pomFile));
      out = new BufferedOutputStream(new FileOutputStream(tmpPomFile));

      final XMLInputFactory inFactory = XMLInputFactory.newInstance();
      parser = inFactory.createXMLEventReader(in);
      final XMLOutputFactory outFactory = XMLOutputFactory.newInstance();
      // writer = new
      // IndentingXMLEventWriter(outFactory.createXMLEventWriter(out));
      writer = outFactory.createXMLEventWriter(out);

      final StaxPath path = new StaxPath("/project/properties");
      boolean propertiesWritten = false;
      while (parser.hasNext())
      {
        final XMLEvent event = parser.nextEvent();
        path.handleEvent(event);

        if (path.isMatch())
        {
          if (event.isStartElement())
          {
            final StartElement element = event.asStartElement();
            final QName qname = element.getName();
            if (GI_PROPERTIES.equals(qname.getLocalPart()))
            {
              writer.add(event);
              handleProperties(parser, writer, buildMetaDataProperties);
              propertiesWritten = true;
              continue;
            }
          }
        }

        if (event.isEndElement())
        {
          final EndElement element = event.asEndElement();
          final QName qname = element.getName();
          if ("project".equals(qname.getLocalPart()) && !propertiesWritten)
          {
            writePropertiesComplete(writer, buildMetaDataProperties);
          }
        }

        writer.add(event);
        // The following is only valid if the XML declaration is not already
        // set.
        // if (event.isStartDocument())
        // {
        // // We want to add a newline after the XML declaration, so the root
        // // element is on a new line...
        // final XMLEventFactory eventFactory = XMLEventFactory.newInstance();
        // writer.add(eventFactory.createCharacters(System
        // .getProperty("line.separator")));
        // }
      }

      return tmpPomFile;
    }
    catch (final FileNotFoundException e)
    {
      final String message = "Cannot find POM file '"
                             + pomFile.getAbsolutePath()
                             + "'.";
      if (log != null && log.isWarnEnabled())
      {
        log.warn(message, e);
      }
      throw new MojoExecutionException(message, e);
    }
    catch (final XMLStreamException e)
    {
      final String message = "Problems parsing POM file '"
                             + pomFile.getAbsolutePath()
                             + "'.";
      if (log.isWarnEnabled())
      {
        log.warn(message, e);
      }
      throw new MojoExecutionException(message, e);
    }
    finally
    {
      StaxUtils.closeQuietly(parser);
      StaxUtils.closeQuietly(writer);
      IOUtils.closeQuietly(in);
      IOUtils.closeQuietly(out);
    }
  }

  /**
   * Writes the complete properties element.
   * 
   * @param writer the writer to write on.
   * @param buildMetaDataProperties the properties to fetch the information.
   * @throws FactoryConfigurationError on any problem.
   * @throws XMLStreamException on any XML problem.
   */
  private void writePropertiesComplete(final XMLEventWriter writer,
      final Properties buildMetaDataProperties)
      throws FactoryConfigurationError, XMLStreamException
  {
    final XMLEventFactory eventFactory = XMLEventFactory.newInstance();
    writer.add(eventFactory.createCharacters("  "));
    StaxUtils.writeStartElement(eventFactory, writer, GI_PROPERTIES);
    writer.add(eventFactory.createCharacters(System
        .getProperty("line.separator")));
    writer.add(eventFactory.createCharacters("  "));
    writeProperty(eventFactory, writer, buildMetaDataProperties,
        Constant.PROP_NAME_BUILD_NUMBER_CURRENT);
    writer.add(eventFactory.createCharacters("  "));
    writeProperty(eventFactory, writer, buildMetaDataProperties,
        Constant.PROP_NAME_BUILD_DATE_CURRENT);
    writer.add(eventFactory.createCharacters("  "));
    StaxUtils.writeEndElement(eventFactory, writer, GI_PROPERTIES);
    writer.add(eventFactory.createCharacters(System
        .getProperty("line.separator")));
  }

  /**
   * Writes a single property.
   * 
   * @param eventFactory the Stax factory to create events.
   * @param writer the writer to write on.
   * @param buildMetaDataProperties the properties to fetch the information.
   * @param propertyName the name of the property to fetch from the given
   *    properties and write to the event writer.
   * @throws XMLStreamException on any problem encountered while writing the
   *    property.
   */
  private void writeProperty(final XMLEventFactory eventFactory,
      final XMLEventWriter writer, final Properties buildMetaDataProperties,
      final String propertyName) throws XMLStreamException
  {
    writer.add(eventFactory.createCharacters("  "));
    StaxUtils.writeStartElement(eventFactory, writer, propertyName);
    writer.add(eventFactory.createCharacters(buildMetaDataProperties
        .getProperty(propertyName, "1")));
    StaxUtils.writeEndElement(eventFactory, writer, propertyName);
    writer.add(eventFactory.createCharacters(System
        .getProperty("line.separator")));
  }

  /**
   * Reads the properties from the file.
   * 
   * @param reader the reader to read the events from.
   * @param writer the writer to write on.
   * @param buildMetaDataProperties the properties to fetch the information.
   * @throws XMLStreamException on any problem encountered while writing the
   *    property.
   */
  private void handleProperties(final XMLEventReader reader,
      final XMLEventWriter writer, final Properties buildMetaDataProperties)
      throws XMLStreamException
  {
    final XMLEventFactory eventFactory = XMLEventFactory.newInstance();
    boolean inBuildNumberElement = false;
    boolean inBuildDateElement = false;
    boolean buildNumberSet = false;
    boolean buildDateSet = false;

    while (reader.hasNext())
    {
      final XMLEvent event = reader.nextEvent();

      if (event.isStartElement())
      {
        final StartElement element = event.asStartElement();
        final QName qname = element.getName();
        if (Constant.PROP_NAME_BUILD_NUMBER_CURRENT.equals(qname.getLocalPart()))
        {
          inBuildNumberElement = true;
        }
        else if (Constant.PROP_NAME_BUILD_DATE_CURRENT.equals(qname.getLocalPart()))
        {
          inBuildDateElement = true;
        }
      }
      else if (event.isCharacters())
      {
        if (inBuildNumberElement)
        {
          final String buildNumberString = event.asCharacters().getData();
          final long buildNumber = Long.parseLong(buildNumberString);
          writer.add(eventFactory.createCharacters(String
              .valueOf(buildNumber + 1L)));
          buildNumberSet = true;
          continue;
        }
        else if (inBuildDateElement)
        {
          writer.add(eventFactory.createCharacters(buildMetaDataProperties
              .getProperty(Constant.PROP_NAME_BUILD_DATE_CURRENT)));
          buildDateSet = true;
          continue;
        }
      }
      else if (event.isEndElement())
      {
        final EndElement element = event.asEndElement();
        final QName qname = element.getName();
        if (Constant.PROP_NAME_BUILD_NUMBER_CURRENT.equals(qname.getLocalPart()))
        {
          inBuildNumberElement = false;
        }
        else if (Constant.PROP_NAME_BUILD_DATE_CURRENT.equals(qname.getLocalPart()))
        {
          inBuildDateElement = false;
        }
        else if (GI_PROPERTIES.equals(qname.getLocalPart()))
        {
          if (!buildNumberSet)
          {
            writeProperty(eventFactory, writer, buildMetaDataProperties,
                Constant.PROP_NAME_BUILD_NUMBER_CURRENT);
          }
          if (!buildDateSet)
          {
            writer.add(eventFactory.createCharacters("  "));
            writeProperty(eventFactory, writer, buildMetaDataProperties,
                Constant.PROP_NAME_BUILD_DATE_CURRENT);
            writer.add(eventFactory.createCharacters("  "));
          }

          writer.add(event);
          return;
        }
      }

      writer.add(event);
    }
  }

  // --- object basics --------------------------------------------------------

}
