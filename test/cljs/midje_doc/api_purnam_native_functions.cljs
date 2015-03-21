(ns midje-doc.purnam-native
  (:require
    [purnam.test] 
    [purnam.native.functions 
    :refer [aset-in aget-in 
            js-lookup js-equals 
            js-assoc js-dissoc js-empty
            js-merge js-merge-nil
            js-copy js-deep-copy
            js-replace js-deep-replace
            js-map js-mapcat js-concat
            js-arities]])
  (:use-macros [purnam.test :only [fact facts]]))
  
[[:chapter {:title "purnam.native.functions" :tag "purnam-native-functions"}]]

"Clojure protocols for javascript native objects and arrays."

[[:section {:title "init" :tag "init-native-functions"}]]

"To use this package, require `purnam.native.functions` in your namespace"

(comment  
  (:require [purnam.native.functions :refer [js-lookup js-equals ....]]))

"Utility functions for javascript native objects and arrays."

[[:section {:title "js-lookup"}]]

(facts [[{:doc "js-lookup"}]]

  "`js-lookup` is like `get` for native javascript. It works with keywords and strings"
  (js-lookup (js* "{a:1, b:2}") "a") => 1
  (js-lookup (js* "{a:1, b:2}") :b) => 2)

[[:section {:title "js-equals"}]]

(facts [[{:doc "js-equals"}]]
  
  "`js-equals` checks for equality on native objects. The clojurescript equality `=` does not check for equality on native objects"
  (= (js* "{a:1}") (js* "{a:1}")) => false?

  "`js-equals` fills this gap:"
  (js-equals (js* "{a:1}") (js* "{a:1}")) => true
  
  "`js-equals` will also check equality for nested native objects and arrays"
  (js-equals (js* "{a:[{b: [{c: 1}]}]}")
             (js* "{a:[{b: [{c: 1}]}]}")) => true)

[[:section {:title "js-assoc"}]]

(facts [[{:doc "js-assoc"}]]

  "`js-assoc` is the native mutable version of `assoc`, it works with both keyword and string keys"

  (def o (js* "{a:1}"))
  
  [[{:numbered false}]]
  (js-assoc o "b" 2)  ;; string
  => (js* "{a:1,b:2}")
  
  (js-assoc o :c 3 "d" 4)  ;; keyword and string 
  => (js* "{a:1,b:2,c:3,d:4}")
  
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

  (def o (js* "{a:1,b:2,c:3,d:4}"))
  
  [[{:numbered false}]]
  (js-dissoc o "a" :b :c)
  => (js* "{d:4}"))

[[:section {:title "js-empty"}]]

(facts [[{:doc "js-empty"}]]

  "`js-empty` is the native mutable version of `empty`"

  (def o (js* "{a:1,b:2,c:3,d:4}"))
  
  [[{:numbered false}]]
  (js-empty o)
  => (js-obj))

[[:section {:title "js-merge"}]]

(facts [[{:doc "js-merge"}]]

  "`js-merge` is the native mutable version of `merge`. It will only mutate the first object argument."

  (def o1 (js* "{a:1}"))
  (def o2 (js* "{b:2}"))
  
  [[{:numbered false}]]
  (js-merge o1 o2)
  => (js* "{a:1,b:2}")
  
  "If the keys are the same, it will overwrite"
  (def o3 (js* "{b:3}"))
  (js-merge o1 o3)
  => (js* "{a:1,b:3}"))

[[:section {:title "js-merge-nil"}]]

(facts [[{:doc "js-merge-nil"}]]

  "`js-merge-nil` is like `js-merge` but it will only merge the keys that are not defined"

  (def o1 (js* "{a:1}"))
  (def o2 (js* "{a:2,b:2}"))
  
  [[{:numbered false}]]
  (js-merge-nil o1 o2)
  => (js* "{a:1,b:2}"))

[[:section {:title "js-copy"}]]

(facts [[{:doc "js-copy"}]]

  "`js-copy` creates another object with the same key/values"

  (def o1 (js* "{a:1,b:2}"))
  (def o2 (js-copy o1))
  
  [[{:numbered false}]]
  (js-equals o1 o2)
  => true
  
  (= o1 o2) => false)

[[:section {:title "js-deep-copy"}]]
(facts [[{:doc "js-deep-copy"}]]

  "`js-deep-copy` copys everything about an object, including circular references"

  (def o1 (js* "function(){var a = {val:1}; a.ref = a; return a;}()"))
  (def o2 (js-deep-copy o1))
  
  "Notice that we can walk o2."
  
  (aget o2 "ref" "ref" "val")
  => 1
  
  "But o1 and o2 are not equal"
  
  (= o1 o2) => false?
  
  "And neither are the references"
  (= (aget o1 "ref") (aget o2 "ref")) => false?)


[[:section {:title "js-replace"}]]
(facts [[{:doc "js-replace"}]]

  "`js-replace` is like `js-copy`, but it uses keeps the pointer to the first object argument"

  (def o1 (js* "{a:1}"))
  (js-replace o1 (js* "{b:2}"))
  o1 => (js* "{b:2}"))

[[:section {:title "js-map"}]]
(facts [[{:doc "js-map"}]]
  "A multi argument version of map, like clojure's map but returns a native arrays"

  (js-map + (js* "[1,2,3,4]") 
            (js* "[5,6,7,8]") 
            [9 10 11 12])
  => (js* "[15,18,21,24]"))

[[:section {:title "js-concat"}]]
(facts [[{:doc "js-concat"}]]
"Concats multiple arrays into a single native arrays"

(js-concat (js* "[1,2,3,4]") 
           (js* "[5,6,7,8]") 
           [9 10 11 12])
=> (js* "[1,2,3,4,5,6,7,8,9,10,11,12]"))
  
[[:section {:title "js-mapcat"}]]
(facts [[{:doc "js-mapcat"}]]
  "Like clojure's mapcat but returns a native arrays"

  (js-mapcat list (js* "[1,2,3,4]") 
                  (js* "[5,6,7,8]") 
                  [9 10 11 12])
  => (js* "[1,5,9,2,6,10,3,7,11,4,8,12]"))

[[:section {:title "js-arities"}]]
(facts [[{:doc "js-arities"}]]
  "Returns all arities of a function. Works with f.n and def.n"

  (js-arities (fn [x] x))
  => [1])

