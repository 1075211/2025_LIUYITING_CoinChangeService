package org.example.coinchangeservice;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.dropwizard.Configuration;
import io.dropwizard.server.DefaultServerFactory;

import javax.validation.constraints.Max;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;

public class CoinChangeConfiguration extends Configuration {

    @NotNull(message = "maxTargetAmountLimit cannot be null")
    @Min(value = 0, message = "maxTargetAmountLimit cannot be less than 0")
    @Max(value = 10000, message = "maxTargetAmountLimit cannot be greater than 10000")
    private Double maxTargetAmountLimit = 10000.0;

    @JsonProperty
    public Double getMaxTargetAmountLimit() {
        return maxTargetAmountLimit;
    }

    @JsonProperty
    public void setMaxTargetAmountLimit(Double maxTargetAmountLimit) {
        this.maxTargetAmountLimit = maxTargetAmountLimit;
    }

    // server
    private DefaultServerFactory server = new DefaultServerFactory();

    @JsonProperty("server")
    public DefaultServerFactory getServer() {
        return server;
    }

    @JsonProperty("server")
    public void setServer(DefaultServerFactory server) {
        this.server = server;
    }
}





