(ns purnam.types.applicative
  (:require
    [purnam.cljs :refer [js-map js-mapcat]]
    [purnam.types.clojure :refer [obj-only]]
    [purnam.types.functor :refer [group-entries]]
    [purnam.protocols :refer [Applicative fmap]])
  (:use-macros [purnam.types.macros :only [extend-all]]))

(defn fapply-array
  ([ag av]
    (js-mapcat #(js-map % av) ag))
  ([ag av avs]
    (js-mapcat #(apply js-map % av avs) ag)))

(defn fapply-atom
  ([ag av]
     (fmap av (deref ag)))
  ([ag av avs]
     (fmap av (deref ag) avs)))

(defn pure-coll [av v]
  (conj (empty av) v))

(defn fapply-coll
  ([ag av]
     (into (empty av)
           (mapcat #(map % av) ag)))
  ([ag av avs]
     (into (empty av)
           (mapcat #(apply map % av avs) ag))))

(defn fapply-list
 ([ag av]
    (apply list
          (mapcat #(map % av) ag)))
 ([ag av avs]
    (apply list
          (mapcat #(apply map % av avs) ag))))

(defn fapply-lazyseq
 ([ag av]
    (mapcat #(map % av) ag))
 ([ag av avs]
    (mapcat #(apply map % av avs) ag)))

(defn fapply-map
  ([ag av]
     (into
      (if-let [f (ag nil)]
        (fmap av f)
        av)
      (remove
       nil?
       (map (fn [[kg vg]]
                (if-let [[kv vv] (find av kg)]
                  [kv (vg vv)]))
              ag))))
  ([ag av avs]
     (into
      (if-let [f (ag nil)]
        (fmap av f avs)
        (apply merge av avs))
      (remove
       nil?
       (map (fn [[kg vg]]
                (if-let [vs (seq (into [] (group-entries
                                           kg (cons av avs))))]
                  [kg (apply vg vs)]))
              ag)))))

(extend-type nil Applicative
  (pure [_ _] nil)
  (fapply
    ([_ _] nil)
    ([_ _ _] nil)))
    
(extend-all Applicative
  [(pure [av v] (?% v))
   (fapply 
      ([ag av] (?% ag av))
      ([ag av avs] (?% ag av avs)))]

  ;;object             [ fapply-object]
  array             [array fapply-array]
  Atom              [atom fapply-atom]
  
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
   PersistentArrayMap]  [#(hash-map nil v) fapply-map])