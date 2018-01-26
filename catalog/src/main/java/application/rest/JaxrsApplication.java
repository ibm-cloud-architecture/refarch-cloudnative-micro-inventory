package application.rest;

import javax.ws.rs.ApplicationPath;
import javax.ws.rs.core.Application;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@ApplicationPath("/micro")
public class JaxrsApplication extends Application {
	private static final Logger logger = LoggerFactory.getLogger(ElasticSearch.class);
	

}
