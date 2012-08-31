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

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Properties;

import org.apache.maven.scm.manager.ScmManager;

import de.smartics.maven.plugin.buildmetadata.scm.Revision;
import de.smartics.maven.plugin.buildmetadata.scm.RevisionNumberFetcher;
import de.smartics.maven.plugin.buildmetadata.scm.ScmException;
import de.smartics.maven.plugin.buildmetadata.scm.maven.MavenScmRevisionNumberFetcher;
import de.smartics.maven.plugin.buildmetadata.scm.maven.ScmAccessInfo;
import de.smartics.maven.plugin.buildmetadata.scm.maven.ScmConnectionInfo;

/**
 * Helper to access the revision information.
 * 
 * @author <a href="mailto:robert.reiner@smartics.de">Robert Reiner</a>
 * @version $Revision:591 $
 */
public class RevisionHelper
{
  // ********************************* Fields *********************************

  // --- constants ------------------------------------------------------------

  // --- members --------------------------------------------------------------

  /**
   * The manager instance to access the SCM system. Provides access to the
   * repository and the provider information.
   */
  private final ScmManager scmManager;

  /**
   * The information to connect to the SCM.
   */
  private final ScmConnectionInfo scmConnectionInfo;

  /**
   * The information to query the SCM.
   */
  private final ScmAccessInfo scmAccessInfo;

  /**
   * The date pattern to use to format revision dates.
   */
  private final String buildDatePattern;

  // ****************************** Initializer *******************************

  // ****************************** Constructors ******************************

  /**
   * Default constructor.
   * 
   * @param scmManager the manager instance to access the SCM system.
   * @param scmConnectionInfo the information to connect to the SCM.
   * @param scmAccessInfo the information to query the SCM.
   * @param buildDatePattern the date pattern to use to format revision dates.
   */
  public RevisionHelper(final ScmManager scmManager,
      final ScmConnectionInfo scmConnectionInfo,
      final ScmAccessInfo scmAccessInfo, final String buildDatePattern)
  {
    this.scmManager = scmManager;
    this.scmConnectionInfo = scmConnectionInfo;
    this.scmAccessInfo = scmAccessInfo;
    this.buildDatePattern = buildDatePattern;
  }

  // ****************************** Inner Classes *****************************

  // ********************************* Methods ********************************

  // --- init -----------------------------------------------------------------

  // --- get&set --------------------------------------------------------------

  // --- business -------------------------------------------------------------

  /**
   * Fetches the revision information and adds it to the property sets.
   * 
   * @param projectProperties the project properties.
   * @param buildMetaDataProperties the build meta data properties.
   * @throws ScmException if the creation of the SCM information failed.
   */
  public void provideScmBuildInfo(

  final Properties projectProperties, final Properties buildMetaDataProperties)
      throws ScmException
  {
    final RevisionNumberFetcher revisionFetcher =
        new MavenScmRevisionNumberFetcher(scmManager, scmConnectionInfo,
            scmAccessInfo);
    final Revision revision = revisionFetcher.fetchLatestRevisionNumber();
    final String revisionId = revision.getId();
    buildMetaDataProperties.setProperty(Constant.PROP_NAME_SCM_REVISION_ID,
        revisionId);
    projectProperties.setProperty(Constant.PROP_NAME_SCM_REVISION_ID,
        revisionId);
    final Date revisionDate = revision.getDate();
    final DateFormat format = new SimpleDateFormat(buildDatePattern);
    final String revisionDateString = format.format(revisionDate);
    buildMetaDataProperties.setProperty(Constant.PROP_NAME_SCM_REVISION_DATE,
        revisionDateString);
    projectProperties.setProperty(Constant.PROP_NAME_SCM_REVISION_DATE,
        revisionDateString);
  }

  // --- object basics --------------------------------------------------------

}
