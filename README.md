<h1>Oracle Identity Federation - OIF Workflow callout example</h1>

A simple Play! Application that demonstrates how Oracle Identity Federation (OIF) 
can call out to a custom application during a federation workflow.

The OIF workflow callout provides a means to interact with the user during SSO. For example, to
 prompt them to agree to terms and conditions, complete additional required attributes (cell,birthdate, etc.). 

Using workflow callout avoids having to modify OIF itself. The application should interact with user and then pass control back to OIF upon completion.
 
As part of the login workflow, OIF will call out to this application (via redirect) and pass the missing attributes
as GET parameters. The sample application prompt the user for the missing attributes and d passes control back to OIF via a redirect. 

You want to configure OIF to invoke the /completeRegistration URL with the following parameters:

<ul>
<li>uid - the LDAP uid of the user logging in</li>
<li>missing=att1,attr2,..  a csv list of missing ldap attributes</li>
<li>refid - The OIF refid that correlates this to the current user workflow.</li>
</ul>
For example:    

GET http://localhost:9010/OIFBPDemo/completeRegistration?uid=test1&missing=mobile,sn&refid=2323453245hhx 



<h2>Running the example</h2>

git clone git://github.com/wstrange/OIFBPDemo.git

- Edit conf/application.conf to set the port number, etc (default for the demo is 9010)
- Edit Appplication.java to set the LDAP connection parameters (admin dn, password, search dn, etc.)
- Run the app - assumes you have play! installed

play run 

