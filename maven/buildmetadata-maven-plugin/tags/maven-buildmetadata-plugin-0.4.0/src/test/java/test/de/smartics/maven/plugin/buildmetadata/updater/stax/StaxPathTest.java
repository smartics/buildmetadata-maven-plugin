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

import javax.xml.namespace.QName;
import javax.xml.stream.XMLEventFactory;
import javax.xml.stream.events.XMLEvent;

import de.smartics.maven.plugin.buildmetadata.updater.stax.StaxPath;
import junit.framework.TestCase;

/**
 * Tests the StAX path expression.
 * 
 * @author <a href="mailto:robert.reiner@smartics.de">Robert Reiner</a>
 * @version $Revision$
 */
public class StaxPathTest extends TestCase
{
  // ********************************* Fields *********************************

  // --- constants ------------------------------------------------------------

  // --- members --------------------------------------------------------------

  /**
   * The event factory to be used by this tests.
   */
  private final XMLEventFactory eventFactory = XMLEventFactory.newInstance();

  // ****************************** Initializer *******************************

  // ****************************** Constructors ******************************

  /**
   * Constructor.
   */
  public StaxPathTest()
  {
    super("stax.path");
  }

  // ****************************** Inner Classes *****************************

  // ********************************* Methods ********************************

  // --- prepare --------------------------------------------------------------

  // --- helper ---------------------------------------------------------------

  /**
   * Tests the <code>null</code> value as path argument.
   */
  public void testNull()
  {
    try
    {
      new StaxPath(null);
      fail("Null path must not be allowed.");
    }
    catch (final NullPointerException e)
    {
      assertTrue(true);
    }
  }

  /**
   * Tests the empty string as path argument value.
   */
  public void testEmpty()
  {
    try
    {
      new StaxPath("");
      fail("Empty path must not be allowed.");
    }
    catch (final IllegalArgumentException e)
    {
      assertTrue(true);
    }
  }

  /**
   * Tests a relative path that is currently not allowed.
   */
  public void testRelativePath()
  {
    try
    {
      new StaxPath("a/b/c");
      fail("Relative path is currently not allowed.");
    }
    catch (final IllegalArgumentException e)
    {
      assertTrue(true);
    }
  }

  /**
   * Tests matching the root element.
   */
  public void testRootPath()
  {
    final StaxPath path = new StaxPath("/");

    assertFalse("No document started.", path.isMatch());
    final XMLEvent event1 = eventFactory.createStartDocument();
    path.handleEvent(event1);
    assertTrue("Root must match.", path.isMatch());
    final XMLEvent event2 = eventFactory.createCData("Some content");
    path.handleEvent(event2);
    assertTrue("Still in root.", path.isMatch());
    final XMLEvent event3 = eventFactory.createStartElement("a", null, "html");
    path.handleEvent(event3);
    assertFalse("<html>.", path.isMatch());
    final XMLEvent event4 = eventFactory.createStartElement("a", null, "body");
    path.handleEvent(event4);
    assertFalse("<body>.", path.isMatch());
    final XMLEvent event5 = eventFactory.createEndElement("a", null, "body");
    path.handleEvent(event5);
    assertFalse("</body>.", path.isMatch());
    final XMLEvent event6 = eventFactory.createEndElement("a", null, "html");
    path.handleEvent(event6);
    assertTrue("</html>.", path.isMatch());
    final XMLEvent event7 = eventFactory.createEndDocument();
    path.handleEvent(event7);
    assertFalse("End of document reached.", path.isMatch());
  }

  /**
   * Tests matching a sample path.
   */
  public void testOneElementPath()
  {
    final StaxPath path = new StaxPath("/a:html");

    assertFalse("No document started.", path.isMatch());
    final XMLEvent event1 = eventFactory.createStartDocument();
    path.handleEvent(event1);
    assertFalse("Root must not match.", path.isMatch());
    final XMLEvent event2 = eventFactory.createCData("Some content");
    path.handleEvent(event2);
    assertFalse("Still in root.", path.isMatch());
    final XMLEvent event3 = eventFactory.createStartElement("a", null, "html");
    path.handleEvent(event3);
    assertTrue("<html>.", path.isMatch());
    final XMLEvent event4 = eventFactory.createStartElement("a", null, "body");
    path.handleEvent(event4);
    assertFalse("<body>.", path.isMatch());
    final XMLEvent event5 = eventFactory.createEndElement("a", null, "body");
    path.handleEvent(event5);
    assertTrue("</body>.", path.isMatch());
    final XMLEvent event6 = eventFactory.createEndElement("a", null, "html");
    path.handleEvent(event6);
    assertFalse("</html>.", path.isMatch());
    final XMLEvent event7 = eventFactory.createEndDocument();
    path.handleEvent(event7);
    assertFalse("End of document reached.", path.isMatch());
  }

