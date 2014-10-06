package main.java.org.bobot.pokepik;

import org.vertx.java.core.Handler;
import org.vertx.java.core.buffer.Buffer;
import org.vertx.java.core.http.HttpServer;
import org.vertx.java.core.http.HttpServerRequest;
import org.vertx.java.core.http.RouteMatcher;
import org.vertx.java.core.json.JsonObject;
import org.vertx.java.core.logging.Logger;
import org.vertx.java.platform.Verticle;

public class Server extends Verticle {
	
	public void start() {
		
		HttpServer server = vertx.createHttpServer();		
		final Logger log = container.logger();

		RouteMatcher routeMatcher = new RouteMatcher();
		
		log.info("Server.java - before route matcher");
		
		routeMatcher.get("/", new Handler<HttpServerRequest>() {
		    public void handle(HttpServerRequest req) {
		        req.response().sendFile("web/index.html");
		    }
		});
		
		routeMatcher.get("/header", new Handler<HttpServerRequest>() {
		    public void handle(HttpServerRequest req) {
		
		    	req.bodyHandler(new Handler<Buffer>() {
					
					@Override
					public void handle(Buffer buff) {
						System.out.println(buff.toString());
					}
				});
		    	
		    	System.out.println("req.params().get(\"idsess\") = " + req.params().get("idsess"));
		    	
//		    	JsonObject jObj = new JsonObject(req.params().get("idsess"));
//		    	System.out.println("jObj = " + jObj.toString());
		    	
		    	req.response().sendFile("web/header.html");
		    }
		});
		
		routeMatcher.get("/content", new Handler<HttpServerRequest>() {
		    public void handle(HttpServerRequest req) {
		        req.response().sendFile("web/content.html");
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
		
		log.info("Server.java - after route matcher");
		
		server.requestHandler(routeMatcher).listen(8080, "localhost");

	}

}
