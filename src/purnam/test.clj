(ns purnam.test
  (:require [purnam.common :refer :all]
            [purnam.test.jasmine :refer [describe-fn it-preprocess it-fn is-fn is-not-fn]]
            [purnam.test.midje :refer [fact-fn]]))

(add-symbols purnam.common/*exclude-expansion*
           '[purnam.test describe fact facts] 
            'describe 'fact 'facts)

(add-symbols purnam.common/*exclude-scoping*
            '[purnam.test describe fact facts]
            'describe 'fact 'facts)

(defmacro fact [opts? & body]
  (fact-fn opts? body))

(defmacro facts [opts? & body]
  (fact-fn opts? body))

(defmacro describe [options & body]
  (describe-fn options body))

(defmacro it [desc & body]
  (let [[desc body] (it-preprocess desc body)]
    (it-fn desc body)))

(defmacro beforeEach [& body]
  (list 'js/beforeEach `(fn [] ~@body)))

(defmacro is 
  ([v expected]
    (is-fn v expected))
  ([v expected tactual texpected]
    (is-fn v expected tactual texpected)))

(defmacro is-not [v expected]
  (is-not-fn v expected))

(defmacro runs [& body]
  "Specs are written by defining a set of blocks with calls to runs, which usually finish with an asynchronous call.
  Once the asynchronous conditions have been met, another runs block defines final test behavior. This is usually expectations based on state after the asynch call returns."
  (list 'js/runs `(fn [] ~@body)))

(defmacro waits-for [fail-msg timeout & body]
  "The waitsFor block takes a latch function, a failure message, and a timeout."
  `(js/waitsFor (fn [] ~@body) ~fail-msg ~timeout))
