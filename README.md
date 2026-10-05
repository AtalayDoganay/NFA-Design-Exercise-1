# NFA Design Exercise 1

Atalay Doganay

## Problems

All five NFAs use the alphabet {0, 1}.

| Problem | Language | Report |
|---|---|---|
| 07 | Ends with 10 | [n07r.md](n07r.md) |
| 08 | Starts with 01 and ends with 10 | [n08r.md](n08r.md) |
| 11 | Second-to-last bit is 1 | [n11r.md](n11r.md) |
| 23 | Odd length or exactly 01 | [n23r.md](n23r.md) |
| 24 | Odd number of zeros or exactly 001 | [n24r.md](n24r.md) |

Each problem includes a JFLAP file, a test file, and a report with NFA and batch-run screenshots. Test files list eight accepted strings before eight rejected strings. The empty string was added separately with Enter Lambda. All 85 JFLAP batch results matched the expected results.

Hand-drawn trees and matching step screenshots are included for #08 (`01110`, accept), #11 (`11001`, reject), and #24 (`0010`, accept).

## Learning summary

### Difficult cases and problem selection

The main design issue in #08 is that the prefix and suffix can overlap. The shortest accepted string is `010`, not `0110`. After reading `01`, both q2 and q3 must remain possible. In the handwritten example `01110`, an early guess of the final `10` stops on another `1`, while a later guess succeeds.

For #11, reaching a final state before finishing the input is not enough. With `11001`, some branches reach q2 too early and stop. The paths that consume the whole string finish at q0 or q1, so the string is rejected.

I completed these five problems to cover suffix matching, overlapping conditions, bit position, and OR constructions. I did not attempt the remaining problems in this assignment.

### Challenging strings and corrections

The recorded runs did not produce unexpected accept/reject results. The three trees focus on cases where it is easy to overlook a branch or accept too early.

For #24, `0010` is accepted because it contains three zeros. The exact-001 branch reaches q4 after `001`, but stops on the extra `0`. The parity branch continues to accepting state q6. In contrast, `001` is accepted by the exact-string branch even though it has an even number of zeros.

Some labels were missing from my handwritten diagrams. The reports clarify those labels and identify the accepting states. The #24 state names and the step inputs were also aligned so the drawings and JFLAP runs can be compared directly.

### Avoiding similar errors

Track the full set of possible states after each symbol and include lambda closure. Keep every branch that can continue, and check acceptance only after all input has been read. Test the empty string, shortest accepted strings, overlapping patterns, and strings that differ by one symbol. These checks can help catch missed transitions in compiler and controller designs as well as course problems.

Step by State shows individual transitions. Step with Closure automatically includes states reachable through lambda moves. A lambda move consumes no input.

### Help and references

I used ChatGPT/Codex for help with NFA construction, test cases, explanations, JFLAP runs and screenshots, and report writing. I drew the three computation trees using the class examples as references.

- [Matthew's repository](https://github.com/c50-31-26F/Matthew_Sychareun_NFA_Design_Exercise_1): problem statements and computation-tree examples.
- [Elliot's repository](https://github.com/esb5192/NFA-design-exercises-1): problem #07 and report organization.
- [Robert's repository](https://github.com/RobertSotelo/NFA_design_exercises_1): repository organization.
- [JFLAP](https://www.jflap.org/): automata editor and simulator.
