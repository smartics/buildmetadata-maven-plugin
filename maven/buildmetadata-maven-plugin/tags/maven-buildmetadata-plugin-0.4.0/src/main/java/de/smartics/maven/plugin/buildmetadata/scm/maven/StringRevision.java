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
package de.smartics.maven.plugin.buildmetadata.scm.maven;

import java.io.Serializable;
import java.util.Date;

import de.smartics.maven.plugin.buildmetadata.scm.Revision;

/**
 * Implementation for a simple revision string.
 * 
 * @author <a href="mailto:robert.reiner@smartics.de">Robert Reiner</a>
 * @version $Revision:591 $
 */
public class StringRevision implements Revision, Serializable
{
  // ********************************* Fields *********************************

  // --- constants ------------------------------------------------------------

  /**
   * The class version identifier.
   */
  private static final long serialVersionUID = 1L;

  // --- members --------------------------------------------------------------

  /**
   * The ID of the revision.
   */
  private final String id;

  /**
   * The revision date.
   */
  private final Date date;

  // ****************************** Initializer *******************************

  // ****************************** Constructors ******************************

  /**
   * Default constructor.
   * 
   * @param id the ID of the revision.
   * @param date the revision date.
   */
  public StringRevision(final String id, final Date date)
  {
    this.id = id;
    this.date = date;
  }

  // ****************************** Inner Classes *****************************

  // ********************************* Methods ********************************

  // --- init -----------------------------------------------------------------

  // --- get&set --------------------------------------------------------------

  /**
   * Returns the ID of the revision.
   * 
   * @return the ID of the revision.
   */
  public String getId()
  {
    return id;
  }

  /**
   * Returns the revision date.
   * 
   * @return the revision date.
   */
  public Date getDate()
  {
    return date;
  }

  // --- business -------------------------------------------------------------

  // --- object basics --------------------------------------------------------

  /**
   * {@inheritDoc}
   */
  public String toString()
  {
    return id + "at " + date;
  }
}
