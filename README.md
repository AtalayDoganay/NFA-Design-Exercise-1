# NFA Design Exercise 1 — Atalay Doganay

Five NFAs over the alphabet {0,1}, with test strings and JFLAP 7.1 results.

**Before submission:** add the three hand-drawn tree photographs, review the reflection below against your own understanding, and confirm the problem numbering in Teams. Then submit this public repository URL on the assignment page.

| Problem | Language | Report |
|---|---|---|
| 07 | Ends with 10 | [n07r.md](n07r.md) |
| 08 | Starts with 01 and ends with 10 | [n08r.md](n08r.md) |
| 11 | Second-to-last bit is 1 | [n11r.md](n11r.md) |
| 23 | Odd length or exactly 01 | [n23r.md](n23r.md) |
| 24 | Odd number of zeros or exactly 001 | [n24r.md](n24r.md) |

Each problem has `nNN.jff`, `nNNt.txt`, and `nNNr.md`. The text files contain eight accepted strings followed by eight rejected strings, with one string per line. The empty string was added separately using JFLAP's Enter Lambda control because Load Inputs skips blank lines.

## Testing

All five files were opened in the official JFLAP 7.1 application. The actual Multiple Run pane produced 85 results in total: 17 per NFA, including the empty string. Every result matched the expected language. The reports contain the actual batch screenshots and, for #08, #11, and #24, initial and step-by-step screenshots from Step with Closure. See [jflap-validation.txt](jflap-validation.txt) for the recorded results.

An independent Python checker also passed every binary string of length 0 through 12: 40,955 checks across the five NFAs. This finite test range is additional evidence, not a proof for all input lengths. The explanation in each report gives the reason for the construction.

## Learning summary from the assisted work

### Difficult cases and selection

Problem #08 needs particular care because its prefix and suffix can overlap. The shortest accepted string is `010`, not `0110`. After reading `01`, the NFA needs both q2 (continue scanning) and q3 (the final `10` may have already begun). Leaving out q3 would incorrectly reject `010`.

These five problems were selected to cover suffix matching, overlapping conditions, the position of a bit, and two OR constructions. This work focuses on the required five; the remaining problems were not attempted or assessed for difficulty.

### Challenging strings and state tracking

No unexpected accept/reject results appeared in the recorded runs. Three boundary cases were examined in detail:

- **#08, `010`:** both q2 and q3 are possible after `01`. The final `0` reaches q4 on one path, so the input is accepted.
- **#11, `1101`:** a path can reach final state q2 while input remains. That does not accept the complete string. After the last symbol, the live states are q0 and q1; neither is final, so the input is rejected. Dead q2 configurations may remain visible in red in JFLAP.
- **#24, `001`:** the parity branch rejects because there are two zeros, but the exact-string branch reaches final state q6. One accepting path is sufficient, so the input is accepted. `0011` is rejected, showing that the exception is exactly `001`, not any string beginning with it.

The recorded runs matched the predictions for all three cases. The main risk in each example is overlooking a possible branch or accepting before the whole string has been read.

### How to avoid similar errors

Track a set of possible states after every symbol rather than choosing a single path. Take lambda closure before reading the first symbol and after each move. Keep branches that are still possible, remove paths that cannot continue, and only accept if at least one path has consumed the entire input in a final state. Tests should include the empty string, shortest accepted strings, overlapping patterns, and near misses formed by adding or changing one bit. These checks help reveal missed cases in parsers and other state-based systems, as well as in course exercises.

Step by State shows individual transitions, including lambda moves as separate steps. Step with Closure automatically includes states reachable by zero or more lambda moves. Lambda consumes no input. Both methods recognize the same language.

### AI help and references

ChatGPT/Codex was used to prepare and explain the NFAs, create test cases, check JFLAP input conventions, run JFLAP through UI automation, capture its results, and draft these reports. The explanations and recorded results above describe that assisted work. The student's own tree drawings are still required.

The following supplied repositories were used as references for problem statements and organization. Their images, hand-drawn trees, and personal reflections were not reused. The NFA files and tests in this repository were prepared separately; standard solutions may naturally use similar constructions.

- [Matthew's examples](https://github.com/c50-31-26F/Matthew_Sychareun_NFA_Design_Exercise_1): statements for #08, #11, #23, and #24.
- [Elliot's examples](https://github.com/esb5192/NFA-design-exercises-1): statement for #07 and report organization.
- [Robert's examples](https://github.com/RobertSotelo/NFA_design_exercises_1): repository organization.
- [JFLAP](https://www.jflap.org/): application used for the captured results.

The official JFLAP JAR used for the captures has SHA-256 `a22c095ddc56b18163e8ebeeef165b3a04cb570f35eb46b96105166e06c99406`. The automation source is in `tools/Capture.java`; it invokes JFLAP's real Multiple Run and Step with Closure actions and captures the application window without altering result pixels. JFLAP itself is not bundled here.
