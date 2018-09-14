(ns crow-cabbage.test-runner
  (:require
   [doo.runner :refer-macros [doo-tests]]
   [crow-cabbage.core-test]
   [crow-cabbage.common-test]))

(enable-console-print!)

(doo-tests 'crow-cabbage.core-test
           'crow-cabbage.common-test)
