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

import java.util.Map;

/**
 * Configuration instance to create instances of
 * {@link de.smartics.maven.plugin.buildmetadata.data.MetaDataProvider} by the
 * {@link de.smartics.maven.plugin.buildmetadata.data.MetaDataProviderBuilder}.
 *
 * @author <a href="mailto:robert.reiner@smartics.de">Robert Reiner</a>
 * @version $Revision:591 $
 */
public class Provider
{
  // ********************************* Fields *********************************

  // --- constants ------------------------------------------------------------

  // --- members --------------------------------------------------------------

  /**
   * The class to instantiate.
   */
  private String type;

  /**
   * Properties to set.
   */
  private Map<String, String> properties;

  // ****************************** Initializer *******************************

  // ****************************** Constructors ******************************

  // ****************************** Inner Classes *****************************

  // ********************************* Methods ********************************

  // --- init -----------------------------------------------------------------

  // --- get&set --------------------------------------------------------------

  /**
   * Returns the class to instantiate.
   *
   * @return the class to instantiate.
   */
  public String getType()
  {
    return type;
  }

  /**
   * Returns the value for properties.
   * <p>
   * Properties to set.
   *
   * @return the value for properties.
   */
  public Map<String, String> getProperties()
  {
    return properties;
  }

  // --- business -------------------------------------------------------------

  // --- object basics --------------------------------------------------------

}
