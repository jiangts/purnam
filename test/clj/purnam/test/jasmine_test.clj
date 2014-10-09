(ns purnam.test.jasmine-test
  (:use midje.sweet)
  (:require [purnam.test.jasmine :as j]
            [purnam.test :refer [describe it is]]))

(fact "it-fn"
  (j/it-fn "<DESC>" '[<BODY>])
  => '(js/it "<DESC>" (clojure.core/fn [] <BODY>)))

(fact "describe-bind-vars"
  (j/describe-bind-vars '<SPEC> ['a 1])
  => '((aset <SPEC> "a" 1)))

(fact "describe-roots-map"
  (j/describe-roots-map '<SPEC> ['a 1])
  => '{a <SPEC>.a})

(fact "describe-fn"
  (j/describe-fn {:doc "hello"} '[<BODY>])
  => '(let [spec (js-obj)]
        (js/describe "hello"
                     (clojure.core/fn [] <BODY> nil))))
                  