  /**
   * Tests matching a sample path.
   */
  public void testLongPath()
  {
    final StaxPath path = new StaxPath("/html/body/h1");

    assertFalse("No document started.", path.isMatch());
    final XMLEvent event1 = eventFactory.createStartDocument();
    path.handleEvent(event1);
    assertFalse("Root must not match.", path.isMatch());
    final XMLEvent event2 = eventFactory.createCData("Some content");
    path.handleEvent(event2);
    assertFalse("Still in root.", path.isMatch());
    final XMLEvent event3 = eventFactory.createStartElement(new QName("html"),
        null, null);
    path.handleEvent(event3);
    assertFalse("<html>.", path.isMatch());
    final XMLEvent event4 = eventFactory.createStartElement(new QName("body"),
        null, null);
    path.handleEvent(event4);
    assertFalse("<body>.", path.isMatch());
    final XMLEvent event4a = eventFactory.createStartElement(new QName("h1"),
        null, null);
    path.handleEvent(event4a);
    assertTrue("<h1>.", path.isMatch());
    final XMLEvent event4b = eventFactory.createEndElement(new QName("h1"),
        null);
    path.handleEvent(event4b);
    assertFalse("</h1>.", path.isMatch());
    final XMLEvent event5 = eventFactory.createEndElement(new QName("body"),
        null);
    path.handleEvent(event5);
    assertFalse("</body>.", path.isMatch());
    final XMLEvent event6 = eventFactory.createEndElement(new QName("html"),
        null);
    path.handleEvent(event6);
    assertFalse("</html>.", path.isMatch());
    final XMLEvent event7 = eventFactory.createEndDocument();
    path.handleEvent(event7);
    assertFalse("End of document reached.", path.isMatch());
  }

  /**
   * Tests matching a sample path.
   */
  public void testNoMatchingPath()
  {
    final StaxPath path = new StaxPath("/project/properties");

    final XMLEvent event1 = eventFactory.createStartDocument();
    path.handleEvent(event1);
    final XMLEvent event3 = eventFactory.createStartElement(
        new QName("project"), null, null);
    path.handleEvent(event3);
    assertFalse("<project>.", path.isMatch());
    final XMLEvent event4 = eventFactory.createStartElement(
        new QName("profile"), null, null);
    path.handleEvent(event4);
    assertFalse("<profile>.", path.isMatch());
    final XMLEvent event4a = eventFactory.createStartElement(new QName(
        "properties"), null, null);
    path.handleEvent(event4a);
    assertFalse("<properties>.", path.isMatch());
    final XMLEvent event4b = eventFactory.createEndElement(new QName(
        "properties"), null);
    path.handleEvent(event4b);
    assertFalse("</properties>.", path.isMatch());
    final XMLEvent event5 = eventFactory.createEndElement(new QName("profile"),
        null);
    path.handleEvent(event5);
    assertFalse("</profile>.", path.isMatch());
    final XMLEvent event6 = eventFactory.createEndElement(new QName("project"),
        null);
    path.handleEvent(event6);
    assertFalse("</project>.", path.isMatch());
    final XMLEvent event7 = eventFactory.createEndDocument();
    path.handleEvent(event7);
    assertFalse("End of document reached.", path.isMatch());
  }

  /**
   * Tests a path with a no match in front.
   */
  public void testPreHill()
  {
    final StaxPath path = new StaxPath("/project/properties");

    final XMLEvent event1 = eventFactory.createStartDocument();
    path.handleEvent(event1);
    final XMLEvent event3 = eventFactory.createStartElement(
        new QName("project"), null, null);
    path.handleEvent(event3);
    assertFalse("<project>.", path.isMatch());
    final XMLEvent event4 = eventFactory.createStartElement(
        new QName("profile"), null, null);
    path.handleEvent(event4);
    assertFalse("<profile>.", path.isMatch());
    final XMLEvent event4a = eventFactory.createStartElement(new QName(
        "properties"), null, null);
    path.handleEvent(event4a);
    assertFalse("<properties>.", path.isMatch());
    final XMLEvent event4b = eventFactory.createEndElement(new QName(
        "properties"), null);
    path.handleEvent(event4b);
    assertFalse("</properties>.", path.isMatch());
    final XMLEvent event5 = eventFactory.createEndElement(new QName("profile"),
        null);
    path.handleEvent(event5);
    assertFalse("</profile>.", path.isMatch());
    final XMLEvent event6 = eventFactory.createStartElement(new QName(
        "properties"), null, null);
    path.handleEvent(event6);
    assertTrue("<properties>.", path.isMatch());
    final XMLEvent event7 = eventFactory.createEndElement(new QName(
        "properties"), null);
    path.handleEvent(event7);
    assertFalse("</properties>.", path.isMatch());
    final XMLEvent event8 = eventFactory.createEndElement(new QName("project"),
        null);
    path.handleEvent(event8);
    assertFalse("</project>.", path.isMatch());
    final XMLEvent event9 = eventFactory.createEndDocument();
    path.handleEvent(event9);
    assertFalse("End of document reached.", path.isMatch());
  }
  // --- tests ----------------------------------------------------------------

}
