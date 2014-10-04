(ns perf-bench
  (:require [clojure.math.numeric-tower :as math]))

;;****************************************************************************
;;                      BENCHMARKING & ANALYSIS OF RESULTS                  ;;
;;                                                                          ;;
;;****************************************************************************

(defmacro collect-all [& forms]
  `(vector ~@forms))

(defmacro bench
  "Times the execution of forms, discarding their output and returning
  time in miliseconds."
  ([& forms]
    `(let [start# (System/nanoTime)]
       ~@forms
       (/ (double ( - (System/nanoTime) start#)) 1000000.0))))

(defmacro bench-times
  "returns time in ms required to run the supplied functions on
   average over the number of iterations"
  [its & forms]
  `(let [start# (. System (nanoTime))]
     (dotimes [n# ~its] ~@forms)
     (/ (double (- (. System (nanoTime)) start#)) (* 1000000.0 ~its))))

(defmacro bench-collect
  "Times the execution of forms, returning vector of time in miliseconds
  then results of executing the forms "
  [& forms]
  `(let [start# (. System (nanoTime))
         ret# (vector ~@forms)]
     (vec (flatten [(/ (double (- (. System (nanoTime)) start#)) 1000000.0) ret#]))))

(defn- median [xs]
  (let [xs (sort xs)
        cnt (count xs)
        mid (bit-shift-right cnt 1)]
    (if (odd? cnt)
      (nth xs mid)
      (/ (+ (nth xs mid) (nth xs (dec mid))) 2))))

(defn- mean [xs]
  (/ (reduce + xs) (count xs)))

(defn- std-dev [xs]
  (let [n (count xs)
	mean (/ (reduce + xs) n)
	intermediate (map #(Math/pow (- %1 mean) 2) xs)]
    (Math/sqrt
     (/ (reduce + intermediate) n))))

(defn analyse
  "returns a map with various stats for the supplied (numeric) sequence"
  [xs]
   [{:total (reduce + xs)
     :count (count xs)
     :mean (double (mean xs))
     :median (double (median xs))
     :max (apply max xs)
     :min (apply min xs)
     :std-dev (std-dev xs)
     :lower-2sigma (- (double (mean xs)) (* 2 (std-dev xs)))
     :upper-2sigma (+ (double (mean xs)) (* 2 (std-dev xs)))}
    xs])

;;Functions for doing 'in-running' analysis
(defn- grand-mean
  "weighted/ grand mean for two populations
  M a vector of means
  N a vector of sizes"
  [M N]
  (if (= (count M) (count N))
    (/ (apply + (map * M N))
       (reduce + N))))

(defn- grand-std-dev
  "grand (combined) std deviation for two populations
  S a vector of std-devs
  M
 a vector of means
  N a vector of sizes"
  [S M N]
  (math/sqrt (- (grand-mean (map #(+ (math/expt %1 2) (math/expt %2 2)) S M)
                             N)
           (math/expt (grand-mean M N) 2))))

(defn combine-analyses
  "comines two maps of analyses created by the analyse function"
  [a1 a2]
  (let [c (+ (:count a1) (:count a2))
        m (grand-mean [(:mean a1) (:mean a2)] [(:count a1) (:count a2)])
        sd (grand-std-dev [(:std-dev a1) (:std-dev a2)]
                          [(:mean a1) (:mean a2)]
                          [(:count a1) (:count a2)])]
  { :total (+ (:total a1) (:total a2))
    :count c
    :mean m
    :median nil
    :max (max (:max a1) (:max a2))
    :min (min (:min a1) (:min a2))
    :std-dev sd
    :lower-2sigma (- (double m) (* 2 sd))
    :upper-2sigma (+ (double m) (* 2 sd))}))
