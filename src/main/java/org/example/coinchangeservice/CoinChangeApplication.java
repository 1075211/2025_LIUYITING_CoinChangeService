package org.example.coinchangeservice;

import io.dropwizard.Application;
import io.dropwizard.jetty.HttpConnectorFactory;
import io.dropwizard.setup.Bootstrap;
import io.dropwizard.setup.Environment;
import org.eclipse.jetty.servlets.CrossOriginFilter;
import org.example.coinchangeservice.health.CoinChangeHealthCheck;
import org.example.coinchangeservice.health.ExternalServiceClient;
import org.example.coinchangeservice.resources.CoinChangeResource;
import org.example.coinchangeservice.service.CoinChangeService;

import javax.servlet.DispatcherType;
import javax.servlet.FilterRegistration;
import java.util.EnumSet;

public class CoinChangeApplication extends Application<CoinChangeConfiguration> {
    public static void main(String[] args) throws Exception {
        new CoinChangeApplication().run(args);
    }

    @Override
    public void initialize(Bootstrap<CoinChangeConfiguration> bootstrap) {
        bootstrap.getObjectMapper().registerSubtypes(HttpConnectorFactory.class);

    }

    @Override
    public void run(CoinChangeConfiguration configuration, Environment environment) {
        // Register resources
        CoinChangeService service = new CoinChangeService();
        CoinChangeResource resource = new CoinChangeResource(service);
        environment.jersey().register(resource);

        // Register health checks
        ExternalServiceClient client = new ExternalServiceClient();
        CoinChangeHealthCheck healthCheck = new CoinChangeHealthCheck(
                configuration.getMaxTargetAmountLimit(),
                client
        );
        environment.healthChecks().register("coinChange", healthCheck);

        // Enable Cross-Origin Resource Sharing (CORS) to allow frontend React app to call the API
        final FilterRegistration.Dynamic cors =
                environment.servlets().addFilter("CORS", CrossOriginFilter.class);

        cors.setInitParameter("allowedOrigins", "*");
        cors.setInitParameter("allowedHeaders", "X-Requested-With,Content-Type,Accept,Origin");
        cors.setInitParameter("allowedMethods", "OPTIONS,GET,PUT,POST,DELETE,HEAD");
        cors.addMappingForUrlPatterns(EnumSet.allOf(DispatcherType.class), true, "/*");

    }

}
