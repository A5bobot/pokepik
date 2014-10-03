package test.java.org.bobot.pokepik;

import static org.vertx.testtools.VertxAssert.assertEquals;
import static org.vertx.testtools.VertxAssert.testComplete;

import java.util.Properties;
import java.util.concurrent.atomic.AtomicInteger;

import main.java.org.bobot.pokepik.Utils;

import org.junit.Test;
import org.vertx.java.core.AsyncResult;
import org.vertx.java.core.AsyncResultHandler;
import org.vertx.java.core.Handler;
import org.vertx.java.core.eventbus.EventBus;
import org.vertx.java.core.eventbus.Message;
import org.vertx.java.core.json.JsonArray;
import org.vertx.java.core.json.JsonObject;
import org.vertx.testtools.TestVerticle;

public class PersistorTest extends TestVerticle {
	
	private EventBus eb;
	
	@Override
	  public void start() {
		
	    eb = vertx.eventBus();	    
	    JsonObject config = new JsonObject();
	    
	    Properties propsMongo = Utils.getProperties("mongo");

	    config.putString("address", "test.persistor");
	    config.putString("db_name", propsMongo.getProperty("vertx.mongo.database"));
	    config.putString("host", propsMongo.getProperty("vertx.mongo.host"));
	    config.putNumber("port", Integer.valueOf(propsMongo.getProperty("vertx.mongo.port")));
	    
	    String username = propsMongo.getProperty("vertx.mongo.username");
	    String password = propsMongo.getProperty("vertx.mongo.password");
	    
	    if (null != username) {
	      config.putString("username", username);
	      config.putString("password", password);
	    }
	    
	    config.putBoolean("fake", false);
	    
	    container.deployModule("io.vertx~mod-mongo-persistor~2.1.1", config, 1, new AsyncResultHandler<String>() {
	    	
	      public void handle(AsyncResult<String> ar) {
	    	  
	        if (ar.succeeded()) {
	          PersistorTest.super.start();
	        } else {
	          ar.cause().printStackTrace();
	        }
	      }
	    });
	  }
	
	@Test
	public void testPersistor() throws Exception {

	    //First delete everything
	    JsonObject json = new JsonObject()
	    .putString("collection", "testcoll")
	    .putString("action", "delete")
	    .putObject("matcher", new JsonObject());

	    eb.send("test.persistor", json, new Handler<Message<JsonObject>>() {
	    	
	      public void handle(Message<JsonObject> reply) {
	    	  
	        assertEquals("ok", reply.body().getString("status"));
	        
	        final int numDocs = 1;
	        final AtomicInteger count = new AtomicInteger(0);
	        
	        for (int i = 0; i < numDocs; i++) {
	          
	        	JsonObject doc = new JsonObject()
	        	.putString("name", "joe bloggs")
	        	.putNumber("age", 40)
	        	.putString("cat-name", "watt");
	        	
	          JsonObject json = new JsonObject()
	          .putString("collection", "testcoll")
	          .putString("action", "save")
	          .putObject("document", doc);
	          
	          eb.send("test.persistor", json, new Handler<Message<JsonObject>>() {
	        	  
	            public void handle(Message<JsonObject> reply) {
	            	
	              assertEquals("ok", reply.body().getString("status"));
	              
	              if (count.incrementAndGet() == numDocs) {
	            	  
	                JsonObject matcher = new JsonObject()
	                .putString("name", "joe bloggs");

	                JsonObject json = new JsonObject()
	                .putString("collection", "testcoll")
	                .putString("action", "find")
	                .putObject("matcher", matcher);

	                eb.send("test.persistor", json, new Handler<Message<JsonObject>>() {
	                	
	                  public void handle(Message<JsonObject> reply) {
	                	  
	                    assertEquals("ok", reply.body().getString("status"));	                    
	                    JsonArray results = reply.body().getArray("results");
	                    
	                    assertEquals(numDocs, results.size());
	                    System.out.println("results.size() = " + results.size());
	                    
	                    testComplete();
	                  }
	                });
	              }
	            }
	          });
	        }


	      }
	    });
	  }
	
	@Test
	  public void testCommand() throws Exception {
	    
		JsonObject ping = new JsonObject()
	    .putString("action", "command")
	    .putString("command", "{ping:1}");
	    
	    eb.send("test.persistor", ping, new Handler<Message<JsonObject>>() {
	    	
	      public void handle(Message<JsonObject> reply) {
	    	  
	          Number ok = reply.body()
	        		  .getObject("result")
	        		  .getNumber("ok");

	          assertEquals(1.0, ok);
	          testComplete();
	      }
	    });
	  }
	
}
