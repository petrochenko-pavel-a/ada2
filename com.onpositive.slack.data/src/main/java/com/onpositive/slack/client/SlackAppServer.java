package com.onpositive.slack.client;

import java.io.IOException;
import java.io.Writer;
import java.util.HashMap;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import org.eclipse.jetty.server.ConnectionFactory;
import org.eclipse.jetty.server.Connector;
import org.eclipse.jetty.server.HttpConnectionFactory;
import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.server.handler.ErrorHandler;
import org.eclipse.jetty.servlet.ServletContextHandler;
import org.eclipse.jetty.servlet.ServletHolder;

import com.github.seratch.jslack.lightning.App;
import com.github.seratch.jslack.lightning.WebEndpoint;
import com.github.seratch.jslack.lightning.handler.WebEndpointHandler;
import com.github.seratch.jslack.lightning.servlet.SlackAppServlet;
import com.github.seratch.jslack.lightning.servlet.SlackOAuthAppServlet;
import com.github.seratch.jslack.lightning.servlet.WebEndpointServlet;

public class SlackAppServer {

    private final Server server;
    private final Map<String, App> pathToApp;
    private final boolean localDebug = System.getenv("SLACK_APP_LOCAL_DEBUG") != null;

    private ErrorHandler errorHandler = new ErrorHandler() {
        @Override
        protected void writeErrorPage(
                HttpServletRequest request,
                Writer writer,
                int code,
                String message,
                boolean showStacks) throws IOException {
            if (localDebug) {
                super.writeErrorPage(request, writer, code, message, showStacks);
            } else {
                writer.write("{\"status\":\"" + code + "\"}");
            }
        }
    };

    public SlackAppServer(App app) {
        this(app, "/slack/events", 3000);
    }

    public SlackAppServer(App app, String path) {
        this(app, path, 3000);
    }

    public SlackAppServer(App app, int port) {
        this(toApps(app, "/slack/events"), port);
    }

    public SlackAppServer(App app, String path, int port) {
        this(toApps(app, path), port);
    }

    public SlackAppServer(Map<String, App> pathToApp) {
        this(pathToApp, 3000);
    }

    public SlackAppServer(Map<String, App> pathToApp, int port) {
        this.pathToApp = pathToApp;
        server = new Server(port);
        removeServerHeader(server);

        ServletContextHandler handler = new ServletContextHandler();
        Map<String, App> addedOnes = new HashMap<>();
        for (Map.Entry<String, App> entry : this.pathToApp.entrySet()) {
            String appPath = entry.getKey();
            App theApp = entry.getValue();
            theApp.config().setAppPath(appPath);
            handler.addServlet(new ServletHolder(new SlackAppServlet(theApp)), appPath);

            if (theApp.config().isOAuthStartEnabled()) {
                if (theApp.config().isDistributedApp()) {
                    // start
                    String oAuthStartPath = appPath + theApp.config().getOauthStartPath();
                    App oAuthStartApp = theApp.toOAuthStartApp();
                    handler.addServlet(new ServletHolder(new SlackOAuthAppServlet(oAuthStartApp)), oAuthStartPath);
                    addedOnes.put(oAuthStartPath, oAuthStartApp);
                } else {
                    //log.warn("The app is not ready for handling your Slack App installation URL. Make sure if you set all the necessary values in AppConfig.");
                }
            }

            if (theApp.config().isOAuthCallbackEnabled()) {
                if (theApp.config().isDistributedApp()) {
                    // callback
                    String oAuthCallbackPath = appPath + theApp.config().getOauthCallbackPath();
                    App oAuthCallbackApp = theApp.toOAuthCallbackApp();
                    handler.addServlet(new ServletHolder(new SlackOAuthAppServlet(oAuthCallbackApp)), oAuthCallbackPath);
                    addedOnes.put(oAuthCallbackPath, oAuthCallbackApp);
                } else {
                    //log.warn("The app is not ready for handling OAuth callback requests. Make sure if you set all the necessary values in AppConfig.");
                }
            }

            // Register additional web endpoints
            if (theApp.getWebEndpointHandlers() != null && theApp.getWebEndpointHandlers().size() > 0) {
                for (Map.Entry<WebEndpoint, WebEndpointHandler> ee : theApp.getWebEndpointHandlers().entrySet()) {
                    WebEndpoint endpoint = ee.getKey();
                    WebEndpointHandler endpointHandler = ee.getValue();
                    ServletHolder servletHolder = new ServletHolder(
                            new WebEndpointServlet(endpoint, endpointHandler, theApp.config()));
                    handler.addServlet(servletHolder, endpoint.getPath());
                }
            }
        }
        handler.addServlet(new ServletHolder(new FilesServlet()), "/images");
        pathToApp.putAll(addedOnes);
        server.setHandler(handler);
        server.setErrorHandler(errorHandler);
    }

    public void start() throws Exception {
        for (App app : pathToApp.values()) {
            app.start();
        }
        server.start();
        server.join();
    }

    public void stop() throws Exception {
        for (App app : pathToApp.values()) {
            app.stop();
        }
        server.stop();
    }

    public ErrorHandler getErrorHandler() {
        return errorHandler;
    }

    public void setErrorHandler(ErrorHandler errorHandler) {
        this.errorHandler = errorHandler;
    }

    // ----------------------------------------------------
    // internal methods
    // ----------------------------------------------------

    private static Map<String, App> toApps(App app, String path) {
        Map<String, App> apps = new HashMap<>();
        apps.put(path, app);
        return apps;
    }

    private static void removeServerHeader(Server server) {
        // https://stackoverflow.com/a/15675075/840108
        for (Connector y : server.getConnectors()) {
            for (ConnectionFactory x : y.getConnectionFactories()) {
                if (x instanceof HttpConnectionFactory) {
                    ((HttpConnectionFactory) x).getHttpConfiguration().setSendServerVersion(false);
                }
            }
        }
    }

}
