package org.example.coinchangeservice.health;

import com.codahale.metrics.health.HealthCheck;

public class CoinChangeHealthCheck extends HealthCheck {

    private final double maxTargetAmountLimit;
    private final ExternalServiceClient externalServiceClient;

    public CoinChangeHealthCheck(double maxTargetAmountLimit, ExternalServiceClient externalServiceClient) {
        this.maxTargetAmountLimit = maxTargetAmountLimit;
        this.externalServiceClient = externalServiceClient;
    }

    @Override
    protected Result check() throws Exception {
        // Check if configuration value is valid
        if (maxTargetAmountLimit <= 0 || maxTargetAmountLimit > 10000) {
            return Result.unhealthy("Invalid maxTargetAmountLimit configuration: " + maxTargetAmountLimit);
        }

        // Check if external dependent service is available
        if (!externalServiceClient.ping()) {
            return Result.unhealthy("Dependent service is unreachable");
        }

        return Result.healthy();
    }
}


