(ns time-calc.test-runner
  (:require
   [cljs.test :refer-macros [run-tests]]
   [time-calc.core-test]))

(enable-console-print!)

(defn runner []
  (if (cljs.test/successful?
       (run-tests
        'time-calc.core-test))
    0
    1))
