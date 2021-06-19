# fuzzy-logic-cloud
Implementation of fuzzy logic in cloudsim

We present a full walkthrough of a fuzzy logic process in order to best explain it. This is based on the actual
algorithm and logic that we are using.
**The Setup:**
We have 2 input parameters — Bandwidth required (BW), and processing power required (PP).
Now, using these we have to output a priority score.
Every fuzzy system defines some fuzzy sets. Let’s say for bandwidth we have
BW: Very Low (VL), Low (L), Medium (M), High (H)
i.e., any input of BW may lie in any of these subsets, and even in more than one, with varying degrees of
membership.

Similarly, we have defined 3 fuzzy subsets for PP and 4 for priority, as follows:
PP: Low (L), Medium (M), High (H)
Priority: Type 1 (T1), Type 2 (T2), Type 3 (T3), Type 4 (T4)

A fuzzy system also has a rulebase which is obtained usually either through the input of experts, or through
artificial intelligence and machine learning processes. In our example we are taking only a single rule, which
is as follows:
_IF bandwidth IS low AND processingPower IS medium THEN priority is type 3_
