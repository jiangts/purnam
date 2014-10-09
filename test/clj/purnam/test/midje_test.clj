(ns purnam.test.midje-test
  (:use midje.sweet)
  (:require [purnam.test.midje :refer :all]))


(fact "find-arrow-positions"
  (find-arrow-positions '(1 => 1))
  => [1]

  (find-arrow-positions '(=> 1))
  => [0])


(fact "fact-groups"
  (fact-groups '(1 => 1))
  => [[:purnam.test.midje/is 1 1 "'1'" "'1'"]]

  (fact-groups '(1 1 => 1 1))
  => [[:purnam.test.midje/norm 1]
      [:purnam.test.midje/is 1 1 "'1'" "'1'"]
      [:purnam.test.midje/norm 1]])

(fact "fact-fn"
  (fact-fn {} '(1 1 => 1 1))
  => '(purnam.test/describe {} (purnam.test/it "" {} 1 (purnam.test/is 1 1 "'1'" "'1'") 1)))
