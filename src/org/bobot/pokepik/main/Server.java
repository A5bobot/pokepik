package org.bobot.pokepik.main;

import org.vertx.java.core.Handler;
import org.vertx.java.core.http.HttpServer;
import org.vertx.java.core.http.HttpServerRequest;
import org.vertx.java.core.http.RouteMatcher;
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
		
//		server.requestHandler(new Handler<HttpServerRequest>() {
//		    public void handle(HttpServerRequest req) {
//		    	String file = "";
//		        if (req.path().equals("/")) {
//		          file = "index.html";
//		        } else if (req.path().equals("/login")) {
//		        	log.info("Server.java : /login path received");
//		        	file = "index.html";
//		        } else if (!req.path().contains("..")) {
//		          file = req.path();
//		        }
//		        req.response().sendFile("web/" + file);
//		    }
//		});
//
//		server.listen(8080, "localhost");

	}

}
