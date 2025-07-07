package org.example.coinchangeservice;

import org.example.coinchangeservice.service.CoinChangeService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

public class CoinChangeServiceTest {

    private CoinChangeService service;

    @BeforeEach
    public void setUp() {
        service = new CoinChangeService();
    }

    @Test
    public void testCalculateMinCoins_validInput_example1() {
        double targetAmount = 7.03;
        List<Double> coins = List.of(0.01, 0.5, 1.0, 5.0, 10.0);

        List<Double> result = service.calculateMinCoins(targetAmount, coins);

        // Expected result: [0.01, 0.01, 0.01, 1, 1, 5]
        assertThat(result).isNotNull();
        assertThat(result).isNotEmpty();

        // Verify the sum equals the target amount
        double sum = result.stream().mapToDouble(Double::doubleValue).sum();
        assertThat(sum).isEqualTo(targetAmount);

        // Verify the result is in ascending order
        for (int i = 0; i < result.size() - 1; i++) {
            assertThat(result.get(i)).isLessThanOrEqualTo(result.get(i + 1));
        }
    }

    @Test
    public void testCalculateMinCoins_validInput_example2() {
        double targetAmount = 103;
        List<Double> coins = List.of(1.0, 2.0, 50.0);

        List<Double> result = service.calculateMinCoins(targetAmount, coins);

        // Expected result: [1, 2, 50, 50]
        assertThat(result).isNotNull();
        assertThat(result).isNotEmpty();

        double sum = result.stream().mapToDouble(Double::doubleValue).sum();
        assertThat(sum).isEqualTo(targetAmount);

        for (int i = 0; i < result.size() - 1; i++) {
            assertThat(result.get(i)).isLessThanOrEqualTo(result.get(i + 1));
        }
    }

    @Test
    public void testCalculateMinCoins_noSolution() {
        double targetAmount = 7.0;
        List<Double> coins = List.of(2.0);

        List<Double> result = service.calculateMinCoins(targetAmount, coins);

        // Since 2 cannot make up 7, the result should be empty
        assertThat(result).isEmpty();
    }

    @Test
    public void testCalculateMinCoins_emptyCoinList() {
        double targetAmount = 10.0;
        List<Double> coins = List.of();

        List<Double> result = service.calculateMinCoins(targetAmount, coins);

        assertThat(result).isEmpty();
    }

    @Test
    public void testCalculateMinCoins_negativeTarget() {
        double targetAmount = -1.0;
        List<Double> coins = List.of(1.0, 2.0);

        List<Double> result = service.calculateMinCoins(targetAmount, coins);

        assertThat(result).isEmpty();
    }

    @Test
    public void testCalculateMinCoins_targetTooLarge() {
        double targetAmount = 10000.01;
        List<Double> coins = List.of(1.0, 2.0);

        List<Double> result = service.calculateMinCoins(targetAmount, coins);

        assertThat(result).isEmpty();
    }

    @Test
    public void testCalculateMinCoins_exactSingleCoin() {
        double targetAmount = 50.0;
        List<Double> coins = List.of(0.01, 50.0, 100.0);

        List<Double> result = service.calculateMinCoins(targetAmount, coins);

        assertThat(result).containsExactly(50.0);
    }

    @Test
    public void testCalculateMinCoins_multipleSameCoins() {
        double targetAmount = 0.03;
        List<Double> coins = List.of(0.01, 0.05);

        List<Double> result = service.calculateMinCoins(targetAmount, coins);

        assertThat(result).containsExactly(0.01, 0.01, 0.01);
    }
    @Test
    public void testInvalidDenominationThrowsException() {
        double targetAmount = 5.0;
        List<Double> coins = List.of(0.03, 1.0); // 0.03

        assertThatThrownBy(() -> service.calculateMinCoins(targetAmount, coins))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("Invalid denomination");
    }

    @Test
    public void testCalculateMinCoins_usingDP() {
        double targetAmount = 6.0;
        List<Double> coins = List.of(1.0, 5.0, 10.0);

        List<Double> result = service.calculateMinCoins(targetAmount, coins, false);

        assertThat(result).isNotEmpty();
        assertThat(result.stream().mapToDouble(Double::doubleValue).sum()).isEqualTo(targetAmount);
    }

    @Test
    public void testGreedyFailsToMatchAmount() {
        double targetAmount = 3.0;
        List<Double> coins = List.of(2.0);

        List<Double> result = service.calculateMinCoins(targetAmount, coins, true);

        assertThat(result).isEmpty();
    }

}
