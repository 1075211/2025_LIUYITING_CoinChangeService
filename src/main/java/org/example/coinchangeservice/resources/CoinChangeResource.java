package org.example.coinchangeservice.resources;

import com.fasterxml.jackson.annotation.JsonProperty;
import org.example.coinchangeservice.service.CoinChangeService;

import javax.ws.rs.Consumes;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.util.List;
import java.util.Map;
import java.util.Set;

@Path("/coin-change")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class CoinChangeResource {

    private final CoinChangeService service;

    public CoinChangeResource(CoinChangeService service) {
        this.service = service;
    }


    private static final Set<Double> ALLOWED_COINS = Set.of(
            0.01, 0.05, 0.1, 0.2, 0.5, 1d, 2d, 5d, 10d, 50d, 100d, 1000d);

    public static class Request {
        @JsonProperty
        public double targetAmount;

        @JsonProperty
        public List<Double> coinDenominations;
    }

    @POST
    public Response getMinimumCoins(Request request) {
        if (request == null) {
            return Response.status(Response.Status.BAD_REQUEST)
                    .entity(Map.of("error", "Request body is missing")).build();
        }
        if (request.targetAmount < 0 || request.targetAmount > 10000) {
            return Response.status(Response.Status.BAD_REQUEST)
                    .entity(Map.of("error", "Target amount must be between 0 and 10,000.00")).build();
        }
        if (request.coinDenominations == null || request.coinDenominations.isEmpty()) {
            return Response.status(Response.Status.BAD_REQUEST)
                    .entity(Map.of("error", "Coin denominations list cannot be empty")).build();
        }

        for (double coin : request.coinDenominations) {
            if (!ALLOWED_COINS.contains(coin)) {
                return Response.status(Response.Status.BAD_REQUEST)
                        .entity(Map.of("error", "Invalid coin denomination: " + coin)).build();
            }
        }

        List<Double> result = service.calculateMinCoins(request.targetAmount, request.coinDenominations);
        if (result == null || result.isEmpty()) {
            return Response.status(Response.Status.NOT_FOUND)
                    .entity(Map.of("error", "No solution found for the given input")).build();
        }

        return Response.ok(result).build();
    }
}



