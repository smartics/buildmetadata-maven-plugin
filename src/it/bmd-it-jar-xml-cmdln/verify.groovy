def fileXml = new File(basedir, 'target/classes/META-INF/buildmetadata.xml')
assert fileXml.exists()


def buildmetadata = new XmlSlurper().parse(fileXml)

def cmdline = buildmetadata.runtime.maven.commandline.text()
assert cmdline.contains("clean package");
