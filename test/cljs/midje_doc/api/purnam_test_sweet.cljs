(ns midje-doc.api.purnam-test-sweet
  (:require [purnam.core])
  (:use-macros [purnam.core :only [! ? f.n def.n obj arr]]
               [purnam.test :only [init describe is it fact facts]]))

[[:section {:title "fact"}]]

"`fact` is the default form for testing. It can currently only use the => checker and is a much lighter version of [midje](https://www.github.com/Marick/midje). However, it is enough for all but the most common cases. The [describe](#describe) example is rewritten below:"

[[{:title "fact form example"}]]
(fact
  (fact [[{:doc "an example test description"
           :globals [ka "a"
                     kb "b"]
           :vars [o (obj :a 1 :b 2 :c 3)]}]]

   "dot notation for native objects"
   o.a => 1
   (+ o.a o.b o.c) => 6

   "support for both native and cljs comparisons"
   o => (obj :a 1 :b 2 :c 3)
   [1 2 3 4] => [1 2 3 4]
   
   "support for function comparison"
    2 => even?
    3 => (comp not even?)
    
   "globals"
    o.|ka| => 1
    (+ o.|ka| o.|kb|) => 3
    
    "vars are allowed to be rebound"
    (! o (arr [1 2 3]
              [4 5 6]
              [7 8 9]))
              
    (- o.2.2 o.0.0) => 8))
[[:section {:title "facts"}]]

"`facts` and `fact` are interchangeable. The difference is how they are rendered in a document. See [lein-midje-doc](http://docs.caudate.me/lein-midje-doc#facts) for more details."

[[{:title "facts form example"}]]
(fact
  (facts [[{:doc "Basic Hashmaps"
            :globals [o {:a 1 :b 2 :c 3}]}]]
    (o :a)       => 1 
    (o :a)       => #(not= 0 %)
    (get o :b)   => 2
    (:c o)       => 3
    (select-keys o [:a :b]) => {:a 1 :b 2}))