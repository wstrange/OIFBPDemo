import play.Logger;
import play.Play;
import play.jobs.Job;
import play.jobs.OnApplicationStart;
import play.mvc.Router;


@OnApplicationStart
public class Bootstrap extends Job {

    public void doJob() {
    	// attempt to hack around the whole apache non root context thing
    	// this did not work - preserved for posterity. 
    	// the right answer is to set up apache:
    	/*
    	 * 
  ProxyPreserveHost On 
  ServerName www.fdemo.com 
  ProxyPass /ui http://127.0.0.1:9010/ui 
  ProxyPassReverse /ui http://127.0.0.1:9010/ui 

    	 * 
    	 */
        //Play.ctxPath = "/ui";
        //Router.load(Play.ctxPath);
        Logger.info("Context path is"  + Play.ctxPath);
    }  
}