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
import java.util.MissingResourceException;
import java.util.Properties;
import java.util.ResourceBundle;
import java.util.StringTokenizer;

import org.apache.commons.io.IOUtils;
import org.apache.maven.doxia.sink.Sink;
import org.apache.maven.reporting.MavenReportException;
import org.codehaus.plexus.util.StringUtils;

import de.smartics.maven.plugin.buildmetadata.common.Constant;
import de.smartics.maven.plugin.buildmetadata.common.Constant.Section;

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

    renderSections(buildMetaDataProperties);

    renderFooter();
    sink.section1_();
  }

  private void renderSections(final Properties buildMetaDataProperties)
  {
    for (final Section section : Constant.REPORT_PROPERTIES)
    {
      sink.sectionTitle2();
      sink.text(messages.getString(section.getTitleKey()));
      sink.sectionTitle2_();
      renderTableStart();
      for (final String key : section.getProperties())
      {
        renderCell(buildMetaDataProperties, key);
      }
      renderTableEnd();
    }
  }

  private void renderTableEnd()
  {
    sink.table_();
  }

  private void renderTableStart()
  {
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
  }

  /**
   * Renders a single cell of the table.
   *
   * @param buildMetaDataProperties build meta data properties to access the
   *          data to be rendered.
   * @param key the key to the data to be rendered.
   */
  private void renderCell(final Properties buildMetaDataProperties,
      final String key)
  {
    final Object value = buildMetaDataProperties.get(key);
    if (value != null)
    {
      sink.tableRow();
      sink.tableCell();
      sink.text(getLabel(key));
      sink.tableCell_();
      sink.tableCell();
      if (Constant.PROP_NAME_MAVEN_ACTIVE_PROFILES.equals(key))
      {
        renderMultiTupleValue(buildMetaDataProperties, value,
            Constant.MAVEN_ACTIVE_PROFILE_PREFIX);
      }
      else if (Constant.PROP_NAME_SCM_LOCALLY_MODIFIED_FILES.equals(key))
      {
        renderMultiValue(value);
      }
      else if (Constant.PROP_NAME_MAVEN_GOALS.equals(key))
      {
        renderMultiValue(value);
      }
      else if (Constant.PROP_NAME_MAVEN_FILTERS.equals(key))
      {
        renderMultiValue(value);
      }
      else
      {
        sink.text(String.valueOf(value));
      }
      sink.tableCell_();
      sink.tableRow_();
    }
  }

  private void renderMultiTupleValue(
      final Properties buildMetaDataProperties, final Object value,
      final String subKeyPrefix)
  {
    final String stringValue = prettify((String) value);
    if (hasMultipleValues(stringValue))
    {
      final StringTokenizer tokenizer = new StringTokenizer(stringValue, ",");
      sink.numberedList(Sink.NUMBERING_DECIMAL);
      while (tokenizer.hasMoreTokens())
      {
        final String profileName = tokenizer.nextToken().trim();
        final String subKey = subKeyPrefix + '.' + profileName;
        final Object subValue = buildMetaDataProperties.get(subKey);
        final String item = profileName + ':' + subValue;
        sink.listItem();
        sink.text(item);
        sink.listItem_();
      }
      sink.numberedList_();
    }
    else
    {
      sink.text(String.valueOf(value));
    }
  }

  private void renderMultiValue(final Object value)
  {
    final String stringValue = prettify((String) value);
    if (hasMultipleValues(stringValue))
    {
      final StringTokenizer tokenizer = new StringTokenizer(stringValue, ",");
      sink.numberedList(Sink.NUMBERING_DECIMAL);
      while (tokenizer.hasMoreTokens())
      {
        final String subValue = tokenizer.nextToken();
        sink.listItem();
        sink.text(String.valueOf(subValue));
        sink.listItem_();
      }
      sink.numberedList_();
    }
    else
    {
      sink.text(stringValue);
    }
  }

  private String prettify(final String string)
  {
    final String trimmed = string.trim();
    final int end = trimmed.length() - 1;
    if (trimmed.charAt(0) == '[' && trimmed.charAt(end) == ']')
    {
      return trimmed.substring(1, end);
    }
    return trimmed;
  }

  private boolean hasMultipleValues(final String stringValue)
  {
    return stringValue.indexOf(',') != -1;
  }

  private String getLabel(final String key)
  {
    try
    {
      return messages.getString(key);
    }
    catch (final MissingResourceException e)
    {
      return key;
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
                                     + this.buildMetaDataPropertiesFile + "'.",
          e);
    }
    finally
    {
      IOUtils.closeQuietly(inStream);
    }
    return buildMetaDataProperties;
  }

  // --- object basics --------------------------------------------------------

}
