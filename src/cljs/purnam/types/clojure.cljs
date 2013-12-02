(ns purnam.types.clojure
  (:require [goog.object :as gobject]
            [goog.array :as  garray]
            [purnam.native :as j]))
            
(defn obj-only [o method]
  (when-not (identical? (type o) js/Object)
    (throw (js/TypeError. (str (j/js-type o) " does not implement '" (name method) "'")))))

(extend-protocol ISeqable
  object
  (-seq [o]
    (obj-only o :seq)
    (map (fn [k] [k (get o k)]) (js-keys o))))

  
#_(extend-protocol IFn
  array
  (-invoke []))

(extend-protocol ICounted
  object
  (-count [o]
    (obj-only o :count)
    (.-length (js-keys o)))
  array
  (-count [a]
    (.-length a)))

(extend-protocol ILookup
  object
  (-lookup
    ([o k] (j/js-lookup o k))
    ([o k not-found] (j/js-lookup o k not-found)))
  array
  (-lookup
    ([a k] (j/js-lookup a k))
    ([a k not-found] (j/js-lookup a k not-found))))

(extend-protocol ITransientAssociative
  object
  (-assoc! [o k v]
    (j/js-assoc o k v))
  array
  (-assoc! [o i v]
    (j/js-assoc o i v)))

(extend-protocol ITransientCollection
  object
  (-conj! [o [k v]]
    (j/js-assoc o k v))

  (-persistent! [o]
    (obj-only o :persistent!)
    (into {} (map (fn [[k v]] [(keyword k) v]) o)))

  array
  (-conj! [a v]
    (do (.push a v)
        a))

  (-persistent! [a]
    (into [] a)))

(extend-protocol IEmptyableCollection
  object
  (-empty [o]
    (obj-only o :empty)
    (js-obj))
  array
  (-empty [a]
    (array)))

(extend-protocol IAssociative
  object
  (-assoc
    ([o & more]
       (j/js-copy-assoc o more)))
  array
  (-assoc
    ([o & more]
       (j/js-copy-assoc o more))))


(defn js-conj-object!
 [output [[k v] & ps]]
 (if-not k
   output
   (recur (j/js-assoc output k v) ps)))

(defn js-conj-array!
 [output [v & vs]]
 (if-not v
   output
   (recur (do (.push output v)
              output)
          vs)))

(defn js-conj-object
  [parent pairs]
  (let [o (gobject/clone parent)]
      (js-conj-object! o pairs)))

(defn js-conj-array
  [parent values]
  (let [a (garray/clone parent)]
      (js-conj-array! a values)
      a))

(extend-protocol ICollection
  object
  (-conj [parent & pairs]
    (js-conj-object parent pairs))

  array
  (-conj [parent & values]
    (js-conj-array parent values)))


(extend-type object
  IAssociative
  (-assoc [o k v]
    (obj-only o :assoc)
    (conj o [k v]))

  IMap
  (-dissoc [parent k]
    (obj-only parent :dissoc)
    (let [o (js-obj)]
      (gobject/extend o parent)
      (dissoc! o k)))

  ITransientMap
  (-dissoc! [o k]
    (gobject/remove o (j/js-strkey k))
    o))
