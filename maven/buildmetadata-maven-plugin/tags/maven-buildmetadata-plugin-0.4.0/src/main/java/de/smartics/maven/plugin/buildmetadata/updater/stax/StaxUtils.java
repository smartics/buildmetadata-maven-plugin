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

import javax.xml.namespace.QName;
import javax.xml.stream.XMLEventFactory;
import javax.xml.stream.XMLEventReader;
import javax.xml.stream.XMLEventWriter;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.events.XMLEvent;

/**
 * Utility class for StAX.
 * 
 * @author <a href="mailto:robert.reiner@smartics.de">Robert Reiner</a>
 * @version $Revision$
 */
public final class StaxUtils
{
  // ********************************* Fields *********************************

  // --- constants ------------------------------------------------------------

  // --- members --------------------------------------------------------------

  // ****************************** Initializer *******************************

  // ****************************** Constructors ******************************

  /**
   * Utility class.
   */
  private StaxUtils()
  {
  }

  // ****************************** Inner Classes *****************************

  // ********************************* Methods ********************************

  // --- init -----------------------------------------------------------------

  // --- get&set --------------------------------------------------------------

  // --- business -------------------------------------------------------------

  /**
   * Closes the reader quietly.
   * 
   * @param reader the reader to close. May be <code>null</code>.
   */
  public static void closeQuietly(final XMLEventReader reader)
  {
    if (reader != null)
    {
      try
      {
        reader.close();
      }
      catch (final XMLStreamException e)
      {
        // ignore silently is what we have been asked to do.
      }
    }
  }

  /**
   * Closes the writer quietly.
   * 
   * @param writer the writer to close. May be <code>null</code>.
   */
  public static void closeQuietly(final XMLEventWriter writer)
  {
    if (writer != null)
    {
      try
      {
        writer.close();
      }
      catch (final XMLStreamException e)
      {
        // ignore silently is what we have been asked to do.
      }
    }
  }

  /**
   * Writes the start element with the given generic identifier.
   * 
   * @param eventFactory the event factory to use.
   * @param writer the writer to write to.
   * @param gi the generic identifier of the tag to write.
   * @throws XMLStreamException on any problem writing the tag.
   */
  public static void writeStartElement(final XMLEventFactory eventFactory,
      final XMLEventWriter writer, final String gi) throws XMLStreamException
  {
    final XMLEvent event = eventFactory.createStartElement(new QName(gi), null,
        null);
    writer.add(event);
  }

  /**
   * Writes the end element with the given generic identifier.
   * 
   * @param eventFactory the event factory to use.
   * @param writer the writer to write to.
   * @param gi the generic identifier of the tag to write.
   * @throws XMLStreamException on any problem writing the tag.
   */
  public static void writeEndElement(final XMLEventFactory eventFactory,
      final XMLEventWriter writer, final String gi) throws XMLStreamException
  {
    final XMLEvent event = eventFactory.createEndElement(new QName(gi), null);
    writer.add(event);
  }

  // --- object basics --------------------------------------------------------
}
