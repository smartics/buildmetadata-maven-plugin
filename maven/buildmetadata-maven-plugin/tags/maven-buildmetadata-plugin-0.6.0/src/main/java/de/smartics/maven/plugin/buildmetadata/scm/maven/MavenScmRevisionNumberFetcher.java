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

import java.util.List;

import org.apache.maven.scm.ChangeFile;
import org.apache.maven.scm.ChangeSet;
import org.apache.maven.scm.ScmVersion;
import org.apache.maven.scm.command.changelog.ChangeLogScmResult;
import org.apache.maven.scm.command.changelog.ChangeLogSet;
import org.apache.maven.scm.manager.NoSuchScmProviderException;
import org.apache.maven.scm.manager.ScmManager;
import org.apache.maven.scm.provider.ScmProvider;
import org.apache.maven.scm.repository.ScmRepository;

import de.smartics.maven.plugin.buildmetadata.scm.Revision;
import de.smartics.maven.plugin.buildmetadata.scm.RevisionNumberFetcher;
import de.smartics.maven.plugin.buildmetadata.scm.ScmException;

/**
 * Implementation on the Maven SCM implementation to fetch the latest revision
 * number.
 * 
 * @author <a href="mailto:robert.reiner@smartics.de">Robert Reiner</a>
 * @version $Revision:591 $
 */
public class MavenScmRevisionNumberFetcher implements RevisionNumberFetcher
{
  // ********************************* Fields *********************************

  // --- constants ------------------------------------------------------------

  // --- members --------------------------------------------------------------

  /**
   * The SCM manager to access to SCM system.
   */
  private final ScmManager scmManager;

  /**
   * The information to connect to the SCM system.
   */
  private final ScmConnectionInfo scmConnectionInfo;

  /**
   * Information to retrieve the revision information from the SCM after the
   * connection is established.
   */
  private final ScmAccessInfo scmAccessInfo;

  // ****************************** Initializer *******************************

  // ****************************** Constructors ******************************

  /**
   * Default constructor.
   * 
   * @param scmManager the SCM manager to access to SCM system.
   * @param scmConnectionInfo the information to connect to the SCM system.
   * @param scmAccessInfo the value for scmAccessInfo.
   */
  public MavenScmRevisionNumberFetcher(final ScmManager scmManager,
      final ScmConnectionInfo scmConnectionInfo,
      final ScmAccessInfo scmAccessInfo)
  {
    this.scmManager = scmManager;
    this.scmConnectionInfo = scmConnectionInfo;
    this.scmAccessInfo = scmAccessInfo;
  }

  // ****************************** Inner Classes *****************************

  // ********************************* Methods ********************************

  // --- init -----------------------------------------------------------------

  // --- get&set --------------------------------------------------------------

  // --- business -------------------------------------------------------------

  /**
   * {@inheritDoc}
   * 
   * @see de.smartics.maven.plugin.buildmetadata.scm.RevisionNumberFetcher#fetchLatestRevisionNumber()
   */
  public Revision fetchLatestRevisionNumber() throws ScmException
  {
    final ScmRepository repository =
        scmConnectionInfo.createRepository(scmManager);
    final ScmProvider provider = createScmProvider(repository);
    final ChangeLogScmResult result =
        scmAccessInfo.fetchChangeLog(repository, provider);

    if (result != null)
    {
      final ChangeLogSet changeLogSet = result.getChangeLog();

      final Revision revision = findEndVersion(changeLogSet);
      return revision;
    }
    else
    {
      return null;
    }
  }

  /**
   * Finds the largest revision number.
   * 
   * @impl Currently we assume the the largest revision is provided by the last
   *       entry of the set.
   * @param changeLogSet the set of change log entries to compare the revision
   *          numbers to find the largest.
   * @return the largest revision number from the set or <code>null</code> if no
   *         end version can be found.
   */
  @SuppressWarnings("unchecked")
  private Revision findEndVersion(final ChangeLogSet changeLogSet)
  {
    final ScmVersion endVersion = changeLogSet.getEndVersion();
    if (endVersion != null)
    {
      return new MavenRevision(endVersion, changeLogSet.getEndDate());
    }

    final List<ChangeSet> changeSets = changeLogSet.getChangeSets();
    if (!changeSets.isEmpty())
    {
      final int lastIndex = changeSets.size() - 1;
      for (int index = lastIndex; index >= 0; index--)
      {
        final ChangeSet set = changeSets.get(lastIndex);
        final List<ChangeFile> changeFiles = set.getFiles();
        if (!changeFiles.isEmpty())
        {
          final ChangeFile file = changeFiles.get(0);
          final String revision = file.getRevision();
          if (revision != null)
          {
            return new StringRevision(revision, set.getDate());
          }
        }
      }
    }

    return null;
  }

  /**
   * Creates the provider instance to access the given repository.
   * 
   * @param repository the repository to access with the provider to be created.
   * @return the provider to access the given repository.
   * @throws ScmException if the provider cannot be created.
   */
  private ScmProvider createScmProvider(final ScmRepository repository)
      throws ScmException
  {
    try
    {
      final ScmProvider provider =
          scmManager.getProviderByRepository(repository);
      return provider;
    }
    catch (final NoSuchScmProviderException e)
    {
      throw new ScmException("Cannot create SCM provider.", e);
    }
  }

  // --- object basics --------------------------------------------------------

}
