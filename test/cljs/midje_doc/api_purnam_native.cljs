(ns midje-doc.api-purnam-native
  (:require [purnam.native]
            [purnam.test])
  (:use-macros [purnam.test :only [fact facts]]))

[[:chapter {:title "purnam.native" :tag "purnam-native"}]]

"Clojure protocols for javascript native objects and arrays."

[[:section {:title "init" :tag "init-native"}]]

"To use this package, require `purnam.native` in your namespace"

(comment  
  (:require [purnam.native]))

[[:section {:title "seq protocol"}]]

(facts [[{:doc "seq protocol"}]]

  "The `seq` can now be used on native js arrays and objects."

  (seq (js* "[1, 2, 3, 4]")) 
  => '(1 2 3 4)

  (seq (js* "{a:1, b:2}"))
  => '(["a" 1] ["b" 2]) 

  "As well as all the built-in functionality that come with it. Although the datastructure then becomes a clojurescript lazyseq."

  (map #(* 2 %) (js* "[1, 2, 3, 4]"))
  => '(2 4 6 8)

  (take 2 (js* "[1, 2, 3, 4]"))
  => '(1 2)

  (count (js* "[1, 2, 3, 4]"))
  => 4

  (get (js* "[1, 2, 3, 4]") "1")
  => 2

  (get-in (js* "{a:{b:{c:1}}}") (js* "['a', 'b', 'c']"))
  => 1)

[[:section {:title "transient protocol"}]]

(facts [[{:doc "transient protocol"}]]

  "Transient protocol allow native js objects arrays to be manipulated using assoc!, dissoc! and persistent!"

  (let [o (js-obj)]
    (assoc! o :a 1)
    o) 
  => (js* "{a:1}")

  (let [o (js* "{a:1}")]
    (dissoc! o :a)
    o) 
  => (js-obj)

  (persistent! (js* "{a:1}"))
  => {:a 1}
  )

[[:section {:title "collection protocol"}]]

"Extension of clojure collection protocols allow native js objects arrays to use `conj`, `assoc` and `dissoc` methods."

(fact
    (def o (js-obj))
    (def o1 (conj o [:a 1]))
    o => (js-obj)
    o1 => (js* "{a:1}")
    )    

(facts [[{:doc "collection protocol"}]]
  "It works with both arrays and objects"

  (conj (js* "[1]") 2 3)
  => (js* "[1,2,3]")

  (conj (js* "{a:1}") [:b 2] [:c 3])
  => (js* "{a:1,b:2,c:3}")

  (assoc (js* "{a:1}") :b 2 :c 3)
  => (js* "{a:1,b:2,c:3}")

  (dissoc (js* "{a:1}") :a)
  => (js-obj))