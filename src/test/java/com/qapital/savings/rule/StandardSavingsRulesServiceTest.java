package com.qapital.savings.rule;

import com.qapital.bankdata.transaction.Transaction;
import com.qapital.savings.event.SavingsEvent;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;
import java.util.List;

public class StandardSavingsRulesServiceTest {

    private static final double DELTA = 0.000001;

    @Test
    void isApplicableTrue() {
        long userId = 1337;
        SavingsRule savingsRule = SavingsRule.createGuiltyPleasureRule(1L, userId, "Starbucks", 3.00d);
        Transaction transaction = new Transaction(1L, userId, -5.34d, "Starbucks", LocalDate.of(2015, 7, 1));

        boolean expected = true;
        boolean actual = StandardSavingsRulesService.isApplicable(savingsRule, transaction);
        Assertions.assertEquals(expected, actual);
    }

    @Test
    void isApplicableFalseWithDifferentUserId() {
        SavingsRule savingsRule = SavingsRule.createGuiltyPleasureRule(1L, 1337L, "Starbucks", 3.00d);
        Transaction transaction = new Transaction(1L, 1337999L, -5.34d, "Starbucks", LocalDate.of(2015, 7, 1));

        boolean expected = false;
        boolean actual = StandardSavingsRulesService.isApplicable(savingsRule, transaction);
        Assertions.assertEquals(expected, actual);
    }

    @Test
    void isApplicableFalseWithPausedRule() {
        long userId = 1337;
        SavingsRule savingsRule = SavingsRule.createGuiltyPleasureRule(1L, userId, "Starbucks", 3.00d);
        savingsRule.setStatus(SavingsRule.Status.paused);
        Transaction transaction = new Transaction(1L, userId, -5.34d, "Starbucks", LocalDate.of(2015, 7, 1));

        boolean expected = false;
        boolean actual = StandardSavingsRulesService.isApplicable(savingsRule, transaction);
        Assertions.assertEquals(expected, actual);
    }

    @Test
    void isApplicableFalseWithPositiveTransactionAmount() {
        long userId = 1337;
        SavingsRule savingsRule = SavingsRule.createGuiltyPleasureRule(1L, userId, "Starbucks", 3.00d);
        Transaction transaction = new Transaction(1L, userId, +999d, "Starbucks", LocalDate.of(2015, 7, 1));

        boolean expected = false;
        boolean actual = StandardSavingsRulesService.isApplicable(savingsRule, transaction);
        Assertions.assertEquals(expected, actual);
    }

    @Test
    void executeRuleWithMultipleGoals() {
        long userId = 1337;
        SavingsRule savingsRule = SavingsRule.createGuiltyPleasureRule(1L, userId, "Starbucks", 3.00d);
        savingsRule.addSavingsGoal(1L);
        savingsRule.addSavingsGoal(2L);

        Transaction transaction = new Transaction(1L, userId, -5.34d, "Starbucks", LocalDate.of(2015, 7, 1));

        List<SavingsEvent> savingsEvents = StandardSavingsRulesService.executeRule(savingsRule, transaction);
        Assertions.assertEquals(2, savingsEvents.size());
        Assertions.assertEquals(1.5d, savingsEvents.get(0).getAmount(), DELTA);
    }

    @Test
    void getSavingsAmountTotalGuiltyPleasurePositive() {
        long userId = 1337;
        SavingsRule savingsRule = SavingsRule.createGuiltyPleasureRule(1L, userId, "Starbucks", 3.00d);
        Transaction transaction = new Transaction(1L, userId, -5.34d, "Starbucks", LocalDate.of(2015, 7, 1));

        double expected = 3.00d;
        double actual = StandardSavingsRulesService.getSavingsAmountTotal(savingsRule, transaction);
        Assertions.assertEquals(expected, actual, DELTA);
    }

    @Test
    void getSavingsAmountTotalGuiltyPleasureNegative() {
        long userId = 1337;
        SavingsRule savingsRule = SavingsRule.createGuiltyPleasureRule(1L, userId, "Starbucks", 3.00d);
        Transaction transaction = new Transaction(3L, userId, -3.09d, "McDonald's", LocalDate.of(2015, 7, 2));

        double expected = 0;
        double actual = StandardSavingsRulesService.getSavingsAmountTotal(savingsRule, transaction);
        Assertions.assertEquals(expected, actual, DELTA);
    }

    @Test
    void getSavingsAmountTotalRoundupPositive() {
        long userId = 1337;
        SavingsRule roundupRule = SavingsRule.createRoundupRule(2L, 1337L, 2.00d);
        Transaction transaction = new Transaction(7L, userId, -9.76d, "Amazon", LocalDate.of(2015, 7, 8));

        double expected = 0.24d;
        double actual = StandardSavingsRulesService.getSavingsAmountTotal(roundupRule, transaction);
        Assertions.assertEquals(expected, actual, DELTA);
    }

    @Test
    void getSavingsAmountTotalRoundupNegative() {
        long userId = 1337;
        SavingsRule roundupRule = SavingsRule.createRoundupRule(2L, 1337L, 2.00d);
        Transaction transaction = new Transaction(11L, userId, -10.0d, "Apple Itunes", LocalDate.of(2015, 8, 3));

        double expected = 0;
        double actual = StandardSavingsRulesService.getSavingsAmountTotal(roundupRule, transaction);
        Assertions.assertEquals(expected, actual, DELTA);
    }

    @Test
    void getRoundupRuleAmount012And200() {
        double expected = 1.88;
        double actual = StandardSavingsRulesService.getRoundupRuleAmount(0.12, 2);
        Assertions.assertEquals(expected, actual, DELTA);
    }

    @Test
    void roundupToNearest1And5() {
        double expected = 5;
        double actual = StandardSavingsRulesService.roundupToNearest(1, 5);
        Assertions.assertEquals(expected, actual, DELTA);
    }

    @Test
    void roundupToNearest9And5() {
        double expected = 10;
        double actual = StandardSavingsRulesService.roundupToNearest(9, 5);
        Assertions.assertEquals(expected, actual, DELTA);
    }

    @Test
    void roundupToNearest012And100() {
        double expected = 1;
        double actual = StandardSavingsRulesService.roundupToNearest(0.12, 1);
        Assertions.assertEquals(expected, actual, DELTA);
    }

}
