
---


# For developing OSDE application #

## An Overview of the Contributing Process ##

  * Development Environment: eclipse 3.X + subclipse/subversive for contributing to OSDE

  * Development Process: (Refer the following section for more details)
    * Make a branch of the trunk
    * Checkout the branch, make your changes, and then commit your changes
    * Ask for code review of your changes in the branch
    * Once you get approval for your changes, merge your branch into the trunk and then commit

## Setup your development environment ##

  * Download and install eclipse from http://www.eclipse.org/ (**Eclipse for RCP/Plug-in Developers** packages is recommended)

  * Install SVN plugin for eclipse: subclipse (http://subclipse.tigris.org/) or subversive (http://www.eclipse.org/subversive/)

  * Install m2eclipse plugin: http://m2eclipse.sonatype.org/

## How to build Apache Shindig for OSDE ##

  * Download and install Apache Maven2 (2.0.8 or higher): http://maven.apache.org/
    * Run maven to check installation correctly. If you do not see results like below, please check **PATH** enviroment variable contains Maven's bin path.

```
   # mvn -v
   Maven version: 2.0.8
   Java version: 1.6.0_14
   OS name: "linux" version: "2.6.24-24-server" arch: "amd64" Family: "unix"
```

  * Check out the Apache Shindig for OSDE from the code repository
    * trunk of shindig: https://opensocial-development-environment.googlecode.com/svn/trunk/shindig/shindig_796905
    * if you use a svn command line client, try this:

> for commiter:
```
   svn co https://opensocial-development-environment.googlecode.com/svn/trunk/shindig/shindig_796905 shindig_796905 
```

> for others:
```
   svn co http://opensocial-development-environment.googlecode.com/svn/trunk/shindig/shindig_7969052 shindig_796905 
```

> svn will check out working copy to shindig\_796905 folder/path.

  * In shell/console, go to the working copy of shindig where you can find pom.xml. Maven will use this pom.xml build the project. If you check out everything, please check the pom.xml contains these messages:
```
  <groupId>org.apache.shindig</groupId>
  <artifactId>shindig-project</artifactId>
  <version>1.1-SNAPSHOT</version>
  <packaging>pom</packaging>
```

  * Build and Install Shinding to Maven Local Reposiotry
```
  mvn package install 
```

  * If you get a warning about compiling, try to execute the mvn command again
  * You can find a war file in shindig\_735392/java/server/target, if no any errors

  * Copy the built files into the plug-in project
    * shindig\_735392/java/server/target/shindig-server-1.1-SNAPSHOT.war to jp.eisbahn.eclipse.plugins.osde/shindig
    * shindig\_735392/social-api/target/shindig-social-api-1.1-SNAPSHOT.jar to jp.eisbahn.eclipse.plugins.osde/libs
    * shindig\_735392/hibernate/target/shindig-hibernate-1.1-SNAPSHOT.jar to jp.eisbahn.eclipse.plugins.osde/libs
    * Test and commit them to svn repository.

## Test your OSDE plugin during development ##

  * Please see:
> http://help.eclipse.org/ganymede/index.jsp?topic=/org.eclipse.pde.doc.user/guide/intro/pde_overview.htm

## Code Conventions ##

  * Sun's code conventions: http://java.sun.com/docs/codeconv/html/CodeConvTOC.doc.html

  * Indentation
    * No tabs. Use 4 spaces for indent and 8 spaces for continuation indent.

  * JavaDoc
    * Required for classes and public methods (except simple getters and setters).

  * Code line length should not exceed 100 characters.

  * Production code should have unit tests, if possible.

  * To make following the conventions easier, we provide two Eclipse code style settings files. You can import them as follows:
    1. Download these files: [import order](http://opensocial-development-environment.googlecode.com/svn/dev/osde-eclipse.importorder) and [formatter](http://opensocial-development-environment.googlecode.com/svn/dev/osde-eclipse-formatter.xml)
    1. Open Eclipse Preference Dialog, go to Java > Code Style > Organize Imports <br><img src='http://opensocial-development-environment.googlecode.com/svn/wiki/HowToContributeToOsde.attach/a2.jpg' />
<ol><li>Click on the "Import..." button and choose the import order file you just downloaded.<br>
</li><li>Open Eclipse Preference Dialog, go to Java > Code Style > Formatter <br><img src='http://opensocial-development-environment.googlecode.com/svn/wiki/HowToContributeToOsde.attach/a1.jpg' />
</li><li>Click on the "Import..." button and choose the formatter file you just downloaded.</li></ol></li></ul>

## Code Review/Submit Guidelines ##

Please follow instructions at: http://code.google.com/p/support/wiki/CodeReviews

Here are the detailed steps for contributing your changes/codes:
  1. A developer creates a new branch with svn cp trunk branches/hot-new-stuff.
  1. The developer writes, tests, and commits several changes to that branch.
  1. The developer clicks "Request Code Review" and fills in the username of a teammate, a description of what the reviewer should look for, and the path to the branch.
  1. The reviewer gets a regular issue notification email about the new code review issue.
  1. The reviewer follows a link from the issue to the branch page and sees a summary of all the work that has been done on the branch and can view diffs.
  1. The reviewer enters review comments, publishes them, and continues the discussion with the developer. (**Note that during the review, the reviewer is expected to enter the comments in the branch page, NOT in any individual revision page**)
  1. The developer may make additional changes to the code based on feedback from the reviewer.
  1. When the reviewer is satisfied, he/she sets the review issue status to "Done" and the issue is closed.
  1. The developer would then merge the work done on the branch into /trunk.

Notes:

  * For people who want to contribute code but don't have submit privilege on OSDE's SVN server: please file a new issue of "review request" and attach your code as a SVN patch.
    * Please type in "/branches/patches" as the branch path when creating the new issue.
    * For reviewers: when the review request comes with a SVN patch, you have two ways to review the code:
      * Simply reply with comments in plain text.
      * Utilize the review feature provided by google code. In this case, the reviewer needs to commit the patch to a folder under /branches/contributor/feature/yyyymmddx/. Once the review is done, then the reviewer needs to merge the code into /trunk.

  * Your code for review should be placed under: /branches/your\_name/your\_feature/yyyymmddx/, where x might be your version number

## Understand the infrastructure of OSDE ##

  * **what each folder is used for? (To Be Done)**

  * **design doc for OSDE (To Be Done)**


---


# For general contribution #

  * File an issue: It could be an issue for bug reporting, new feature request, a code patch, or whatever.

  * Update Wiki pages: Please request for access rights if needed.

  * Cash donation is not accepted at this time.