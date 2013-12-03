(ns midje-doc.api-purnam-test
  (:require [purnam.core])
  (:use-macros [purnam.core :only [! ? f.n def.n obj arr]]
               [purnam.test :only [describe is it is-not fact facts]]))


[[:chapter {:title "purnam.test" :tag "purnam-test"}]]

[[:section {:title "init"}]]
"All tests require the following within the namespace declaration."

(comment 
  (:require [purnam.core])
  (:use-macros [purnam.test :only [describe it is is-not]]))

[[:section {:title "describe"}]]

"`describe` is the top-level form for testing. Its usage is in combination with the setup clause `it` and the checkers `is` and `is-not`. `:globals` sets up bindings for variables that can be manipulated but cannot be rebounded. `:vars` are allowed to be rebounded."

[[{:title "describe purnam example"}]]
(describe
   {:doc "an example test description"
    :globals [ka "a"
              kb "b"]
    :vars [o (obj :a 1 :b 2 :c 3)]}

   (it "dot notation for native objects"
       (is 1 o.a)
       (is 6 (+ o.a o.b o.c)))

   (it "support for both native and cljs comparisons"
       (is o (obj :a 1 :b 2 :c 3))
       (is [1 2 3 4] [1 2 3 4]))

   (it "support for function comparison"
       (is 2 even?)
       (is-not 2 odd?)
       (is 3 (comp not even?)))

   (it "globals"
       (is 1 (? o.|ka|))
       (is 3 (+ (? o.|ka|) (? o.|kb|))))

   (it "vars are allowed to be rebound"
       (! o (arr [1 2 3]
                 [4 5 6]
                 [7 8 9]))
       (is 8 (- (? o.2.2) (? o.0.0)))))

"Normal clojure datastructures can also be compared:"

[[{:title "describe clojure example"}]]
(describe 
 {:doc "Basic Hashmaps"
  :globals [o {:a 1 :b 2 :c 3}]}

 (it "should be easy to use"
   (is (o :a) 1)
   (is-not (o :a) 0)
   (is (get o :b) 2)
   (is (:c o) 3)
   (is (select-keys o [:a :b]) {:a 1 :b 2})))

