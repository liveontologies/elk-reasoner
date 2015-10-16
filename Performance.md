# Performance #

High performance in [standard reasoning tasks](ReasoningTasks.md) is the primary goal of ELK. This is achieved by using highly efficient _consequence-based_ reasoning algorithms that have been further enhanced to take advantage of modern multi-core processors. However ELK offers very competitive performance even on single-core CPUs.

## Experimental Results ##

Initial versions of ELK illustrated a very good performance on standard [ontologies](TestOntologies.md) such as [SNOMED CT](http://www.ihtsdo.org/) which could be classified in under 5 sec on a laptop. For detailed performance results, please refer to the publications on the [main page](http://code.google.com/p/elk-reasoner/).

## How to Measure ELK Performance ##

The best way for measuring ELK performance is to use the [command line client](ElkCommandLine.md) which reports times for each major processing step (loading/parsing, saturation, transitive reduction for classification, etc.). Reasoning is mainly bounded by CPU speed, but sufficient memory is required to store all inferences. In particular, even if no out-of-memory error occurs, less memory leads to an increased garbage collection activity in Java that will  take additional time.