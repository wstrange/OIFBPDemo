package controllers;

import play.*;
import play.mvc.*;

import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.net.URLEncoder;
import java.util.*;

import com.mysql.jdbc.log.Log;
import com.unboundid.ldap.sdk.DecodeableControl;
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
	//public static String BASEDN = "ou=People,dc=example,dc=com";
	public static String BASEDN = "dc=oracleateam,dc=com";
	
	public static String OIF_URL = "https://feddemo.oracleateam.com/fed/user";
	
	private static LDAPConnection ldapConnection = null;
	
	public static LDAPConnection getConnection()  {
		if( ldapConnection == null )
			try {
				//ldapConnection = new LDAPConnection("localhost", 1389,"cn=Directory Manager", "password");
				ldapConnection = new LDAPConnection("localhost", 3131, "cn=orcladmin", "password");
			}
			catch(Exception e) {
				Logger.error("LDAP Problem", e);
			}
		return ldapConnection;
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
     * @param refId  OIF refernence id for the workflow
     */
    public static void completeRegistration(String uid,String missing,String refId) {
    	
    	List l = new ArrayList();
    	l.add("test");
    	renderArgs.put("testList", l);
    	render(uid,missing,refId);
    }
    
    /**
     * Action that does the work (updates ldap) and then redirects back to OIF. This is
     * what gets called <b>after</b> the user hits "SUBMIT" on the completeRegistrationForm
     * 
     * Updates the users cell phone number. You can add any number of attributes to the form and the param list
     * 
     * @param uid  the LDAP uid of the user we are going to modify
     * @param missing - a csv list of the missing ldap attributes we need to prompt for
     * @param redirectURL -  the URL we should redirect to once we have updated the users profile
     */
    public static void updateUserInfo(String uid,String missing,String refId) {
    	Logger.info(" uid=" + uid + " refid=" + refId + " missing=" + missing);
    	
    	Map <String,String>p = request.params.allSimple();
    	
    	Logger.info("Request params=" + p);
    	
    	
    	String dn = "uid=" + uid + "," + BASEDN;
    	
    	LDAPConnection ldap = getConnection();
		Logger.info("Got a LDAP connection");
		
    	
    	try {		 		
    		SearchResultEntry entry = ldap.getEntry(dn);
    		
    		Logger.info("Got user ldap Entry=" + entry);
    		
    		if( entry != null ) {
	    		for( String k: p.keySet()) {
	    			if( k.startsWith("ldap-") )  {
	    				String val = p.get(k);
	    				String ldapAttr = k.substring(5);
	    				Logger.info("Updating ldap " + ldapAttr + " to val=" + val);
	    				Modification mod = new Modification(ModificationType.REPLACE, ldapAttr, val);
	    				ldap.modify(dn,mod);	
	    			}				
	    		}
    		}
    		else {
    			flash.error("Can't find user LDAP entry for uid"+ dn);
    			completeRegistration("NoSuchUser"+uid,missing,refId);
    		}
    		
    	}
    	catch(Exception e) {
    		// shit happens
    		Logger.error(e,"Some LDAP error Happened %s", e.getMessage());	
    		flash.error("LDAP error=" + e.getMessage());
    		completeRegistration("someuser",missing,refId);
    	}
    	
    	
    	String redirectURL = makeRedirectURL(refId);
    	
    
    	
    	Logger.info("Redirecting back to OIF URL=" + redirectURL);
    	
    	
    	// for dev only - show results of LDAP update. 
    	// comment out when OIF is integrated
    	render(uid,redirectURL); 
    	
    	// NOT REACHED
    	// Redirect back to OIF 
    	//redirect(redirectURL);
    }
    
    private static String makeRedirectURL(final String refid) {
    	try {
			String s = URLEncoder.encode(OIF_URL,"UTF-8");
			return s + "?" + refid;
		} catch (UnsupportedEncodingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return (e.getMessage());
		}
    }
    

}