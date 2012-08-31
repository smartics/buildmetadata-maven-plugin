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
package test.de.smartics.maven.plugin.buildmetadata.updater.stax;

import java.io.File;
import java.lang.reflect.Method;
import java.net.URL;
import java.util.Properties;

import org.apache.commons.io.FileUtils;
import org.apache.commons.lang.StringUtils;

import de.smartics.maven.plugin.buildmetadata.Constant;
import de.smartics.maven.plugin.buildmetadata.updater.stax.StaxPomUpdater;
import junit.framework.TestCase;

/**
 * Tests the updating of POMs.
 * 
 * @author <a href="mailto:robert.reiner@smartics.de">Robert Reiner</a>
 * @version $Revision$
 */
public class StaxPomUpdaterTest extends TestCase
{
  // ********************************* Fields *********************************

  // --- constants ------------------------------------------------------------

  // --- members --------------------------------------------------------------

  // ****************************** Initializer *******************************

  // ****************************** Constructors ******************************

  /**
   * Constructor.
   */
  public StaxPomUpdaterTest()
  {
    super("stax.pom.updater");
  }

  // ****************************** Inner Classes *****************************

  // ********************************* Methods ********************************

  // --- prepare --------------------------------------------------------------

  /**
   * {@inheritDoc}
   * 
   * @throws Exception {@inheritDoc}
   */
  protected void setUp() throws Exception
  {
    super.setUp();
  }

  /**
   * {@inheritDoc}
   * 
   * @throws Exception {@inheritDoc}
   */
  protected void tearDown() throws Exception
  {
    super.tearDown();
  }

  // --- helper ---------------------------------------------------------------

  /**
   * The helper to read the POM and execute the test.
   * 
   * @param pom the name of the POM file to read.
   * @return the reference to the written POM file.
   * @throws Exception never.
   */
  public File testUpdate(final String pom) throws Exception
  {
    final StaxPomUpdater uut = new StaxPomUpdater();

    final File baseDir = new File(System.getProperty("user.dir"));
    final File pomFile = fetchFile(pom);
    final Properties properties = new Properties();
    // properties.setProperty(Constant.PROP_NAME_BUILD_NUMBER,
    // String.valueOf(10));
    properties.setProperty(Constant.PROP_NAME_BUILD_DATE_CURRENT, "23.12.2006");

    final Method method = StaxPomUpdater.class.getDeclaredMethod(
        "createUpdatedPom", new Class[]
        { File.class, File.class, Properties.class });
    method.setAccessible(true);
    final Object[] args = new Object[]
    { baseDir, pomFile, properties };
    try
    {
      final File file = (File) method.invoke(uut, args);
      return file;
    }
    catch (final Exception e)
    {
      e.printStackTrace();
      throw e;
    }
  }

  /**
   * Fetches the file from the resource directory of this test.
   * 
   * @param pom the name of the POM file to read.
   * @return the requested file.
   */
  private File fetchFile(final String pom)
  {
    final URL url = StaxPomUpdaterTest.class.getResource(pom);
    final String urlString = url.toExternalForm().substring(6);
    final File pomFile = new File('/' + urlString);
    return pomFile;
  }

  // --- tests ----------------------------------------------------------------

  /**
   * Test with POM file <code>pom1.xml</code>.
   * 
   * @throws Exception never.
   */
  public void testUpdate1() throws Exception
  {
    final File file = testUpdate("pom1.xml");
    try
    {
      assertTrue("File does not exist.", file.exists());
      final String result = FileUtils.readFileToString(file, "UTF-8");
      final String expected = FileUtils.readFileToString(
          fetchFile("pom1-expected.xml"), "UTF-8");
      assertEquals("Simple POM.", StringUtils.remove(expected, '\r'),
          StringUtils.remove(result, '\r'));
    }
    finally
    {
      file.delete();
    }
  }

  /**
   * Test with POM file <code>pom2.xml</code>.
   * 
   * @throws Exception never.
   */
  public void testUpdate2() throws Exception
  {
    final File file = testUpdate("pom2.xml");
    assertTrue("File does not exist.", file.exists());
    final String result = FileUtils.readFileToString(file, "UTF-8");
    final String expected = FileUtils.readFileToString(
        fetchFile("pom2-expected.xml"), "UTF-8");
    assertEquals("Simple POM.", StringUtils.remove(expected, '\r'), StringUtils
        .remove(result, '\r'));
  }

  /**
   * Test with POM file <code>pom3.xml</code>.
   * 
   * @throws Exception never.
   */
  public void testUpdate3() throws Exception
  {
    final File file = testUpdate("pom3.xml");
    assertTrue("File does not exist.", file.exists());
    final String result = FileUtils.readFileToString(file, "UTF-8");
    final String expected = FileUtils.readFileToString(
        fetchFile("pom3-expected.xml"), "UTF-8");
    assertEquals("Simple POM.", StringUtils.remove(expected, '\r'), StringUtils
        .remove(result, '\r'));
  }

  /**
   * Test with POM file <code>pom4.xml</code>.
   * 
   * @throws Exception never.
   */
  public void testUpdate4() throws Exception
  {
    final File file = testUpdate("pom4.xml");
    assertTrue("File does not exist.", file.exists());
    final String result = FileUtils.readFileToString(file, "UTF-8");
    final String expected = FileUtils.readFileToString(
        fetchFile("pom4-expected.xml"), "UTF-8");
    assertEquals("Simple POM.", StringUtils.remove(expected, '\r'), StringUtils
        .remove(result, '\r'));
  }

  /**
   * Test with POM file <code>pom5.xml</code>.
   * <p>
   * Checks that not every <code>properties</code> element is written to the
   * POM.
   * 
   * @throws Exception never.
   */
  public void testUpdate5() throws Exception
  {
    final File file = testUpdate("pom5.xml");
    assertTrue("File does not exist.", file.exists());
    final String result = FileUtils.readFileToString(file, "UTF-8");
    final String expected = FileUtils.readFileToString(
        fetchFile("pom5-expected.xml"), "UTF-8");
    assertEquals("Multiple properties elements.", StringUtils.remove(expected,
        '\r'), StringUtils.remove(result, '\r'));
  }

}
