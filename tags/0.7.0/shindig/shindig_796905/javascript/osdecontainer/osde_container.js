var shindig = shindig || {};
shindig.osde = {};

(function() {
	
	var parentUrl = document.location.href;
	var baseUrl = parentUrl.substring(0, parentUrl.indexOf("index.html"));
	
	var gadgetUrlMatches = /[?&]url=((?:[^#&]+|&amp;)+)/.exec(parentUrl);
	var gadgetUrl = gadgetUrlMatches[1];
	
	var appIdMatches = /[?&]appId=((?:[^#&]+|&amp;)+)/.exec(parentUrl);
	var appId = appIdMatches[1];
	
	var gadget;
	
	var viewerIdMatches = /[?&]viewerId=((?:[^#&]+|&amp;)+)/.exec(parentUrl);
	var viewerId = (viewerIdMatches) ? viewerIdMatches[1] : "john.doe";
	var ownerIdMatches = /[?&]ownerId=((?:[^#&]+|&amp;)+)/.exec(parentUrl);
	var ownerId = (ownerIdMatches) ? ownerIdMatches[1] : "john.doe";
	
	var viewMatches = /[?&]view=((?:[^#&]+|&amp;)+)/.exec(parentUrl);
	var current_view = (viewMatches) ? viewMatches[1] : "canvas";

	var widthMatches = /[?&]width=((?:[^#&]+|&amp;)+)/.exec(parentUrl);
	var width = (widthMatches) ? widthMatches[1] + "%" : "100%";

	var countryMatches = /[?&]country=((?:[^#&]+|&amp;)+)/.exec(parentUrl);
	var country = (countryMatches) ? countryMatches[1] : "default";

	var languageMatches = /[?&]language=((?:[^#&]+|&amp;)+)/.exec(parentUrl);
	var language = (languageMatches) ? languageMatches[1] : "default";

	var viewParamsMatches = /[?&]appParams=((?:[^#&]+|&amp;)+)/.exec(parentUrl);
	var viewParams = (viewParamsMatches) ? gadgets.json.parse(decodeURIComponent(viewParamsMatches[1])) : "";

	var userPrefsMatches = /[?&]userPrefs=((?:[^#&]+|&amp;)+)/.exec(parentUrl);
	var userPrefs = (userPrefsMatches) ? gadgets.json.parse(decodeURIComponent(userPrefsMatches[1])) : {};

	var useStMatches = /[?&]use_st=((?:[^#&]+|&amp;)+)/.exec(parentUrl);
	var useSt = (useStMatches) ? gadgets.json.parse(decodeURIComponent(useStMatches[1])) : {};

	function generateSecureToken() {
		var fields = [ownerId, viewerId, appId, "osde", gadgetUrl, "0", "default"];
		for (var i = 0; i < fields.length; i++) {
			fields[i] = escape(fields[i]);
		}
		return fields.join(":");
	}
	
	OsdeContainerGadget = function(opt_params) {
		gadgets.IfrGadget.call(this, opt_params);
	};
	
	OsdeContainerGadget.inherits(gadgets.IfrGadget);
	
	gadgets.container.gadgetClass = OsdeContainerGadget;
	
	function sendRequestToServer(url, method, opt_postParams, opt_callback, opt_excludeSecurityToken) {
		opt_postParams = opt_postParams || {};
		var makeRequestParams = {
				"CONTENT_TYPE" : "JSON",
				"METHOD" : method,
				"POST_DATA" : opt_postParams
		};
		if (!opt_excludeSecurityToken) {
			url = url + "?st=" + gadget.secureToken;
		}
		gadgets.io.makeNonProxiedRequest(url, function(data) {
			data = data.data;
			if (opt_callback) {
				opt_callback(data);
			}
		}, makeRequestParams, "text/json");
	}
	
	function requestGadgetMetaData(opt_callback) {
		var request = {
				context: {
					country : country,
					language : language,
					view : current_view,
					container : "default"
				},
				gadgets: [{
					url : gadgetUrl,
					moduleId : 1
				}]
		};
		sendRequestToServer("/gadgets/metadata", "POST",
				gadgets.json.stringify(request), opt_callback, true);
	}
	
	function generateGadgets(metadata) {
		gadgets.container.view_ = current_view;
		gadgets.container.country_ = country;
		gadgets.container.language_ = language;
		gadgets.container.gadgets_ = {};
		for (var i = 0; i < metadata.gadgets.length; i++) {
			var up = {};
			for (var upName in metadata.gadgets[i].userPrefs) {
				up[upName] = userPrefs[upName];
			}
			gadget = gadgets.container.createGadget({
				"specUrl" : metadata.gadgets[i].url,
				"title" : metadata.gadgets[i].title,
				"viewParams" : viewParams});
			gadget.setUserPrefs(up);
			gadget.setServerBase("../../");
			if (useSt != "0") {
				gadget.secureToken = escape(generateSecureToken());
			}
			gadgets.container.addGadget(gadget);
		}
		gadgets.container.layoutManager.setGadgetChromeIds(["gadget-chrome"]);
		gadgets.container.renderGadgets();
	}
	
	shindig.osde.initGadget = function() {
		gadgets.container.nocache_ = 1;
		var gadgetChrome = document.getElementById("gadget-chrome");
		gadgetChrome.style.width = width;
		requestGadgetMetaData(generateGadgets);
	}
	
})();