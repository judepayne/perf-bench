(defproject perf-bench "0.1.2"
  :description "mini IO performance benchmarking library for Clojure"
  :url "http://github.com/judepayne/perf-bench"
  :scm {:name "git"
        :url "http://github.com/judepayne/perf-bench"}
  :license {:name "Eclipse Public License"
            :url "http://www.eclipse.org/legal/epl-v10.html"}
  :dependencies [[org.clojure/clojure "1.6.0"]
                 [org.clojure/math.numeric-tower "0.0.4"]]
  :signing {:gpg-key "5C92FAF1"}
  :deploy-repositories [["clojars" {:creds :gpg}]])
