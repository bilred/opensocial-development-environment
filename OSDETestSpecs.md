[#OSDE\_0.2.8](#OSDE_0.2.8.md)

# Introduction #

There are three general operation systems to test. <br />
Windows XP, Windows Vista and Linux.<br />
See issue <a href='http://code.google.com/p/opensocial-development-environment/issues/detail?id=49'>49</a>, <a href='http://code.google.com/p/opensocial-development-environment/issues/detail?id=50'>50</a>, <a href='http://code.google.com/p/opensocial-development-environment/issues/detail?id=51'>51</a>.

This page is used to describe what items need to test.<br />
You should define items here before test, <br />
or test by items described here to be familiar with OSDE.

Moreover, you can write anything should test but not described here,<br />
or even any improvement on next generation OSDE if you're responsible for it.

# OSDE 0.2.8 #

[#Windows\_XP](#Windows_XP.md)<br />
[#Windows\_Vista](#Windows_Vista.md)<br />
[#Linux](#Linux.md)

## Windows XP ##

  * Env : Windows XP, Eclipse for RCP/Plug-in Developers Build id: 20090619-0625, JDK1.6

### Common features that will be Implemented and Tested for OSDE 0.2.8 release ###

  * A built in Shindig server for local testing and debugging.
  * An integrated database based on the Java H2 for storing and managing social data.
  * Simple project and gadget code generation wizards to quickly generate your [OpenSocial](http://code.google.com/intl/zh-TW/apis/opensocial/) application projects and application code.
  * A multi-paned gadget spec editor that facilitates productive gadget development.
  * A new "[OpenSocial](http://code.google.com/intl/zh-TW/apis/opensocial/)" Eclipse perspective that provides enhanced editing and debugging with easy access and control of the local Shindig and social database.
  * An [OpenSocial](http://code.google.com/intl/zh-TW/apis/opensocial/) REST Java client wizard that can quickly generate a new project along with sample code that uses the [OpenSocial](http://code.google.com/intl/zh-TW/apis/opensocial/) RESTful Java client library to connect to the local [OpenSocial](http://code.google.com/intl/zh-TW/apis/opensocial/) server.

### Related documentation. ###

  * <a href='http://wiki.opensocial.org/index.php?title=OSDE_Tutorial'>OSDE Tutorial</a>
  * <a href='http://code.google.com/p/opensocial-development-environment/w/list'>OSDE Wiki</a>

### Test Identification (Test Areas) ###

  * Installation
  * Building a Simple OpenSocial App in OSDE
  * Running an OpenSocial app in OSDE
  * Creating a Java RESTful Client Project

### Test Cases Detail/Procedure ###

<table border='1' cellspacing='0'>
<blockquote><thead>
<blockquote><tr>
<blockquote><td>
<blockquote>Installation<br>
</blockquote></td>
</blockquote></tr>
<tr>
<blockquote><td>Test Id</td>
<td>Test Item</td>
<td>Input Spec</td>
<td>Output Spec</td>
</blockquote></tr>
</blockquote></thead>
<tbody>
<blockquote><tr>
<blockquote><td>installation_01</td>
<td>Show OSDE menus</td>
<td>
<ol><li>Download OSDE 0.2.8 plugin library<br>
</li><li>Copy OSDE 0.2.8 library to eclipse plugin folder<br>
</li><li>Restart eclipse<br>
</li></ol></td>
<td>
<ol><li>Show OSDE menu bar and items<br>
</li><li>Show OSDE tree node in Preference<br>
</li><li>Show OSDE tree node in New Project<br>
</li></ol></td>
</blockquote></tr>
</blockquote></tbody>
</table></blockquote>

<table border='1' cellspacing='0'>
<blockquote><thead>
<blockquote><tr>
<blockquote><td>
<blockquote>Building a Simple OpenSocial App in OSDE<br>
</blockquote></td>
</blockquote></tr>
<tr>
<blockquote><td>Test Id</td>
<td>Test Item</td>
<td>Input Spec</td>
<td>Output Spec</td>
</blockquote></tr>
</blockquote></thead>
<tbody>
<blockquote><tr>
<blockquote><td>build_sample_01</td>
<td>Launch Apache Shindig server</td>
<td>clicking the "Launch Apache Shindig server" button on Eclipse's main toolbar</td>
<td>Shindig start up in the console</td>
</blockquote></tr>
<tr>
<blockquote><td>build_sample_02</td>
<td>Launch Apache Shindig server</td>
<td>selecting 'Launch Apache Shindig' from the OSDE menu</td>
<td>Shindig start up in the console</td>
</blockquote></tr>
<tr>
<blockquote><td>build_sample_03</td>
<td>populate the database</td>
<td>selecting 'Create sample data in Shindig' from the OSDE menu</td>
<td>database interface windows in the console add people 'canonical', 'john.doe', 'jane.doe', 'george.doe'</td>
</blockquote></tr>
<tr>
<blockquote><td>build_sample_04</td>
<td>populate the database</td>
<td>
<ol><li>use the database interface windows in the console to create custom data 'shooeugensea'<br>
</li><li>click 'New'<br>
</li><li>input ID: shooeugenesea<br>
</li><li>input Display Name: shooeugenesea<br>
</li></ol></td>
<td>database interface windows in the console add people 'shooeugenesea'</td>
</blockquote></tr>
<tr>
<blockquote><td>build_sample_05</td>
<td>create a new OSDE project</td>
<td>
<ol><li>select 'Create new project...-> OpenSocial Project' from the main menu<br>
</li><li>input project name 'OSDETest' -> 'Next'<br>
</li><li>input title 'OSDETestTitle'<br>
</li><li>input Author Email 'tester@test.com' -> 'Next'<br>
</li><li>select 'Type: HTML'<br>
</li><li>select 'Create the external JavaScript file for this view' -> 'Finish'<br>
</li></ol></td>
<td>
<ol><li>A project named OSDETest<br>
</li><li>a file named '.project'<br>
</li><li>a file named canvas.js<br>
</li><li>a file named gedget.xml<br>
</li><li>the gadget editor appears in the main window of Eclipse<br>
</li></ol></td>
</blockquote></tr>
</blockquote></tbody>
</table></blockquote>

<table border='1' cellspacing='0'>
<blockquote><thead>
<blockquote><tr>
<blockquote><td>
<blockquote>Running an OpenSocial app in OSDE<br>
</blockquote></td>
</blockquote></tr>
<tr>
<blockquote><td>Test Id</td>
<td>Test Item</td>
<td>Input Spec</td>
<td>Output Spec</td>
</blockquote></tr>
</blockquote></thead>
<tbody>
<blockquote><tr>
<blockquote><td>run_app_01</td>
<td>
<blockquote>Run application and show friend names<br>
</blockquote></td>
<td>
<ol><li>open gadget.xml -> Contents view -> Choose canvas.html<br>
</li><li>edit canvas.html value to<br>
<pre><code>&lt;script type="text/javascript" src="http://localhost:8080/OSDETest/canvas.js"&gt;&lt;/script&gt;<br>
<br>
&lt;script type="text/javascript"&gt;<br>
gadgets.util.registerOnLoadHandler(init);<br>
&lt;/script&gt;<br>
<br>
Your friends:&lt;div id='friends'&gt;&lt;/div&gt;                    <br>
</code></pre>
</li><li>edit canvas.js value to<br>
<pre><code>function loadFriends() {<br>
    var req = opensocial.newDataRequest();<br>
    req.add(req.newFetchPersonRequest(opensocial.IdSpec.PersonId.VIEWER), 'viewer');<br>
    var viewerFriends = opensocial.newIdSpec({ "userId" : "VIEWER", "groupId" : "FRIENDS" });<br>
    var opt_params = {};<br>
    opt_params[opensocial.DataRequest.PeopleRequestFields.MAX] = 100;<br>
    req.add(req.newFetchPeopleRequest(viewerFriends, opt_params), 'viewerFriends'); <br>
    req.send(onLoadFriends);<br>
}<br>
 <br>
function onLoadFriends(data) {<br>
    var viewer = data.get('viewer').getData();<br>
    var viewerFriends = data.get('viewerFriends').getData();<br>
    html = new Array();<br>
    html.push('&lt;ul&gt;');<br>
    viewerFriends.each(function(person) {<br>
        html.push('&lt;li&gt;', person.getId(), '&lt;/li&gt;');<br>
    });<br>
    html.push('&lt;/ul&gt;');<br>
    document.getElementById('friends').innerHTML = html.join('');<br>
    gadgets.window.adjustHeight();<br>
} <br>
 <br>
function init() {<br>
    loadFriends();<br>
} <br>
</code></pre>
</li><li>right-clicking the gadget file (gadget.xml) and selecting OSDE->'Run this application'<br>
</li><li>select View: Canvas<br>
</li><li>select width: 100<br>
</li><li>select Owner: john.doe<br>
</li><li>select Viewer: george.doe<br>
</li><li>select TAIWAN<br>
</li><li>select Language: Chinese (zh)<br>
</li><li>click OK<br>
</li></ol></td>
<td>
<blockquote>Browser shows:<br>
<pre><code>Your friends:<br>
george.doe<br>
jane.doe<br>
john.doe<br>
</code></pre>
</blockquote></td>
</blockquote></tr>
</blockquote></tbody>
</table>
<table border='1' cellspacing='0'>
<thead>
<blockquote><tr>
<blockquote><td>
<blockquote>Creating a Java RESTful Client Project<br>
</blockquote></td>
</blockquote></tr>
<tr>
<blockquote><td>Test Id</td>
<td>Test Item</td>
<td>Input Spec</td>
<td>Output Spec</td>
<td>Note</td>
</blockquote></tr>
</blockquote></thead>
<tbody>
<blockquote><tr>
<blockquote><td>create_restful_01</td>
<td>create a new Java client application that uses the Java client libraries</td>
<td>
<ol><li>right-click the gadget spec (gadget.xml) and select OSDE->Create Java Project for Restful Protocol<br>
</li><li>enter project name 'OSDERestfulTest'<br>
</li></ol></td>
<td>
<ol><li>show new created project named 'OSDERestfuTest' with OpenSocial Java Client Library and a Sample.java<br>
</li></ol></td>
<td></td>
</blockquote></tr>
<tr>
<blockquote><td>create_restful_02</td>
<td>Show friends by modifying the variable 'VIEWER_ID'</td>
<td>
<ol><li>change the variable 'VIEWER_ID' value to 'canonical'<br>
</li><li>run the application<br>
</li></ol></td>
<td>
<ol><li>show new created project named 'OSDERestfuTest' with OpenSocial Java Client Library and a Sample.java<br>
</li><li>Console shows<br>
<pre><code>Viewer: canonical<br>
Friend: george.doe<br>
Friend: jane.doe<br>
Friend: john.doe                    <br>
</code></pre>
</li></ol></td>
<td>
<blockquote>Shindig server should remain running for the client application to work<br>
</blockquote></td>
</blockquote></tr>
</blockquote></tbody>
</table></blockquote>


## Windows Vista ##
_TODO: test spec in windows vista_

## Linux ##
_TODO: test spec in linux_