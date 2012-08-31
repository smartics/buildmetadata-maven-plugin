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
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;
import java.util.ResourceBundle;

import org.apache.commons.io.IOUtils;
import org.apache.maven.doxia.sink.Sink;
import org.apache.maven.reporting.MavenReportException;
import org.codehaus.plexus.util.StringUtils;

/**
 * Renders the build report.
 *
 * @author <a href="mailto:robert.reiner@smartics.de">Robert Reiner</a>
 * @version $Revision:591 $
 */
public class BuildReportRenderer
{
  // ********************************* Fields *********************************

  // --- constants ------------------------------------------------------------

  /**
   * The class version identifier.
   * <p>
   * The value of this constant is {@value}.
   */
  private static final long serialVersionUID = 1L;

  // --- members --------------------------------------------------------------

  /**
   * The sink to write to.
   */
  private final Sink sink;

  /**
   * The resource bundle to access localized messages.
   */
  private final ResourceBundle messages;

  /**
   * The properties file to read the build information from.
   */
  private final File buildMetaDataPropertiesFile;

  // ****************************** Initializer *******************************

  // ****************************** Constructors ******************************

  /**
   * Default constructor.
   *
   * @param messages the resource bundle to access localized messages.
   * @param sink the sink to write to.
   * @param buildMetaDataPropertiesFile the properties file to read the build
   *          information from.
   */
  public BuildReportRenderer(final ResourceBundle messages, final Sink sink,
      final File buildMetaDataPropertiesFile)
  {
    this.sink = sink;
    this.messages = messages;
    this.buildMetaDataPropertiesFile = buildMetaDataPropertiesFile;
  }

  // ****************************** Inner Classes *****************************

  // ********************************* Methods ********************************

  // --- init -----------------------------------------------------------------

  // --- get&set --------------------------------------------------------------

  // --- business -------------------------------------------------------------

  /**
   * Renders the report to the instance's sink.
   *
   * @throws MavenReportException if the report cannot be rendered.
   */
  public void renderReport() throws MavenReportException
  {
    sink.head();
    sink.title();
    sink.text(messages.getString("report.name"));
    sink.title_();
    sink.head_();

    sink.body();
    renderBody();
    sink.body_();
    sink.flush();
    sink.close();
  }

  /**
   * Renders the body of the report.
   *
   * @throws MavenReportException if the report cannot be rendered.
   */
  private void renderBody() throws MavenReportException
  {
    sink.section1();

    sink.sectionTitle1();
    sink.text(messages.getString("report.name"));
    sink.sectionTitle1_();

    sink.paragraph();
    sink.text(messages.getString("report.description"));
    sink.paragraph_();

    final Properties buildMetaDataProperties = readBuildMetaDataProperties();

    sink.table();
    sink.tableRow();
    sink.tableHeaderCell("200");
    final String topicLabel = messages.getString("report.table.header.topic");
    sink.text(topicLabel);
    sink.tableHeaderCell_();
    sink.tableHeaderCell();
    final String valueLabel = messages.getString("report.table.header.value");
    sink.text(valueLabel);
    sink.tableHeaderCell_();
    sink.tableRow_();

    for (String key : Constant.REPORT_PROPERTIES)
    {
      renderCell(buildMetaDataProperties, key);
    }

    sink.table_();

    renderFooter();
    sink.section1_();
  }

  /**
   * Renders a single cell of the table.
   *
   * @param buildMetaDataProperties build meta data properties to access the
   *          data to be rendered.
   * @param key the key to the data to be rendered.
   */
  private void renderCell(
      final Properties buildMetaDataProperties,
      final String key)
  {
    final Object value = buildMetaDataProperties.get(key);
    if (value != null)
    {
      sink.tableRow();
      sink.tableCell();
      sink.text(messages.getString(key));
      sink.tableCell_();
      sink.tableCell();
      sink.text(String.valueOf(value));
      sink.tableCell_();
      sink.tableRow_();
    }
  }

  /**
   * Renders the footer text.
   */
  private void renderFooter()
  {
    final String footerText = messages.getString("report.footer");
    if (StringUtils.isNotBlank(footerText))
    {
      sink.rawText(footerText);
    }
  }

  /**
   * Reads the build meta data properties from the well known location.
   *
   * @return the read properties.
   * @throws MavenReportException if the properties cannot be read.
   */
  private Properties readBuildMetaDataProperties() throws MavenReportException
  {
    final Properties buildMetaDataProperties = new Properties();
    InputStream inStream = null;
    try
    {
      inStream =
          new BufferedInputStream(new FileInputStream(
              this.buildMetaDataPropertiesFile));
      buildMetaDataProperties.load(inStream);
    }
    catch (final IOException e)
    {
      throw new MavenReportException("Cannot read build properties file '"
          + this.buildMetaDataPropertiesFile + "'.");
    }
    finally
    {
      IOUtils.closeQuietly(inStream);
    }
    return buildMetaDataProperties;
  }

  // --- object basics --------------------------------------------------------

}
