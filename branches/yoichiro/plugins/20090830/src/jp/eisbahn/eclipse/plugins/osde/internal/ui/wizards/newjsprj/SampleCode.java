/*
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements. See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership. The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License. You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied. See the License for the
 * specific language governing permissions and limitations under the License.
 */
package jp.eisbahn.eclipse.plugins.osde.internal.ui.wizards.newjsprj;

/**
 * This class has sample code list.
 * 
 * @author Yoichiro Tanaka
 */
public class SampleCode {

    /** Fetch a person and friends code for OpenSocial 0.9 */
    public static final String FETCH_PEOPLE_09 =
              "<!-- Fetching People and Friends -->\n"
            + "<div>\n"
            + "  <button onclick='fetchPeople();'>Fetch people and friends</button>\n"
            + "  <div>\n"
            + "    <span id='viewer'></span>\n"
            + "    <ul id='friends'></ul>\n"
            + "  </div>\n"
            + "</div>\n"
            + "<script type='text/javascript'>\n"
            + "function fetchPeople() {\n"
            + "  var batch = osapi.newBatch().\n"
            + "      add('viewer', osapi.people.getViewer()).\n"
            + "      add('friends', osapi.people.get({userId: '@viewer', groupId: '@friends'}));\n"
            + "  batch.execute(function(result) {\n"
            + "    document.getElementById('viewer').innerHTML = result.viewer.id;\n"
            + "    var friends = result.friends.list;\n"
            + "    for (var i = 0; i < friends.length; i++) {\n"
            + "      document.getElementById('friends').innerHTML += '<li>' + friends[i].id + '</li>';\n"
            + "    }\n"
            + "  });\n"
            + "}\n"
            + "</script>\n"
            + "\n";

    /** Fetch a person and friends code for OpenSocial 0.8 */
    public static final String FETCH_PEOPLE_08 =
              "<!-- Fetching People and Friends -->\n"
            + "<div>\n"
            + "  <button onclick='fetchPeople();'>Fetch people and friends</button>\n"
            + "  <div>\n"
            + "    <span id='viewer'></span>\n"
            + "    <ul id='friends'></ul>\n"
            + "  </div>\n"
            + "</div>\n"
            + "<script type='text/javascript'>\n"
            + "function fetchPeople() {\n"
            + "  var req = opensocial.newDataRequest();\n"
            + "  req.add(req.newFetchPersonRequest(opensocial.IdSpec.PersonId.VIEWER), 'viewer');\n"
            + "  var params = {};\n"
            + "  params[opensocial.IdSpec.Field.USER_ID] = opensocial.IdSpec.PersonId.VIEWER;\n"
            + "  params[opensocial.IdSpec.Field.GROUP_ID] = 'FRIENDS';\n"
            + "  var idSpec = opensocial.newIdSpec(params);\n"
            + "  req.add(req.newFetchPeopleRequest(idSpec), 'friends');\n"
            + "  req.send(function(data) {\n"
            + "    var viewer = data.get('viewer').getData();\n"
            + "    document.getElementById('viewer').innerHTML = viewer.getId();\n"
            + "    var friends = data.get('friends').getData();\n"
            + "    document.getElementById('friends').innerHTML = '';\n"
            + "    friends.each(function(friend) {\n"
            + "      document.getElementById('friends').innerHTML += '<li>' + friend.getId() + '</li>';\n"
            + "    });\n"
            + "    gadgets.window.adjustHeight();\n"
            + "  });\n"
            + "}\n"
            + "</script>\n"
            + "\n";

    /** Create activity code*/
    public static final String CREATE_ACTIVITY =
              "<!-- Posting activity -->\n"
            + "<div>\n"
            + "  <input type='text' id='title' />\n"
            + "  <button onclick='postActivity();'>Post activity</button>\n"
            + "  <div id='result_activity'></div>\n"
            + "</div>\n"
            + "<script type='text/javascript'>\n"
            + "function postActivity() {\n"
            + "  var params = {};\n"
            + "  params[opensocial.Activity.Field.TITLE] = document.getElementById('title').value;\n"
            + "  var activity = opensocial.newActivity(params);\n"
            + "  opensocial.requestCreateActivity(\n"
            + "      activity, opensocial.CreateActivityPriority.HIGH, function(response) {\n"
            + "        if (response.hadError()) {\n"
            + "          document.getElementById('result_activity').innerHTML = response.getErrorMessage();\n"
            + "        } else {\n"
            + "          document.getElementById('result_activity').innerHTML = 'Succeeded!';\n"
            + "        }\n"
            + "        gadgets.window.adjustHeight();\n"
            + "      });\n"
            + "}\n"
            + "</script>\n"
            + "\n";
    
