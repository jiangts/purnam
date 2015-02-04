(ns compiler.issue-1
  (:require [purnam.test])
  (:use-macros [purnam.common :only [set-safe-aget]]
               [purnam.core :only [? !]]))

(set! js/aa #js {})
(def a js/aa)
(! a.b.c 5555)
(+ 2 (? a.b.c))