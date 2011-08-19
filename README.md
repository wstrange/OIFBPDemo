<h1>Oracle Identity Federation - OIF Workflow callout example</h1>

A simple Play! Application that demonstrates how Oracle Identity Federation (OIF) 
can call out to a custom application during a federation workflow.

The OIF workflow callout provides a means to interact with the user during SSO. For example, to
 prompt them to agree to terms and conditions, complete additional required attributes (cell,birthdate, etc.). 

Using workflow callout avoids having to modify OIF itself. The application should interact with user and then pass control back to OIF upon completion.
 
As part of the login process, OIF will call out to this application (via redirect) and pass the relevant attributes
as GET parameters. The sample application updates the mobile attribute and passes control back to OIF via a redirect. 

You want to configure OIF to invoke the /completeRegistration URL with the following parameters:

<ul>
<li>uid - the LDAP uid of the user logging in</li>
<li>cell - the cell phone number of the user to update. For clarity the GET parameter is called "cell" to distinguish it from the ldap attribute called "mobile"
		(helpfull when trying to understand the code..)</li>
<li>redirectURL - The OIF URL to redirect back to once we have updated the users profile</li>
</ul>
For example:    

GET http://localhost:9010/OIFBPDemo/completeRegistration?uid=test1&cell=5551212&redirectURL=http://foo.com/fed/idp?blahblah

TODO: Do we need to URLEncode the URL..?


<h2>Running the example</h2>

git clone git://github.com/wstrange/OIFBPDemo.git

- Edit conf/application.conf to set the port number, etc (default for the demo is 9010)
- Edit Appplication.java to set the LDAP connection parameters (admin dn, password, search dn, etc.)
- Run the app - assumes you have play! installed

play run 

