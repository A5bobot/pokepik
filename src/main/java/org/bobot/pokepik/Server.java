package main.java.org.bobot.pokepik;

import java.util.Properties;

import org.vertx.java.core.AsyncResult;
import org.vertx.java.core.AsyncResultHandler;
import org.vertx.java.core.Handler;
import org.vertx.java.core.buffer.Buffer;
import org.vertx.java.core.eventbus.EventBus;
import org.vertx.java.core.eventbus.Message;
import org.vertx.java.core.http.HttpServer;
import org.vertx.java.core.http.HttpServerRequest;
import org.vertx.java.core.http.RouteMatcher;
import org.vertx.java.core.json.JsonObject;
import org.vertx.java.core.logging.Logger;
import org.vertx.java.platform.Verticle;

public class Server extends Verticle {
	
	private EventBus eb;
	private Properties propsAuth;
	private Properties propsMongo;
	
	public void start() {
		
		eb = vertx.eventBus();
		
		// Configuration MongoDB (fichier ressources/mongo.properties via Utils.getProperties)
		JsonObject configMongo = new JsonObject();
		propsMongo = Utils.getProperties("mongo");
		configMongo.putString("address", propsMongo.getProperty("vertx.mongo.address"));
		configMongo.putString("db_name", propsMongo.getProperty("vertx.mongo.database"));
		configMongo.putString("host", propsMongo.getProperty("vertx.mongo.host"));
		configMongo.putNumber("port", Integer.valueOf(propsMongo.getProperty("vertx.mongo.port")));
		
		System.out.println("DEBUG - configMongo : " + configMongo.toString());
		
		// Deploiement du module io.vertx~mod-mongo-persistor~2.1.1

		container.deployModule("io.vertx~mod-mongo-persistor~2.1.1", configMongo, 1, new AsyncResultHandler<String>() {	    	
		      public void handle(AsyncResult<String> ar) {		    	  
		        if (ar.succeeded()) {
		        	
		        	System.out.println("Deploiement de io.vertx~mod-mongo-persistor~2.1.1 : OK");
		
					// Configuration AuthManager (fichier ressources/auth.properties via Utils.getProperties)
					JsonObject configAuth = new JsonObject();
				    propsAuth = Utils.getProperties("auth");		
				    configAuth.putString("address", propsAuth.getProperty("vertx.auth.address"));
				    configAuth.putString("user_collection", propsAuth.getProperty("vertx.auth.coll"));
				    configAuth.putString("persistor_address", propsAuth.getProperty("vertx.auth.persistor"));
				    configAuth.putNumber("session_timeout", Integer.valueOf(propsAuth.getProperty("vertx.auth.timeout")));
					
				    System.out.println("DEBUG - configAuth : " + configAuth.toString());
				    
				    // Deploiement du module io.vertx~mod-auth-mgr~2.0.0-final
				    container.deployModule("io.vertx~mod-auth-mgr~2.0.0-final", configAuth, new AsyncResultHandler<String>() {

						@Override
						public void handle(AsyncResult<String> arg0) {
								if (arg0.succeeded()) {
			    					System.out.println("Deploiement de io.vertx~mod-auth-mgr~2.0.0-final : OK");
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
	    
		HttpServer server = vertx.createHttpServer();		
		
		final Logger log = container.logger();

		RouteMatcher routeMatcher = new RouteMatcher();
		
		routeMatcher.get("/", new Handler<HttpServerRequest>() {
		    public void handle(HttpServerRequest req) {
		        req.response().sendFile("web/index.html");
		    }
		});
		
		routeMatcher.noMatch(new Handler<HttpServerRequest>() {
		    public void handle(HttpServerRequest req) {
		    	if (!req.path().contains("..")) {
		    		req.response().sendFile("web/" + req.uri());
		    	} else {
		    		req.response().sendFile("web/index.html");
		    	}
		    }
		});
		
		routeMatcher.post("/sess", new Handler<HttpServerRequest>() {
			@Override
			public void handle(final HttpServerRequest req) {
				req.bodyHandler(new Handler<Buffer>() {
					@Override
					public void handle(Buffer data) {
						JsonObject jsonBody = new JsonObject(data.toString());
						System.out.println("jsonBody : " + jsonBody);
						eb.send(propsAuth.getProperty("vertx.auth.address") + ".authorise",jsonBody, new Handler<Message<JsonObject>>() {
							@Override
							public void handle(Message<JsonObject> message) {
								String strMessage = message.body().toString();
								// Envoie reponse
								if ("ok".equals(message.body().getString("status"))) {
									req.response().headers().set("username", message.body().getString("username"));
									req.response().sendFile("web/mainboard.html");
								} else {
									req.response().sendFile("web/login.html");
								}
								
								req.response().close();
							}});
					}
					
				});
			}});
		
		routeMatcher.post("/logout", new Handler<HttpServerRequest>() {
			@Override
			public void handle(final HttpServerRequest req) {
				req.bodyHandler(new Handler<Buffer>() {
					@Override
					public void handle(Buffer data) {
						JsonObject jsonBody = new JsonObject(data.toString());
						eb.send(propsAuth.getProperty("vertx.auth.address") + ".logout",jsonBody, new Handler<Message<JsonObject>>() {
							@Override
							public void handle(Message<JsonObject> message) {
								String strMessage = message.body().toString();
								// Envoie reponse
								req.response().sendFile("web/header_auth.html");
								req.response().close();
							}});
					}
					
				});
			}});
		
		routeMatcher.post("/auth", new Handler<HttpServerRequest>() {
		    public void handle(final HttpServerRequest req) {
		    	
		    	req.bodyHandler(new Handler<Buffer>() {
		            public void handle(Buffer data) {
		            	
		            	JsonObject jsonBody = new JsonObject(data.toString());
		            	
		            	//TODO : controles username & password
		            	
		            	eb.send(propsAuth.getProperty("vertx.auth.address") + ".login", jsonBody, new Handler<Message<JsonObject>>() {

							@Override
							public void handle(Message<JsonObject> message) {
								String strMessage = message.body().toString();
								
								// Envoie reponse
								req.response().setChunked(true);
								req.response().end(strMessage);
								req.response().close();
							}} );
//		            			
//		            		eb.send(propsAuth.getProperty("vertx.auth.address") + ".authorise",jsonReqSession, new Handler<Message<JsonObject>>() {
		            }
		          });
		    }
		});
		
		routeMatcher.post("/newUser", new Handler<HttpServerRequest>() {
		    public void handle(final HttpServerRequest req) {
		    	
		    	req.bodyHandler(new Handler<Buffer>() {
		            public void handle(Buffer data) {
		            	
		            	JsonObject jsonBody = new JsonObject(data.toString());
		            	
		            	if ("init".equals(jsonBody.getString("newUserRequestType"))) {
		            		System.out.println("DEBUG - /newUser -> jsonBody = " + jsonBody);
		            		req.response().sendFile("web/newuser.html");
		            	}
		            	req.response().close();
		            }
		          });
		    }
		});


		server.requestHandler(routeMatcher).listen(8080, "localhost");
	}

}
