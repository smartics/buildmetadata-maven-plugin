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

import java.io.Serializable;

import javax.xml.namespace.QName;
import javax.xml.stream.events.XMLEvent;

import org.apache.commons.lang.StringUtils;

/**
 * A simple helper to ensure that we are inside a certain element.
 * 
 * @author <a href="mailto:robert.reiner@smartics.de">Robert Reiner</a>
 * @version $Revision$
 */
public final class StaxPath implements Serializable
{
  // ********************************* Fields *********************************

  // --- constants ------------------------------------------------------------

  /**
   * The class version identifier.
   */
  private static final long serialVersionUID = 1L;

  // --- members --------------------------------------------------------------

  /**
   * The path with each element at its position.
   * 
   * @serial
   */
  private final String[] path;

  /**
   * The current element to match to go to the next step.
   * 
   * @serial
   */
  private int pointer;

  /**
   * The current depth.
   * 
   * @serial
   */
  private int depth;

  /**
   * Flag that indicates that the current context in the document matches the
   * path.
   * 
   * @serial
   */
  private boolean matches;

  // ****************************** Initializer *******************************

  // ****************************** Constructors ******************************

  /**
   * Default constructor.
   * 
   * @param path the path to the element in well known form. This is a value
   *        like <code>/html/body/h1</code>.
   * @throws NullPointerException if path is <code>null</code>.
   * @throws IllegalArgumentException if this is blank or not a relative path.
   *         Relative pathes may be accepted in future versions without notice.
   * @todo support for wildcards and xpath expressions.
   */
  public StaxPath(final String path) throws NullPointerException,
      IllegalArgumentException
  {
    if (path == null)
    {
      throw new NullPointerException("Path must not be 'null'.");
    }
    final String strippedPath = StringUtils.remove(path, ' ');
    if (StringUtils.isBlank(strippedPath) || !strippedPath.startsWith("/"))
    {
      throw new IllegalArgumentException(
          "Relative pathes currently not allowed. Use absolute pathes only.");
    }
    this.path = StringUtils.split(strippedPath, '/');
  }

  // ****************************** Inner Classes *****************************

  // ********************************* Methods ********************************

  // --- init -----------------------------------------------------------------

  // --- get&set --------------------------------------------------------------

  /**
   * Checks whether or not the path matches the current context.
   * 
   * @return <code>true</code> if the path matches, <code>false</code>
   *         otherwise.
   */
  public boolean isMatch()
  {
    return matches;
  }

  // --- business -------------------------------------------------------------

  /**
   * Handles the event and returns whether or not the context now matches the
   * path expression.
   * 
   * @param event the new encountered event.
   * @return <code>true</code> if the path matches the current configuration,
   *         <code>false</code> otherwise.
   */
  public boolean handleEvent(final XMLEvent event)
  {
    if (event.isStartElement())
    {
      depth++;
      if (pointer >= path.length || matches)
      {
        pointer++;
        this.matches = false;
      }
      else
      {
        final QName qname = event.asStartElement().getName();
        final String name = getFullQualifiedString(qname);
        if (path[pointer].equals(name))
        {
          pointer++;
          if (pointer == path.length && depth == pointer)
          {
            this.matches = true;
          }
        }
      }
    }
    else if (event.isEndElement())
    {
      depth--;
      if (pointer >= path.length)
      {
        pointer--;
        if (pointer == path.length && depth == pointer)
        {
          matches = true;
        }
        else
        {
          matches = false;
        }
      }
      else
      {
        final QName qname = event.asEndElement().getName();
        final String name = getFullQualifiedString(qname);
        if (pointer > 0 && path[pointer - 1].equals(name))
        {
          pointer--;
          if (pointer >= path.length)
          {
            this.matches = false;
          }
        }
      }
    }
    else if (event.isStartDocument())
    {
      if (path.length == 0)
      {
        matches = true;
      }
    }
    else if (event.isEndDocument())
    {
      matches = false;
    }

    return matches;
  }

  /**
   * Returns the full qualified string for the given qualified name.
   * 
   * @param qname the qualified name.
   * @return the qualified string (prefix and local part separated by a colon).
   */
  private static String getFullQualifiedString(final QName qname)
  {
    if (!StringUtils.isBlank(qname.getPrefix()))
    {
      return qname.getPrefix() + ':' + qname.getLocalPart();
    }
    else
    {
      return qname.getLocalPart();
    }
  }

  // --- object basics --------------------------------------------------------
}
