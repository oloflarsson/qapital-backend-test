# Notes
Notes taken by Olof when implementing the solution.

## Usage
Use `./run.sh` to run the application. Then use `./test.sh` to invoke the endpoints.  

## Solution Reasoning
Unit tests seems like a good idea! We don't want to get sensitive finance business logic wrong. I'll confess I don't always write unit tests but this seems like a situation where they would really help and getting the solution right isn't trivial.

## Improvement Ideas

Using doubles for currency is inconvenient and insecure due to them being inexact. It's a known anti pattern. All kinds of odd rounding issues may happen. With that in mind switching to something like BigDecimal would likely be a good idea.

Immutability can help reduce the risk for bugs. Looking at SavingsRule for example the fields are not final. And the collections are mutable. Using Guava ImmutableList can help enforce mutability.

Using primitives where possible can help reduce the risk for bugs and make the code more readable. Looking at SavingsRule we can see that all fields are nullable. Is that nullability necessary?

SavingsRule.savingsGoalIds should be a Set and not a List?

I find the usage of LocalDate interesting and I wonder if Instant would be better? Why bother about the time zone and why throwing away information about exactly when the transaction happened?

Should the enums be uppercase to follow conventions better?
