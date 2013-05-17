(ns purnam.test-jasmin
  (:use [midje.sweet :exclude [contains]]
        purnam.checks
        purnam.test))

(fact "describe"
  '(describe
    "test"
    [a1 (obj :array [1 2 3 4])]
    (it "descripition"
        (is o1.array.0 odd?)))
  =>
  (expands-into
   '(let [a1 (obj :array [1 2 3 4])]
      (js/describe
       "test"
       (clojure.core/fn []
         (it "descripition"
             (is (purnam.cljs/aget-in o1 ["array" "0"]) odd?)) nil)))))

(fact "beforeEach"
  '(beforeEach <BODY>)
  =>
  (expands-into
   '(js/beforeEach
     (clojure.core/fn []
       <BODY>))))

(fact "it"
  '(it "<DESC>" <BODY>)
  =>
  (expands-into
   '(js/it "<DESC>"
     (clojure.core/fn []
       <BODY>))))

(fact "is"
  '(is <FORM> <EXPECTED>)
  =>
  (expands-into
   '(.toSatisfy (js/expect <FORM>) <EXPECTED> "<FORM>" "<EXPECTED>")))

(fact "is-not"
  '(is-not <FORM> <EXPECTED>)
  =>
  (expands-into
   '(.toSatisfy (.-not (js/expect <FORM>))
                <EXPECTED> "<FORM>" "<EXPECTED>")))