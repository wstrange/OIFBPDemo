package controllers;

import play.*;
import play.mvc.*;

import java.util.*;

import com.mysql.jdbc.log.Log;
import com.unboundid.ldap.sdk.LDAPConnection;
import com.unboundid.ldap.sdk.LDAPException;
import com.unboundid.ldap.sdk.Modification;
import com.unboundid.ldap.sdk.ModificationType;
import com.unboundid.ldap.sdk.SearchRequest;
import com.unboundid.ldap.sdk.SearchResult;
import com.unboundid.ldap.sdk.SearchResultEntry;
import com.unboundid.ldap.sdk.SearchScope;

import models.*;

public class Application extends Controller {
	public static String BASEDN = "ou=People,dc=example,dc=com";
	
	public static LDAPConnection getConnection() throws LDAPException {
		return new LDAPConnection("localhost", 1389,"cn=Directory Manager", "password");
	}

	// default index page - renders a link to the /completeRegistration URL
    public static void index() {
        render();
    }
    
    /*
     * render the page for the user that prompts them for their cell, or any other info...
     * 
     * This is the main call in point for OIF. These parmeters are passed via GET
     * 
     * @param uid the user id in ldap 
     * @param cell the users cell phone number 
     * @param redirectURL  URL to redirect to after completion
     */
    public static void completeRegistration(String uid,String cell,String redirectURL) {
    	render(uid,cell,redirectURL);
    }
    
    /**
     * Action that does the work (updates ldap) and then redirects back to OIF. This is
     * what gets called <b>after</b> the user hits "SUBMIT" on the completeRegistrationForm
     * 
     * Updates the users cell phone number. You can add any number of attributes to the form and the param list
     * 
     * @param uid  the LDAP uid of the user we are going to modify
     * @param cell - the cell phone of the user to update.
     * @param redirectURL -  the URL we should redirect to once we have updated the users profile
     */
    public static void updateUserInfo(String uid,String cell,String redirectURL) {
    	Logger.info("Cell=" + cell + " uid=" + uid + " redirect=" + redirectURL);
    	

    	String dn = "uid=" + uid + "," + BASEDN;
    	
    	try {
    		LDAPConnection ldap = getConnection();
    		Logger.info("Got a LDAP connection");
    		
    		SearchResultEntry entry = ldap.getEntry(dn);
    		
    		Logger.info("Entry=" + entry);
    	
    		// modify the users cell phone
    		if( entry != null) {
    			Modification mod = new Modification(ModificationType.REPLACE, "mobile", cell);
    			ldap.modify(dn,mod);		
    		}
    		else {
    			flash.error("Can't find LDAP entry for uid"+ dn);
    			completeRegistration("NoSuchUser"+uid,"5551212",redirectURL);
    		}
    		
    	}
    	catch(Exception e) {
    		// shit happens
    		Logger.error(e,"Some LDAP error Happened %s", e.getMessage());	
    		flash.error("LDAP error=" + e.getMessage());
    		completeRegistration("someuser","5551212",redirectURL);
    	}
    	
    
    	
    	Logger.info("Redirecting back to OIF URL=" + redirectURL);
    	
    	
    	// for dev only - show results of LDAP update. 
    	// comment out when OIF is integrated
    	render(uid,cell,redirectURL); 
    	
    	// NOT REACHED
    	// Redirect back to OIF 
    	//redirect(redirectUrl);
    }
    

}