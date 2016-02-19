# Introduction #

It means that a gadget-developer can easily deploy (upload) his/her
open-social gadget to a production container (say, iGoogle) from within OSDE, and then he/she can preview the gadget with live interaction with a production container.

# Feature Description #

  * task priority: p0=highest, p4=lowest

  * please see below for detailed steps

  1. Developer (right-)clicks on anyone of the following
    * (done) gadget.xml
    * (done) "OSDE" tab menu
  1. OSDE displays a dropdown list of functions.
  1. Developer selects "Run this application on production" and a (dropdown sub-menu) list of container options appear.
    * (done) Support iGoogle container
    * (p4) Prepare to support more containers.
  1. OSDE pops up a dialog asking for:
    * (done) google account and password
    * (done) a checkbox asking whether to use external browser or not
    * (done) preview home view or canvas view
    * (p2) if developer has already input account/password info, OSDE stores the corresponding session tokens and does not ask for account/password again during the current session.
    * (p4) country and language
  1. Devloper clicks on OK button
  1. OSDE interacts with iGoogle server to authenticate the user
  1. OSDE uploads gadget files to iGoogle server
    * (done) support one gadget xml file
    * (done) support multiple files
  1. OSDE opens a browser
  1. OSDE previews the gadget against iGoogle production server.
    * (done) support previewing the whole iframe of the gadget
    * (p4) support interacting with the whole iGoogle container (not just inside the iframe of the gadget) during preview.


# References #

  * [Issue 39](https://code.google.com/p/opensocial-development-environment/issues/detail?id=39) (http://code.google.com/p/opensocial-development-environment/issues/detail?id=39)