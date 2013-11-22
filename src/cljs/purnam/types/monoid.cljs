(ns purnam.types.monoid
  (:require
    [purnam.types.clojure :refer [obj-only]]
    [purnam.protocols :refer [Monoid id]])
  (:use-macros [purnam.types.macros :only [extend-all]]))
  
(extend-type nil Monoid
  (id [_] nil))

(extend-all Monoid
 [(id [m] ?%)]
 
 ;;object            []
 function          [identity]
 array             [(array)]
 string            [""]
 number            [0]
 Keyword           [(keyword "")]
 Atom              [(atom (id (deref m)))]

 LazySeq           [(lazy-seq [])]

 [EmptyList
  IndexedSeq RSeq NodeSeq 
  ArrayNodeSeq List Cons
  ChunkedCons ChunkedSeq 
  KeySeq ValSeq Range 
  PersistentArrayMapSeq
  EmptyList]       [(list)]

 [PersistentVector
  Subvec BlackNode 
  RedNode]         [(vector)]   

 [PersistentHashSet
  PersistentTreeSet] [(hash-set)]

 [PersistentHashMap
  PersistentTreeMap
  PersistentArrayMap]  [(hash-map)])