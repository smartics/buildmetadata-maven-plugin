/*
 * Copyright 2006-2025 smartics, Kronseder & Reiner GmbH
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not
 * use this file except in compliance with the License. You may obtain a copy of
 * the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
 * License for the specific language governing permissions and limitations under
 * the License.
 */
def fileXml = new File(basedir, 'target/classes/META-INF/buildmetadata.xml')
assert fileXml.exists()


def buildmetadata = new XmlSlurper().parse(fileXml)

def compilerTarget = buildmetadata.runtime.java.'compiler.target'.text()
assert compilerTarget.contains("1.6");

