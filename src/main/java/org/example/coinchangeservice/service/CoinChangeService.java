package org.example.coinchangeservice.service;

import java.util.*;

public class CoinChangeService {


    public List<Double> calculateMinCoins(double targetAmount, List<Double> coinDenominations) {
        if (targetAmount < 0 || targetAmount > 10000 || coinDenominations == null || coinDenominations.isEmpty()) {
            return Collections.emptyList();
        }

        int amount = (int) Math.round(targetAmount * 100);
        int n = coinDenominations.size();

        int[] coins = new int[n];
        for (int i = 0; i < n; i++) {
            coins[i] = (int) Math.round(coinDenominations.get(i) * 100);
        }

        int MAX = amount + 1;
        int[] dp = new int[amount + 1];
        int[] coinUsed = new int[amount + 1];
        Arrays.fill(dp, MAX);
        dp[0] = 0;

        for (int i = 1; i <= amount; i++) {
            for (int j = 0; j < n; j++) {
                if (coins[j] <= i && dp[i - coins[j]] + 1 < dp[i]) {
                    dp[i] = dp[i - coins[j]] + 1;
                    coinUsed[i] = j;
                }
            }
        }

        if (dp[amount] == MAX) {
            return Collections.emptyList();
        }

        List<Double> result = new ArrayList<>();
        int remaining = amount;
        while (remaining > 0) {
            int cIndex = coinUsed[remaining];
            double coinValue = coinDenominations.get(cIndex);
            result.add(coinValue);
            remaining -= (int) Math.round(coinValue * 100);
        }

        Collections.sort(result);
        return result;
    }
}


