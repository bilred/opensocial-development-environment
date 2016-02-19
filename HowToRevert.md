# Introduction #

This page is for teaching developers who want to undo a committed revision (change). **Revision** and **change** are used interchangeably in this document. We are using [Subclipse](http://subclipse.tigris.org/), [Subversive](http://www.eclipse.org/subversive/) and svn commands as examples in this tutorial.

# Caveat #

Undoing an earlier committed change (revision) is a non-trivial task and may break the codes/functions in the trunk. Please be very careful when you decide to do it and read this document thoroughly.

# Details #

## SVN ##

This section will teach you how to undo a commit in the repository. Let's describe the problem first. You have done a commit to svn. As time went by, you think that change is never should have been committed. Is any way to roll back a change that has already been committed? Undo the specific revision and keep other descendent existing.

We can use svn merge，assume the revision number will be [revision 538](https://code.google.com/p/opensocial-development-environment/source/detail?r=538). You can check files in the log information that are need to roll back.
```
qrtt1@debian:~/test/trunk$ svn log -vr 538
------------------------------------------------------------------------
r538 | sccheng | 2009-08-24 11:28:43 +0800 (Mon, 24 Aug 2009) | 1 line
Changed paths:
   M /trunk/jp.eisbahn.eclipse.plugins.osde
   M /trunk/jp.eisbahn.eclipse.plugins.osde/plugin.xml
   M /trunk/jp.eisbahn.eclipse.plugins.osde/src/jp/eisbahn/eclipse/plugins/osde/internal/Activator.java
   D /trunk/jp.eisbahn.eclipse.plugins.osde/src/jp/eisbahn/eclipse/plugins/osde/internal/OsdeConfig.java
   D /trunk/jp.eisbahn.eclipse.plugins.osde/src/jp/eisbahn/eclipse/plugins/osde/internal/OsdePreferenceInitializer.java
   A /trunk/jp.eisbahn.eclipse.plugins.osde/src/jp/eisbahn/eclipse/plugins/osde/internal/preferences (from /branches/sccheng/plugins/20090821/src/jp/eisbahn/eclipse/plugins/osde/internal/preferences:537)
   R /trunk/jp.eisbahn.eclipse.plugins.osde/src/jp/eisbahn/eclipse/plugins/osde/internal/preferences/OsdePreferencePage.java (from /branches/sccheng/plugins/20090821/src/jp/eisbahn/eclipse/plugins/osde/internal/preferences/OsdePreferencePage.java:537)
   R /trunk/jp.eisbahn.eclipse.plugins.osde/src/jp/eisbahn/eclipse/plugins/osde/internal/preferences/PreferenceConstants.java (from /branches/sccheng/plugins/20090821/src/jp/eisbahn/eclipse/plugins/osde/internal/preferences/PreferenceConstants.java:537)
   R /trunk/jp.eisbahn.eclipse.plugins.osde/src/jp/eisbahn/eclipse/plugins/osde/internal/preferences/PreferenceInitializer.java (from /branches/sccheng/plugins/20090821/src/jp/eisbahn/eclipse/plugins/osde/internal/preferences/PreferenceInitializer.java:537)
   M /trunk/jp.eisbahn.eclipse.plugins.osde/src/jp/eisbahn/eclipse/plugins/osde/internal/runtime/AbstractRunAction.java
   M /trunk/jp.eisbahn.eclipse.plugins.osde/src/jp/eisbahn/eclipse/plugins/osde/internal/runtime/RunApplicationDialog.java
   M /trunk/jp.eisbahn.eclipse.plugins.osde/src/jp/eisbahn/eclipse/plugins/osde/internal/shindig/DatabaseLaunchConfigurationCreator.java
   M /trunk/jp.eisbahn.eclipse.plugins.osde/src/jp/eisbahn/eclipse/plugins/osde/internal/shindig/ShindigLaunchConfigurationCreator.java
   M /trunk/jp.eisbahn.eclipse.plugins.osde/src/jp/eisbahn/eclipse/plugins/osde/internal/shindig/ShindigLauncher.java
   M /trunk/jp.eisbahn.eclipse.plugins.osde/src/jp/eisbahn/eclipse/plugins/osde/internal/ui/OsdePreferencePage.java
   M /trunk/jp.eisbahn.eclipse.plugins.osde/src/jp/eisbahn/eclipse/plugins/osde/internal/ui/views/docs/DocumentView.java
   M /trunk/jp.eisbahn.eclipse.plugins.osde/test/jp/eisbahn/eclipse/plugins/osde/internal/ActivatorTest.java
   D /trunk/jp.eisbahn.eclipse.plugins.osde/test/jp/eisbahn/eclipse/plugins/osde/internal/OsdeConfigTest.java
   D /trunk/jp.eisbahn.eclipse.plugins.osde/test/jp/eisbahn/eclipse/plugins/osde/internal/OsdePreferenceInitializerTest.java

Merge branch /branches/sccheng/plugins/20090821 into trunk with revision 516, revision 517, revision 519, revision 520 - Implement a new preference page for OSDE
------------------------------------------------------------------------
```

Then, using svn info to get repostiory url that will be used with svn merge:
```
qrtt1@debian:~/test/trunk$ svn info
Path: .
URL: http://opensocial-development-environment.googlecode.com/svn/trunk
Repository Root: http://opensocial-development-environment.googlecode.com/svn
Repository UUID: fa0fd100-0148-11de-ada4-5153b8187bf2
Revision: 636
Node Kind: directory
Schedule: normal
Last Changed Author: sccheng@gmail.com
Last Changed Rev: 635
Last Changed Date: 2009-09-18 15:38:11 +0800 (Fri, 18 Sep 2009)
```

Use svn merge to roll back [revision 538](https://code.google.com/p/opensocial-development-environment/source/detail?r=538):
```
qrtt1@debian:~/test/trunk$ svn merge -c -538 http://opensocial-development-environment.googlecode.com/svn/trunk
--- Reverse-merging r538 into 'jp.eisbahn.eclipse.plugins.osde':
A    jp.eisbahn.eclipse.plugins.osde/test/jp/eisbahn/eclipse/plugins/osde/internal/OsdeConfigTest.java
A    jp.eisbahn.eclipse.plugins.osde/test/jp/eisbahn/eclipse/plugins/osde/internal/OsdePreferenceInitializerTest.java
A    jp.eisbahn.eclipse.plugins.osde/src/jp/eisbahn/eclipse/plugins/osde/internal/OsdeConfig.java
Skipped missing target: 'jp.eisbahn.eclipse.plugins.osde/src/jp/eisbahn/eclipse/plugins/osde/internal/preferences'
A    jp.eisbahn.eclipse.plugins.osde/src/jp/eisbahn/eclipse/plugins/osde/internal/OsdePreferenceInitializer.java
Conflict discovered in 'jp.eisbahn.eclipse.plugins.osde/plugin.xml'.
Select: (p) postpone, (df) diff-full, (e) edit,
        (h) help for more options: e
Select: (p) postpone, (df) diff-full, (e) edit, (r) resolved,
        (h) help for more options: r
U    jp.eisbahn.eclipse.plugins.osde/plugin.xml
 G   jp.eisbahn.eclipse.plugins.osde
```

Roll back may get conflicts. You must edit and resolve it. Developers should run all test cases after undo changes. If any error raises, fix them and pass all tests is important. Finally, you can commit the changes in your working copy.


## Subclipse ##

Let's assume the HEAD revision of the current trunk is [revision 616](https://code.google.com/p/opensocial-development-environment/source/detail?r=616). And we would like to roll back all changes committed in [revision 538](https://code.google.com/p/opensocial-development-environment/source/detail?r=538). But at the same time we want to keep changes committed after [revision 538](https://code.google.com/p/opensocial-development-environment/source/detail?r=538), namely from [revision 539](https://code.google.com/p/opensocial-development-environment/source/detail?r=539) to [revision 616](https://code.google.com/p/opensocial-development-environment/source/detail?r=616). The steps in subclipse are:

  * Checkout the codes from the trunk as a local working copy and make sure it is in sync with the HEAD revision
  * Use Subclipse to choose which revision you want to discard and confirm it
  * Resolve any file conflicts by editing them after discarding the revision in the previous step (There will definitely be file conflicts if your are reverting a quite)
  * Run all tests and make sure you didn't break anything in your local working program
  * If all looks good, commit your changes to the trunk

In the last step, if you are scared you might be missing something, put your reverted local working copy in a branch and ask for a code review from other developers before committing it to the trunk.

This completes the whole processing of reverting. The following screenshots are from Subclipse to guide you through the steps using Subclipse to find out revision history and revert in detail:

Show all revision histories:<br>
<img src='http://opensocial-development-environment.googlecode.com/files/subclipse_show_history.JPG'>

Find out the revision you want to revert and select it to confirm:<br>
<img src='http://opensocial-development-environment.googlecode.com/files/subclipse_revert_change.JPG' />

<h2>Subversive</h2>

In the subversive do not provide one button solution to undo a commit but using a reversed merge can undo the commit. You can find the merge in the team context-menu:<br>
<br>
<img src='http://opensocial-development-environment.googlecode.com/files/subversive_1.png' />

After merge dialog opened, you need set up:<br>
<ul><li>the project svn url<br>
</li><li>revision (we need to undo rev 538: browse the revision and select rev 538 and the previous revision)<br>
</li><li>check reversed merge</li></ul>

<img src='http://opensocial-development-environment.googlecode.com/files/subversive_2.png' />

Then perform the merge command and subversive will open <b>Team Synchronizing Perspective</b>. In our example, do merge break my working copy:<br>
<br>
<img src='http://opensocial-development-environment.googlecode.com/files/subversive_4.png' />

If your working copy is broken, edit the conflicts and pass tests before commiting changes.