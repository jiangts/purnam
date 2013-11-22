(ns purnam.types.magma
  (:require
    [purnam.types.clojure :refer [obj-only]]
    [purnam.protocols :refer [Magma op pure]])
  (:use-macros [purnam.types.macros :only [extend-all]]))
  

(extend-type nil Magma
  (op
    ([_ y] y)
    ([_ y ys]
       (reduce op y ys))))

(defn op-function
  ([x y]
      (cond (= identity x) y
            (= identity y) x
            :else (comp x y)))
  ([x y ys]
    (reduce op-function x (cons y ys))))

(defn op-atom
  ([rx ry]
     (pure rx (op (deref rx) (deref ry))))
  ([rx ry rys]
     (pure rx (op
               (deref rx)
               (deref ry)
               (map deref rys)))))

(extend-all Magma
 [(op 
   ([x y] (?% x y)) 
   ([x y ys] (?% x y ys)))]

 function          [fmap-function]
 array             [fmap-array]
 string            [fmap-string]
 Keyword           [fmap-keyword]
 Atom              [op-atom]

 (comment
 LazySeq           [#(lazy-seq [v]) fapply-lazyseq]

 [IndexedSeq RSeq NodeSeq 
  ArrayNodeSeq List Cons
  ChunkedCons ChunkedSeq 
  KeySeq ValSeq Range 
  PersistentArrayMapSeq
  EmptyList]          [list fapply-list]

 [PersistentVector
  Subvec BlackNode 
  RedNode]            [vector fapply-coll]

 [PersistentHashSet
  PersistentTreeSet]  [hash-set fapply-coll]


 [PersistentHashMap
  PersistentTreeMap
  PersistentArrayMap]  [#(hash-map nil %2) fapply-map]))