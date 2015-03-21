(ns midje-doc.api-purnam-test-jasmine
  (:use-macros [purnam.test :only [describe is it is-not fact facts waits-for]]))

[[:section {:title "init"}]]
"All tests require the following within the namespace declaration."

(comment 
  (:require [purnam.test])
  (:use-macros [purnam.test :only [describe it is is-not]]))

"**PLEASE NOTE**: `(:require [purnam.test])` must be placed inside your namespace. This configures jasmine with the correct checker so that tests can be run. There will be random errors outputted without it when karma is run."

[[:section {:title "describe"}]]

"`describe` is the top-level form for testing. Its usage is in combination with the setup clause `it` and the checkers `is` and `is-not`. `:globals` sets up bindings for variables that can be manipulated but cannot be rebounded. `:vars` are allowed to be rebounded."

[[{:title "describe purnam example"}]]
(describe
   {:doc "an example test description"
    :globals [ka "a"
              kb "b"]
    :vars [o (js* "{a:1,b:2,c:3}")
           y (js* "[[1, 2, 3],
                    [4, 5, 6],
                    [7, 8, 9]]")]}

   (it "dot notation for native objects"
       (is 1 o.a)
       (is 6 (+ o.a o.b o.c)))

   (it "support for both native and cljs comparisons"
       (is [1 2 3 4] [1 2 3 4]))

   (it "support for function comparison"
       (is 2 even?)
       (is-not 2 odd?)
       (is 3 (comp not even?)))

   (it "globals"
       (is o.|ka| 1)
       (is 3 (+ o.|ka| o.|kb|))))
