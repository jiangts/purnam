(ns midje-doc.api-purnam-cljs
  (:require [purnam.native :refer [aset-in aget-in 
                                 js-lookup js-equals 
                                 js-assoc js-dissoc js-empty
                                 js-merge js-merge-nil
                                 js-copy js-deep-copy
                                 js-replace js-deep-replace]])
  (:use-macros [purnam.core :only [obj arr]]
               [purnam.test :only [init]]
               [purnam.test.sweet :only [fact facts]]))

[[{:hide true}]]
(init)

[[:chapter {:title "purnam.native" :tag "purnam-cljs"}]]

"Utility functions for javascript native objects and arrays."

[[:section {:title "js-lookup"}]]

(facts [[{:doc "js-lookup"}]]

  "`js-lookup` is like `get` for native javascript. It works with keywords and strings"
  (js-lookup (obj :a 1 :b 2) "a") => 1
  (js-lookup (obj :a 1 :b 2) :b) => 2)

[[:section {:title "js-equals"}]]

(facts [[{:doc "js-equals"}]]
  
  "`js-equals` checks for equality on native objects. The clojurescript equality `=` does not check for equality on native objects"
  (= (obj :a 1) (obj :a 1)) => false

  "`js-equals` fills this gap:"
  (js-equals (obj :a 1) (obj :a 1)) => true
  
  "`js-equals` will also check equality for nested native objects and arrays"
  (js-equals (obj :a [{:b [{:c 1}]}])
              (obj :a [{:b [{:c 1}]}])) => true)

[[:section {:title "js-assoc"}]]

(facts [[{:doc "js-assoc"}]]

  "`js-assoc` is the native mutable version of `assoc`, it works with both keyword and string keys"

  (def o (obj :a 1))
  
  [[{:numbered false}]]
  (js-assoc o "b" 2)  ;; string
  => (obj :a 1 :b 2)
  
  (js-assoc o :c 3 "d" 4)  ;; keyword and string 
  => (obj :a 1 :b 2 :c 3 :d 4)
  
  "`js-assoc` also works with native arrays and allows number keys"
  
  (def o (array 0))
  
  [[{:numbered false}]]
  (js-assoc o "1" 1)   ;; string
  => (array 0 1)
  
  (js-assoc o 2 2)     ;; number
  => (array 0 1 2))


[[:section {:title "js-dissoc"}]]

(facts [[{:doc "js-dissoc"}]]

  "`js-dissoc` is the native mutable version of `dissoc`, it works with both keyword and string keys"

  (def o (obj :a 1 :b 2 :c 3 :d 4))
  
  [[{:numbered false}]]
  (js-dissoc o "a" :b :c)
  => (obj :d 4))

[[:section {:title "js-empty"}]]

(facts [[{:doc "js-empty"}]]

  "`js-empty` is the native mutable version of `empty`"

  (def o (obj :a 1 :b 2 :c 3 :d 4))
  
  [[{:numbered false}]]
  (js-empty o)
  => (obj))

[[:section {:title "js-merge"}]]

(facts [[{:doc "js-merge"}]]

  "`js-merge` is the native mutable version of `merge`. It will only mutate the first object argument."

  (def o1 (obj :a 1))
  (def o2 (obj :b 2))
  
  [[{:numbered false}]]
  (js-merge o1 o2)
  => (obj :a 1 :b 2)
  
  "If the keys are the same, it will overwrite"
  (def o3 (obj :b 3))
  (js-merge o1 o3)
  => (obj :a 1 :b 3))

[[:section {:title "js-merge-nil"}]]

(facts [[{:doc "js-merge-nil"}]]

  "`js-merge-nil` is like `js-merge` but it will only merge the keys that are not defined"

  (def o1 (obj :a 1))
  (def o2 (obj :a 2 :b 2))
  
  [[{:numbered false}]]
  (js-merge-nil o1 o2)
  => (obj :a 1 :b 2))

[[:section {:title "js-copy"}]]

(facts [[{:doc "js-copy"}]]

  "`js-copy` creates another object with the same key/values"

  (def o1 (obj :a 1))
  (def o2 (js-copy o1))
  
  [[{:numbered false}]]
  (js-equals o1 o2)
  => true
  
  (= o1 o2) => false)

[[:section {:title "js-deep-copy"}]]
(facts [[{:doc "js-deep-copy"}]]

  "`js-deep-copy` copys everything about an object, including circular references"

  (def o1 (obj :val 1 
               :ref self))
  (def o2 (js-deep-copy o1))
  
  "Notice that we can walk o2."
  
  (aget o2 "ref" "ref" "val")
  => 1
  
  "But o1 and o2 are not equal"
  
  (= o1 o2) => false
  
  "And neither are the references"
  (= (aget o1 "ref") (aget o2 "ref")) => false)


[[:section {:title "js-replace"}]]
(facts [[{:doc "js-replace"}]]

  "`js-replace` is like `js-copy`, but it uses keeps the pointer to the first object argument"

  (def o1 (obj :a 1))
  (js-replace o1 (obj :b 2))
  o1 => (obj :b 2))