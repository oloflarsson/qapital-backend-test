package com.qapital.savings.rule;

import com.qapital.bankdata.transaction.Transaction;
import com.qapital.bankdata.transaction.TransactionsService;
import com.qapital.savings.event.SavingsEvent;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

@Service
public class StandardSavingsRulesService implements SavingsRulesService {

    private final TransactionsService transactionsService;

    @Autowired
    public StandardSavingsRulesService(TransactionsService transactionsService) {
        this.transactionsService = transactionsService;
    }

    @Override
    public List<SavingsRule> activeRulesForUser(Long userId) {
        SavingsRule guiltyPleasureRule = SavingsRule.createGuiltyPleasureRule(1l, userId, "Starbucks", 3.00d);
        guiltyPleasureRule.addSavingsGoal(1l);
        guiltyPleasureRule.addSavingsGoal(2l);
        SavingsRule roundupRule = SavingsRule.createRoundupRule(2l, userId, 2.00d);
        roundupRule.addSavingsGoal(1l);

        return List.of(guiltyPleasureRule, roundupRule);
    }

    @Override
    public List<SavingsEvent> executeRule(SavingsRule savingsRule) {
        return transactionsService.latestTransactionsForUser(savingsRule.getUserId()).stream()
                .filter(transaction -> isApplicable(savingsRule, transaction))
                .map(transaction -> executeRule(savingsRule, transaction))
                .flatMap(Collection::stream)
                .collect(Collectors.toList());
    }

    private static boolean isApplicable(SavingsRule savingsRule, Transaction transaction) {
        return Objects.equals(savingsRule.getUserId(), transaction.getUserId()) &&
                savingsRule.isActive() &&
                transaction.getAmount() < 0;
    }

    private static List<SavingsEvent> executeRule(SavingsRule savingsRule, Transaction transaction) {
        double savingsAmountTotal = getSavingsAmountTotal(savingsRule, transaction);
        if (savingsAmountTotal == 0) {
            return new ArrayList<>();
        }

        double amount = savingsAmountTotal / savingsRule.getSavingsGoalIds().size();

        return savingsRule.getSavingsGoalIds().stream()
                .map(savingsGoalId -> SavingsEvent.ofRuleApplication(savingsRule, amount, savingsGoalId))
                .collect(Collectors.toList());
    }

    private static double getSavingsAmountTotal(SavingsRule savingsRule, Transaction transaction) {
        SavingsRule.RuleType ruleType = savingsRule.getRuleType();

        if (ruleType == SavingsRule.RuleType.roundup) {
            double positiveTransactionAmount = Math.abs(transaction.getAmount());
            return roundupToNearest(positiveTransactionAmount, savingsRule.getAmount()) - positiveTransactionAmount;
        }

        if (ruleType == SavingsRule.RuleType.guiltypleasure) {
            if (Objects.equals(transaction.getDescription(), savingsRule.getPlaceDescription())) {
                return savingsRule.getAmount();
            } else {
                return 0;
            }
        }

        throw new UnsupportedOperationException("Not Implemented: " + ruleType);
    }

    private static double roundupToNearest(double amount, double multiple) {
        return multiple * (Math.ceil(amount / multiple));
    }
}
