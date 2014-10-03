package test.java.org.bobot.pokepik;

import static org.vertx.testtools.VertxAssert.assertEquals;
import static org.vertx.testtools.VertxAssert.testComplete;

import java.util.Properties;

import main.java.org.bobot.pokepik.Utils;

import org.junit.Test;
import org.vertx.java.core.AsyncResult;
import org.vertx.java.core.AsyncResultHandler;
import org.vertx.java.core.Handler;
import org.vertx.java.core.eventbus.EventBus;
import org.vertx.java.core.eventbus.Message;
import org.vertx.java.core.json.JsonObject;
import org.vertx.testtools.TestVerticle;


public class AuthManagerTest extends TestVerticle {

	private EventBus eb;
	private Properties propsAuth;
	private Properties propsMongo;
	
	@Override
	  public void start() {
		
		eb = vertx.eventBus();	
		
		JsonObject configMongo = new JsonObject();
		propsMongo = Utils.getProperties("mongo");
		configMongo.putString("address", propsMongo.getProperty("vertx.mongo.address"));
		configMongo.putString("db_name", propsMongo.getProperty("vertx.mongo.database"));
		configMongo.putString("host", propsMongo.getProperty("vertx.mongo.host"));
		configMongo.putNumber("port", Integer.valueOf(propsMongo.getProperty("vertx.mongo.port")));
		
		container.deployModule("io.vertx~mod-mongo-persistor~2.1.1", configMongo, 1, new AsyncResultHandler<String>() {
	    	
		      public void handle(AsyncResult<String> ar) {
		    	  
		        if (ar.succeeded()) {
		        	
		        	System.out.println("Deploiement de io.vertx~mod-mongo-persistor~2.1.1 : OK");
		        	
		    		JsonObject configAuth = new JsonObject();
		    	    propsAuth = Utils.getProperties("auth");		
		    	    configAuth.putString("address", propsAuth.getProperty("vertx.auth.address"));
		    	    configAuth.putString("user_collection", propsAuth.getProperty("vertx.auth.coll"));
		    	    configAuth.putString("persistor_address", propsAuth.getProperty("vertx.auth.persistor"));
		    	    configAuth.putNumber("session_timeout", Integer.valueOf(propsAuth.getProperty("vertx.auth.timeout")));
		    	    
		    	    container.deployModule("io.vertx~mod-auth-mgr~2.0.0-final", configAuth, 1 , new AsyncResultHandler<String>() {

		    			@Override
		    			public void handle(AsyncResult<String> arg0) {
		    				
		    				if (arg0.succeeded()) {
		    					System.out.println("Deploiement de io.vertx~mod-auth-mgr~2.0.0-final : OK");
		    					AuthManagerTest.super.start();
		    				} else {
		    					arg0.cause().printStackTrace();
		    				}
		    			}	    
		    	    });
		        	
		        } else {
		          ar.cause().printStackTrace();
		        }
		      }
		    });
		}
	
	@Test
	public void testConfigAuth() {
		
		String username="olivier";
		String password="testpwdolivier";
		
		// Insert user test
		final JsonObject docUserTest = new JsonObject()
    	.putString("username", username)
    	.putString("password", password);
    	
		JsonObject jsonInsertUserTest = new JsonObject()
		.putString("collection", propsAuth.getProperty("vertx.auth.coll"))
		.putString("action", "save")
		.putObject("document", docUserTest);
		
		eb.send(propsMongo.getProperty("vertx.mongo.address"), jsonInsertUserTest);
		
		// Check insert
		JsonObject jsonFindUserTest = new JsonObject()
		.putString("collection", propsAuth.getProperty("vertx.auth.coll"))
		.putString("action", "findone")
		.putObject("matcher", docUserTest);
		
		eb.send(propsMongo.getProperty("vertx.mongo.address"), jsonFindUserTest, new Handler<Message<JsonObject>>() {

			@Override
			public void handle(Message<JsonObject> reply) {
				System.out.println("Inserted test user : " + reply.body().toString());
			}
		});
		
		// Resultat dans mongodb : 
		// { "_id" : "8910072a-2d00-4afb-81bb-203a6672e1de", "username" : "olivier", "password" : "testpwdolivier" }

	    
		eb.send(propsAuth.getProperty("vertx.auth.address") + ".login",docUserTest, new Handler<Message<JsonObject>>() {

			@Override
			public void handle(Message<JsonObject> reply) {

				System.out.println("reply : " + reply.body().toString());
				System.out.println("sessionID = " + reply.body().getString("sessionID"));
				
				assertEquals("ok", reply.body().getString("status"));
				
				// Delete user test				
				JsonObject jsonDeleteUserTest = new JsonObject()
			    .putString("collection", propsAuth.getProperty("vertx.auth.coll"))
			    .putString("action", "delete")
			    .putObject("matcher", docUserTest);
				
				eb.send(propsMongo.getProperty("vertx.mongo.address"), jsonDeleteUserTest);
				
				testComplete();
				
			}
	    	
	    });
	}

}
