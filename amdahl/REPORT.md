These are the results of running single.rs and multi.rs respectively.

### Machine Info
 - CPU: Apple M2
 - Cores: 4 cores
 - OS: arm64 Fedora Linux (VM)

## Single-Threaded
 - Time: 14.679900655 seconds
 - Core 0: busy=7016 idle=181291  (1.00%)
 - Core 1: busy=14919 idle=172968  (100.00%)
 - Core 2: busy=6659 idle=181658  (0.96%)
 - Core 3: busy=6809 idle=181514  (3.77%)
 - Average load across all cores is 26.43%.

## Multi-Threaded
 - Time: 4.015543584 seconds
 - Core 0: busy=6172 idle=143757  (100.00%)
 - Core 1: busy=12093 idle=137605  (100.00%)
 - Core 2: busy=5853 idle=144077  (100.00%)
 - Core 3: busy=5976 idle=143967  (100.00%)
 - Average load across all cores is 100.00%.

## Calculating Amdahl's Law using Speed Up
Let:
```
    S = 14.679900655 / 4.015543584 = 3.65576922
```

Amdahl's Law is:
```
    S = 1 / (f + (1 - f) / n)
```

Where _`S`_ is the speedup, _`f`_ is the fraction of the program runtime spent on 
non-parallelisable work and _`n`_ is the number of cores.

By rearranging Amdahl's Law to find _`f`_ we get:
```
    f = (1 / S - 1 / n) / (1 - 1 / n)
```

Substituting our _`S`_ and _`n`_ values into the formula we get:
```
    f = (1 / 3.65576922 - 1 / 8) / (1 - 1 / 8) = 0.16976028
```

This means roughly 17% of our code has to run sequentially (non-parallelisable).
