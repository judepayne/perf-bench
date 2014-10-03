# perf-bench

A clojure library for performance benchmarking IO type operations. e.g. database/ file reads and writes. network operations etc

## Usage

add to your project.clj

    [perf-bench "0.1.5"]

use in code

    ...(:require [perf-bench :as b])...

Use the one of the bench-marking macros which takes as arguments a function and a number of iterations to run the function to measure average time taken by the function in ms (miliseconds)

Say you benchmark a thousand writes to disk and have a vector called my-times of a thousand numbers in ms, you can then call:

    analyse my-times

which produces a map with statistics about my-times

If you do are many batches of writes over time, you can use

    combine-analyses my-times1 my-times2

to combine two sets of analysis maps.

## Todo

write a throughput macro

## License

Copyright © 2014 FIXME

Distributed under the Eclipse Public License either version 1.0 or (at
your option) any later version.
