# NFA Design Exercise 1 — Atalay Doganay

Five NFAs over {0,1}, ordered test files, reports, actual JFLAP results, and three hand-drawn computation trees.

| Problem | Language | Report |
|---|---|---|
| 07 | Ends with 10 | [n07r.md](n07r.md) |
| 08 | Starts with 01 and ends with 10 | [n08r.md](n08r.md) |
| 11 | Second-to-last bit is 1 | [n11r.md](n11r.md) |
| 23 | Odd length or exactly 01 | [n23r.md](n23r.md) |
| 24 | Odd number of zeros or exactly 001 | [n24r.md](n24r.md) |

Each problem has `nNN.jff`, `nNNt.txt`, and `nNNr.md`. Each test file contains eight accepted strings followed by eight rejected strings, one per line. The empty string is added separately using JFLAP's Enter Lambda control because Load Inputs skips blank lines.

## Validation and handwritten traces

All five files were run in the official JFLAP 7.1 application. The 85 Multiple Run results (17 per NFA, including the empty string) matched their expected outcomes. The reports contain the actual NFA and batch screenshots. The independent Python verifier also passed all 40,955 binary inputs of length 0 through 12 across the five NFAs. Finite testing is evidence, not a proof for every input length.

The submitted handwritten trees and matching Step with Closure screenshots use:

| Problem | Input | Outcome |
|---|---|---|
| 08 | `01110` | Accept |
| 11 | `11001` | Reject |
| 24 | `0010` | Accept |

These inputs differ from the earlier study guides. The reports, screenshots, and #11 test file were updated to match the photographs. Problem #24's states were renamed to match the drawing: q1–q4 form the exact-001 branch; q5/q6 track even/odd zero parity. Renaming does not change the language. The original handwritten photos are preserved; missing labels and final-state markings are explicitly clarified beside each photo.

## Learning summary from the assisted work

### Difficult cases and choices

Problem #08 requires care because its prefix and suffix may overlap. The shortest accepted string is `010`, not `0110`. After `01`, both q2 and q3 must be possible. The longer handwritten example `01110` also shows why early guesses of the final `10` can fail while another branch survives.

Problem #11 highlights a different trap: reaching a final state while input remains does not accept the complete string. In `11001`, earlier branches reach q2 and stop, while the complete paths end at nonfinal q0 and q1.

These five problems cover suffix matching, overlapping conditions, a bit's position, and two OR constructions. The other problems were not attempted or assessed for difficulty in this work.

### Challenging strings and corrections

No unexpected accept/reject outcomes appeared in the recorded runs. The three handwritten examples were examined as challenging computation cases rather than presented as invented failed tests.

For #24, `0010` has three zeros and is accepted by the parity branch. The exact-string branch reaches q4 at `001`, but cannot consume the extra `0`. Conversely, the batch case `001` is accepted by the exact-string branch even though its number of zeros is even. One complete accepting path is enough.

Reviewing the handwritten pages revealed omitted transition labels and differences from the earlier guide inputs/state names. The reports identify those notation omissions; the formal JFLAP file and the matching step captures make the intended computation explicit. This is why matching the exact input and the exact state names across drawings and software matters.

### Avoiding future errors

Track every possible state after each symbol, apply lambda closure, and retain all viable branches. Check acceptance only after consuming the entire input. Include empty strings, shortest accepted inputs, overlaps, and one-symbol near misses in tests. In a compiler or other state-based controller, missed branches or premature acceptance can change behavior; explicit transition tables and boundary tests help expose these errors.

Step by State exposes individual transitions, including lambda transitions. Step with Closure includes states reachable through lambda moves automatically. Lambda consumes no input; both simulations recognize the same language.

### Assistance and references

ChatGPT/Codex helped construct and explain the NFAs, prepare tests, run JFLAP through UI automation, capture results, and draft reports. Atalay supplied the handwritten photographs. The supplied class repositories informed problem statements, organization, and drawing examples; their image files and personal reflections are not submitted as Atalay's work. Standard NFA constructions may naturally resemble one another.

- [Matthew's examples](https://github.com/c50-31-26F/Matthew_Sychareun_NFA_Design_Exercise_1): problem statements and handwritten-tree examples.
- [Elliot's examples](https://github.com/esb5192/NFA-design-exercises-1): problem #07 and report organization.
- [Robert's examples](https://github.com/RobertSotelo/NFA_design_exercises_1): repository organization.
- [JFLAP](https://www.jflap.org/): application used for the results.

The actual capture source is `tools/Capture.java`; validation records are in `jflap-validation.txt` and `validation.txt`. The official JFLAP JAR SHA-256 is `a22c095ddc56b18163e8ebeeef165b3a04cb570f35eb46b96105166e06c99406`.

## Before submitting

Confirm the five problem statements/numbers against Teams and review this learning summary against your own understanding. Submit the public repository URL on the assignment page; publishing on GitHub does not itself submit to the course.
