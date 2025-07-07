package org.example.coinchangeservice.service;

import java.util.*;

public class CoinChangeService {

    private static final Set<Integer> VALID_DENOMINATIONS = new HashSet<>(
            Arrays.asList(1, 5, 10, 20, 50, 100, 200, 500, 1000, 5000, 10000, 100000));

    /**
     * using the greedy algorithm by default.
     *
     * @param targetAmount      Target amount in the range [0, 10000]
     * @param coinDenominations List of available coin denominations
     * @return Minimum coin denomination list (sorted ascending)
     * Time complexity: O(n log n + A / minCoin),
     *   where n is the number of coin types, A is the amount (in cents), minCoin is the smallest coin denomination.
     */
    public List<Double> calculateMinCoins(double targetAmount, List<Double> coinDenominations) {
        return calculateMinCoins(targetAmount, coinDenominations, true);
    }

    public List<Double> calculateMinCoins(double targetAmount, List<Double> coinDenominations, boolean useGreedy) {
        if (targetAmount < 0 || targetAmount > 10000 || coinDenominations == null || coinDenominations.isEmpty()) {
            return Collections.emptyList();
        }

        // Convert denominations to integers (unit: cents)
        int amount = (int) Math.round(targetAmount * 100);
        int n = coinDenominations.size();

        // Validate and convert denominations
        int[] coins = new int[n];
        for (int i = 0; i < n; i++) {
            int coinVal = (int) Math.round(coinDenominations.get(i) * 100);
            if (!VALID_DENOMINATIONS.contains(coinVal)) {
                throw new IllegalArgumentException("Invalid denomination: " + coinDenominations.get(i));
            }
            coins[i] = coinVal;
        }

        if (useGreedy) {
            return calculateByGreedy(amount, coins);
        } else {
            return calculateByDP(amount, coins, coinDenominations);
        }
    }

    /**
     * Overall complexity: O(n log n + A / minCoin),
     *   where n is number of coin types, A is amount (in cents), minCoin is smallest coin.
     */
    private List<Double> calculateByGreedy(int amount, int[] coins) {
        List<Integer> coinList = new ArrayList<>();
        for (int c : coins) coinList.add(c);
        coinList.sort(Comparator.reverseOrder());
        List<Double> result = new ArrayList<>();
        for (int coin : coinList) {
            while (amount >= coin) {
                amount -= coin;
                result.add(coin / 100.0);
            }
        }

        // If there's remaining amount, it means it's not possible to form the target
        if (amount > 0) {
            return Collections.emptyList();
        }

        Collections.sort(result);
        return result;
    }

    /**
     * Dynamic programming implementation, suitable for arbitrary coin combinations, guarantees optimality.
     *
     * Time complexity:
     * - Nested loops over amount A and coin types n
     * - Overall complexity O(A × n)
     *
     * Space complexity:
     * - O(A), used for dp array and coinUsed array
     */
    private List<Double> calculateByDP(int amount, int[] coins, List<Double> coinDenominations) {
        int MAX = amount + 1;
        int[] dp = new int[amount + 1];         // dp[i]: minimum coins needed to form amount i
        int[] coinUsed = new int[amount + 1];   // records the coin index used to form amount i
        Arrays.fill(dp, MAX);
        dp[0] = 0;

        for (int i = 1; i <= amount; i++) {
            for (int j = 0; j < coins.length; j++) {
                if (coins[j] <= i && dp[i - coins[j]] + 1 < dp[i]) {
                    dp[i] = dp[i - coins[j]] + 1;
                    coinUsed[i] = j;
                }
            }
        }

        if (dp[amount] == MAX) {
            return Collections.emptyList();
        }

        // Backtrack to find the coin combination
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
