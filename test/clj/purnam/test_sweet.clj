(ns purnam.test-angular
  (:require [midje.sweet :exclude [contains]]
            [purnam.test.sweet :as m]))

(fact
  (m/fact-groups
   '[1 => 1])
  => [[1 1]])


(fact
  (macroexpand-1
   '(m/fact
      1 => 1))
  =>
  '(let [spec (js-obj)]
     (js/describe ""
                  (clojure.core/fn []
                    (js/it "" (clojure.core/fn []
                                (.toSatisfy (js/expect 1) 1 "1" "1"))) nil)))

 )
