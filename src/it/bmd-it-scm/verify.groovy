/*
 * Copyright 2006-2014 smartics, Kronseder & Reiner GmbH
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

@GrabResolver(
     name='jgit-repository',
     root='http://download.eclipse.org/jgit/maven'
)
@Grab(
     group='org.eclipse.jgit',
     module='org.eclipse.jgit',
     version='1.1.0.201109151100-r'
)

def fileProps = new File(basedir, 'build.properties')

assert fileProps.exists()
assert fileProps.text.contains("clean, install");


import org.eclipse.jgit.*
import org.eclipse.jgit.lib.*
import org.eclipse.jgit.util.*
import org.eclipse.jgit.revwalk.*



directory = new File(basedir, "../../../")
System.out.println ("Got directory " + directory)
Repository repository =
  RepositoryCache.open(
       RepositoryCache.FileKey.lenient(directory,FS.DETECTED),
       true)

Ref head = repository.getRef("refs/heads/master");
System.out.println("Found head: " + head);

// a RevWalk allows to walk over commits based on some filtering that is defined
RevWalk walk = new RevWalk(repository)
RevCommit commit = walk.parseCommit(head.getObjectId());

System.out.println("Found Commit: " + commit.getName());

assert fileProps.text.contains(commit.getName());
