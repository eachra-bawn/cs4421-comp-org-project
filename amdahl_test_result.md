These are the results of running singlethread.c and multithread.c respectively.

### Machine Info
 - CPU: Apple M2
 - Cores: 4 cores
 - OS: arm64 Fedora Linux (VM)

## Single-Threaded
 - Time: 19.041820 seconds
 - Core 0: busy=21482 idle=222441  (100.00%)
 - Core 1: busy=13793 idle=230746  (2.91%)
 - Core 2: busy=14083 idle=230437  (1.96%)
 - Core 3: busy=15500 idle=229085  (1.96%)
 - Average load across all cores is 26.71%.

## Multi-Threaded
 - Time: 5.462875 seconds
 - Core 0: busy=24528 idle=241768  (100.00%)
 - Core 1: busy=14701 idle=252311  (100.00%)
 - Core 2: busy=14963 idle=252036  (100.00%)
 - Core 3: busy=16506 idle=250569  (100.00%)
 - Average load across all cores is 100.00%.

## Calculating Amdahl's Law using Speed Up
Let:
```
    S = 19.041820 / 5.462875 = 3.48567741
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
    f = (1 / 3.48567741 - 1 / 8) / (1 - 1 / 8) = 0.185
```
