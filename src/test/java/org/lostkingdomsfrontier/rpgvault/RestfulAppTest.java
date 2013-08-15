package org.lostkingdomsfrontier.rpgvault;


import org.glassfish.grizzly.http.server.HttpServer;
import org.glassfish.jersey.grizzly2.httpserver.GrizzlyHttpServerFactory;
import org.glassfish.jersey.jackson.JacksonFeature;
import org.glassfish.jersey.server.ResourceConfig;
import org.lostkingdomsfrontier.rpgvault.restful.ConfigDao;

import java.io.IOException;
import java.net.URI;
import java.util.logging.Logger;

/**
 * @author John McCormick
 *         Date: 8/10/13
 */
public class RestfulAppTest {
    private final static Logger LOG = Logger.getLogger(RestfulAppTest.class.getName());

    protected static HttpServer startServer(URI baseURI) throws IOException {
        LOG.info("SpeechPerformanceManager Application READY for external connections against: " + baseURI);
        ResourceConfig resourceConfig = new ResourceConfig().packages("org.lostkingdomsfrontier.rpgvault.restful.resources")
                .register(JacksonFeature.class);
        return GrizzlyHttpServerFactory.createHttpServer(baseURI, resourceConfig);
    }

    public static void main(String[] args) throws Exception {
        ConfigDao.CONFIG.changeConfig("./conf/restfulAppTest.config.xml");
        String baseURI = ConfigDao.CONFIG.getConfig().getString("baseURI", "http://localhost:9000/rpgVault/");
        HttpServer server = startServer(URI.create(baseURI));
        LOG.info("rpgVault RestfulAppTest READY for external connections against: [" + baseURI + "]");
        System.in.read();
        LOG.info("++ Stopping grizzly ...");
        server.stop();
    }

}