    /** Sharing appdata with friends for OpenSocial 0.8 */
    public static final String SHARE_APPDATA_08 =
              "<!-- Sharing data with friends -->\n"
            + "<div>\n"
            +"  <input type='text' id='content' />\n"
            +"  <button onclick='shareData();'>Share data</button>\n"
            +"  <button onclick='fetchFriendData();'>Fetch friend's data</button>\n"
            +"  <div id='result_appdata'></div>\n"
            +"  <ul id='contents'></ul>\n"
            +"</div>\n"
            +"<script type='text/javascript'>\n"
            +"function shareData() {\n"
            +"  var content = document.getElementById('content').value;\n"
            +"  var req = opensocial.newDataRequest();\n"
            +"  req.add(req.newUpdatePersonAppDataRequest(opensocial.IdSpec.PersonId.VIEWER, 'content', content));\n"
            +"  req.send(function(response) {\n"
            +"    if (response.hadError()) {\n"
            +"      document.getElementById('result_appdata').innerHTML = response.getErrorMessage();\n"
            +"    } else {\n"
            +"      document.getElementById('result_appdata').innerHTML = 'Succeeded!';\n"
            +"    }\n"
            +"    gadgets.window.adjustHeight();\n"
            +"  });\n"
            +"}\n"
            +"function fetchFriendData() {\n"
            +"  var req = opensocial.newDataRequest();\n"
            +"  var params = {};\n"
            +"  params[opensocial.IdSpec.Field.USER_ID] = opensocial.IdSpec.PersonId.VIEWER;\n"
            +"  params[opensocial.IdSpec.Field.GROUP_ID] = 'FRIENDS';\n"
            +"  var idSpec = opensocial.newIdSpec(params);\n"
            +"  req.add(req.newFetchPersonAppDataRequest(idSpec, ['content']), 'stored');\n"
            +"  req.send(function(data) {\n"
            +"    var stored = data.get('stored').getData();\n"
            +"    for(var id in stored) {\n"
            +"      var obj = stored[id];\n"
            +"      document.getElementById('contents').innerHTML\n"
            +"          += '<li>' + id + ': ' + obj['content'] + '</li>';\n"
            +"    }\n"
            +"    gadgets.window.adjustHeight();\n"
            +"  });\n"
            +"}\n"
            +"</script>\n"
            +"\n";
    
    /** Sharing appdata with friends for OpenSocial 0.9 */
    public static final String SHARE_APPDATA_09 =
              "<!-- Sharing data with friends -->\n"
            + "<div>\n"
            +"  <input type='text' id='content' />\n"
            +"  <button onclick='shareData();'>Share data</button>\n"
            +"  <button onclick='fetchFriendData();'>Fetch friend's data</button>\n"
            +"  <div id='result_appdata'></div>\n"
            +"  <ul id='contents'></ul>\n"
            +"</div>\n"
            +"<script type='text/javascript'>\n"
            +"function shareData() {\n"
            +"  var content = document.getElementById('content').value;\n"
            +"  osapi.appdata.update({userId: '@viewer', data: {pet: 'a crazed monkey'}});"
            +"  var req = opensocial.newDataRequest();\n"
            +"  req.add(req.newUpdatePersonAppDataRequest(opensocial.IdSpec.PersonId.VIEWER, 'content', content));\n"
            +"  req.send(function(response) {\n"
            +"    if (response.hadError()) {\n"
            +"      document.getElementById('result_appdata').innerHTML = response.getErrorMessage();\n"
            +"    } else {\n"
            +"      document.getElementById('result_appdata').innerHTML = 'Succeeded!';\n"
            +"    }\n"
            +"    gadgets.window.adjustHeight();\n"
            +"  });\n"
            +"}\n"
            +"function fetchFriendData() {\n"
            +"  var req = opensocial.newDataRequest();\n"
            +"  var params = {};\n"
            +"  params[opensocial.IdSpec.Field.USER_ID] = opensocial.IdSpec.PersonId.VIEWER;\n"
            +"  params[opensocial.IdSpec.Field.GROUP_ID] = 'FRIENDS';\n"
            +"  var idSpec = opensocial.newIdSpec(params);\n"
            +"  req.add(req.newFetchPersonAppDataRequest(idSpec, ['content']), 'stored');\n"
            +"  req.send(function(data) {\n"
            +"    var stored = data.get('stored').getData();\n"
            +"    for(var id in stored) {\n"
            +"      var obj = stored[id];\n"
            +"      document.getElementById('contents').innerHTML\n"
            +"          += '<li>' + id + ': ' + obj['content'] + '</li>';\n"
            +"    }\n"
            +"    gadgets.window.adjustHeight();\n"
            +"  });\n"
            +"}\n"
            +"</script>\n"
            +"\n";

}
