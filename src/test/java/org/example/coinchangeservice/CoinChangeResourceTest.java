package org.example.coinchangeservice;

import io.dropwizard.testing.junit5.DropwizardExtensionsSupport;
import io.dropwizard.testing.junit5.ResourceExtension;
import org.example.coinchangeservice.resources.CoinChangeResource;
import org.example.coinchangeservice.service.CoinChangeService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import javax.ws.rs.client.Entity;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.util.List;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;

@ExtendWith(DropwizardExtensionsSupport.class)
public class CoinChangeResourceTest {

    private static final CoinChangeService service = new CoinChangeService();
    private static final CoinChangeResource resource = new CoinChangeResource(service);

    private static final ResourceExtension RESOURCES = ResourceExtension.builder()
            .addResource(resource)
            .build();

    @Test
    public void testValidRequest() {
        CoinChangeResource.Request req = new CoinChangeResource.Request();
        req.targetAmount = 7.03;
        req.coinDenominations = List.of(0.01, 0.5, 1.0, 5.0, 10.0);

        Response response = RESOURCES.target("/coin-change")
                .request(MediaType.APPLICATION_JSON)
                .post(Entity.entity(req, MediaType.APPLICATION_JSON));

        assertThat(response.getStatus()).isEqualTo(200);
        List<Double> result = response.readEntity(List.class);
        assertThat(result).isNotEmpty();

        // Verify the resulting coin denominations are in ascending order
        for (int i = 0; i < result.size() - 1; i++) {
            assertThat(result.get(i).doubleValue()).isLessThanOrEqualTo(result.get(i + 1).doubleValue());
        }
    }

    @Test
    public void testInvalidCoinDenomination() {
        CoinChangeResource.Request req = new CoinChangeResource.Request();
        req.targetAmount = 10.0;
        req.coinDenominations = List.of(0.03, 1.0); // 0.03 is an invalid denomination

        Response response = RESOURCES.target("/coin-change")
                .request(MediaType.APPLICATION_JSON)
                .post(Entity.entity(req, MediaType.APPLICATION_JSON));

        assertThat(response.getStatus()).isEqualTo(400);
        Map<String, String> error = response.readEntity(Map.class);
        assertThat(error.get("error")).contains("Invalid coin denomination");
    }

    @Test
    public void testEmptyCoinDenominations() {
        CoinChangeResource.Request req = new CoinChangeResource.Request();
        req.targetAmount = 10.0;
        req.coinDenominations = List.of();

        Response response = RESOURCES.target("/coin-change")
                .request(MediaType.APPLICATION_JSON)
                .post(Entity.entity(req, MediaType.APPLICATION_JSON));

        assertThat(response.getStatus()).isEqualTo(400);
        Map<String, String> error = response.readEntity(Map.class);
        assertThat(error.get("error")).contains("Coin denominations list cannot be empty");
    }

    @Test
    public void testNegativeTargetAmount() {
        CoinChangeResource.Request req = new CoinChangeResource.Request();
        req.targetAmount = -5;
        req.coinDenominations = List.of(1.0, 2.0);

        Response response = RESOURCES.target("/coin-change")
                .request(MediaType.APPLICATION_JSON)
                .post(Entity.entity(req, MediaType.APPLICATION_JSON));

        assertThat(response.getStatus()).isEqualTo(400);
        Map<String, String> error = response.readEntity(Map.class);
        assertThat(error.get("error")).contains("Target amount must be between 0 and 10,000.00");
    }

    @Test
    public void testTargetAmountTooLarge() {
        CoinChangeResource.Request req = new CoinChangeResource.Request();
        req.targetAmount = 10000.01;
        req.coinDenominations = List.of(1.0, 2.0);

        Response response = RESOURCES.target("/coin-change")
                .request(MediaType.APPLICATION_JSON)
                .post(Entity.entity(req, MediaType.APPLICATION_JSON));

        assertThat(response.getStatus()).isEqualTo(400);
        Map<String, String> error = response.readEntity(Map.class);
        assertThat(error.get("error")).contains("Target amount must be between 0 and 10,000.00");
    }

    @Test
    public void testNoSolutionFound() {
        CoinChangeResource.Request req = new CoinChangeResource.Request();
        req.targetAmount = 7.0;
        req.coinDenominations = List.of(2.0); // 2 cannot make 7

        Response response = RESOURCES.target("/coin-change")
                .request(MediaType.APPLICATION_JSON)
                .post(Entity.entity(req, MediaType.APPLICATION_JSON));

        assertThat(response.getStatus()).isEqualTo(404);
        Map<String, String> error = response.readEntity(Map.class);
        assertThat(error.get("error")).contains("No solution found");
    }

    @Test
    public void testMissingRequestBody() {
        Response response = RESOURCES.target("/coin-change")
                .request(MediaType.APPLICATION_JSON)
                .post(null);  // Empty request body

        assertThat(response.getStatus()).isEqualTo(400);
        Map<String, String> error = response.readEntity(Map.class);
        assertThat(error.get("error")).contains("Request body is missing");
    }
}
