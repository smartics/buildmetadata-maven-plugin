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

import java.io.File;
import java.io.Serializable;
import java.util.List;

import org.apache.maven.scm.ScmBranch;
import org.apache.maven.scm.ScmFileSet;
import org.apache.maven.scm.command.changelog.ChangeLogScmResult;
import org.apache.maven.scm.command.changelog.ChangeLogSet;
import org.apache.maven.scm.provider.ScmProvider;
import org.apache.maven.scm.repository.ScmRepository;

import de.smartics.maven.plugin.buildmetadata.scm.ScmException;

/**
 * Provides access information to retrieve revision information from the SCM.
 * 
 * @author <a href="mailto:robert.reiner@smartics.de">Robert Reiner</a>
 * @version $Revision:591 $
 */
public class ScmAccessInfo implements Serializable
{
  // ********************************* Fields *********************************

  // --- constants ------------------------------------------------------------

  /**
   * The class version identifier.
   * <p>
   * The value of this constant is {@value}.
   * </p>
   */
  private static final long serialVersionUID = 1L;

  /**
   * The number of retries to fetch the change log if the first attempt failed
   * to return a non empty set.
   */
  public static final int DEFAULT_RETRY_COUNT = 5;

  // --- members --------------------------------------------------------------

  /**
   * The format of the dates understood by the SCM system.
   */
  private String dateFormat;

  /**
   * The root directory that contains the files under SCM control.
   */
  private File rootDirectory;

  /**
   * The range of the query in days to fetch change log entries from the SCM. If
   * no change logs have been found, the range is incremented up to
   * {@value #DEFAULT_RETRY_COUNT} times. If no change log has been found after
   * these {@value #DEFAULT_RETRY_COUNT} additional queries, the revision number
   * will not be set with a valid value.
   */
  private int queryRangeInDays;

  // ****************************** Initializer *******************************

  // ****************************** Constructors ******************************

  /**
   * Default constructor.
   */
  public ScmAccessInfo()
  {
  }

  // ****************************** Inner Classes *****************************

  // ********************************* Methods ********************************

  // --- init -----------------------------------------------------------------

  // --- get&set --------------------------------------------------------------

  /**
   * Returns the format of the dates understood by the SCM system.
   * 
   * @return the format of the dates understood by the SCM system.
   */
  public String getDateFormat()
  {
    return dateFormat;
  }

  /**
   * Sets the format of the dates understood by the SCM system.
   * 
   * @param dateFormat the format of the dates understood by the SCM system.
   */
  public void setDateFormat(final String dateFormat)
  {
    this.dateFormat = dateFormat;
  }

  /**
   * Returns the root directory that contains the files under SCM control.
   * 
   * @return the root directory that contains the files under SCM control.
   */
  public File getRootDirectory()
  {
    return rootDirectory;
  }

  /**
   * Sets the root directory that contains the files under SCM control.
   * 
   * @param rootDirectory the root directory that contains the files under SCM
   *          control.
   */
  public void setRootDirectory(final File rootDirectory)
  {
    this.rootDirectory = rootDirectory;
  }

  /**
   * Returns the range of the query in days to fetch change log entries from the
   * SCM. If no change logs have been found, the range is incremented up to five
   * (5) times. If no change log has been found after this five queries, the
   * revision number will not be set with a valid value.
   * 
   * @return the range of the query in days to fetch change log entries from the
   *         SCM.
   */
  public int getQueryRangeInDays()
  {
    return queryRangeInDays;
  }

  /**
   * Sets the range of the query in days to fetch change log entries from the
   * SCM. If no change logs have been found, the range is incremented up to five
   * (5) times. If no change log has been found after this five queries, the
   * revision number will not be set with a valid value.
   * 
   * @param queryRangeInDays the range of the query in days to fetch change log
   *          entries from the SCM.
   */
  public void setQueryRangeInDays(final int queryRangeInDays)
  {
    this.queryRangeInDays = queryRangeInDays;
  }

  // --- business -------------------------------------------------------------

  /**
   * Returns the result of the change log query.
   * 
   * @param repository the repository to fetch the change log information from.
   * @param provider the provider to use to access the repository.
   * @return the change log entries that match the query, <code>null</code> if
   *         none have been found.
   * @throws ScmException if the change log cannot be fetched.
   */
  public ChangeLogScmResult fetchChangeLog(
      final ScmRepository repository,
      final ScmProvider provider) throws ScmException
  {
    try
    {
      ChangeLogScmResult result = null;
      int currentRange = queryRangeInDays;
      for (int i = 0; i <= DEFAULT_RETRY_COUNT; i++)
      {
        result =
            provider.changeLog(repository, createFileSet(), null, null,
                currentRange, (ScmBranch) null, dateFormat);
        if (!isEmpty(result))
        {
          return result;
        }
        currentRange += queryRangeInDays;
      }
      return result;
    }
    catch (final org.apache.maven.scm.ScmException e)
    {
      throw new ScmException("Cannot fetch change log from repository.", e);
    }
  }

  /**
   * Checks if the given result contains change logs or not.
   * 
   * @param result the result to be checked.
   * @return <code>true</code> if change logs have been found,
   *         <code>false</code> if any reference up the path to the change logs
   *         is <code>null</code> or the set is empty.
   */
  private boolean isEmpty(final ChangeLogScmResult result)
  {
    if (result != null)
    {
      final ChangeLogSet changeLogSet = result.getChangeLog();
      if (changeLogSet != null)
      {
        final List<?> changeLogSets = changeLogSet.getChangeSets();
        if (changeLogSets != null)
        {
          return changeLogSets.isEmpty();
        }
      }
    }
    return false;
  }

  /**
   * Creates the file set on the root directory of the checked out project.
   * 
   * @return the file set on the root directory of the checked out project.
   */
  protected ScmFileSet createFileSet()
  {
    return new ScmFileSet(rootDirectory);
  }

  // --- object basics --------------------------------------------------------

}
