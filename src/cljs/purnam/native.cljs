(ns purnam.native
  (:require [goog.object :as gobj]
            [goog.array :as garr]
            [purnam.common :as common]
            [clojure.string :as st]))

(defn nested-val [[k & ks] val]
  (if (nil? k)
    val
    (let [o (js-obj)]
      (aset o k (nested-val ks val))
      o)))

(defn nested-delete [[k & ks] val]
  (if (nil? ks)
    (js-delete val k)
    (nested-delete ks val))
  val)

(defn aset-in
  ([var arr] (aset-in var arr nil)) 
  ([var arr val]
    (let [[k & ks] arr]
      (cond (nil? k) nil
            
            (empty? ks) 
            (if val
              (aset var k val)
              (js-delete var k))
            
            :else
            (if-let [svar (aget var k)]
              (aset-in svar ks val)
              (if val (aset var k (nested-val ks val)))))
      var)))
      
(defn aget-in
  ([var] var)
  ([var arr]
     (cond  (nil? var) nil
            (empty? arr) var
            :else (aget-in (aget var (first arr))
                           (next arr)))))

(defn js-strkey [x]
  (cond
    (string? x) x
    (keyword? x) (name x)
    :else (str x)))

(defn js-obj-name [this]
  (if-let [[_ n] (re-find #"^function (\w+)" (str this))]
    n
    "Object"))

(defn js-type [o]
  (let [ty (type o)
        ty (if (and ty (.-cljs$lang$type ty))
             (.-cljs$lang$ctorStr ty)
             (js/goog.typeOf o))]
  ty))

(defn js-lookup
  ([o k]
     (aget o (js-strkey k)))
  ([o k not-found]
     (let [s (js-strkey k)]
       (if-let [res (aget o s)]
         res
         not-found))))

(defn js-range [n]
  (let [res (js/Array. n)]
    (loop [i   0]
      (if (< i n) 
        (do (.push res i)
            (recur (inc i)))
        res))))

(defn js-assoc
  ([o k v]
     (do (aset o (js-strkey k) v)
         o))
  ([o k v & more]
     (js-assoc o k v)
     (if more
       (recur o (first more) (second more) (nnext more))
       o)))

(defn js-dissoc
  [o k & more]
  (js-delete o (js-strkey k))
  (if more
    (recur o (first more) (next more))
    o))

(defn js-empty [o]
  (case (js/goog.typeOf o)
    "object"
    (doseq [k (js-keys o)]
      (js-delete o k))

    "array"
    (aset o "length" 0))
 o)

(defn js-merge
  ([o1 o2]
     (doseq [k (js-keys o2)]
       (aset o1 k (aget o2 k)))
      o1)
  ([o1 o2 & more]
     (apply js-merge (js-merge o1 o2) more)))

(defn js-merge-nil
  ([o1 o2]
     (doseq [k (js-keys o2)]
       (if-not (aget o1 k)
         (aset o1 k (aget o2 k))))
     o1)
  ([o1 o2 & more]
     (js-merge-nil o1 o2)
     (if more
       (recur (js-merge-nil o1 o2) (first more) (next more))
       o1)))

(defn js-replace [o1 o2]
 (js-empty o1)
 (js-merge o1 o2))

(defn js-equals [v1 v2]
  (if (= v1 v2) true
      (let [t1 (js/goog.typeOf v1)
            t2 (js/goog.typeOf v2)]
        (cond (= "array" t1 t2)
              (garr/equals v1 v2 js-equals)

              (= "object" t1 t2)
              (let [ks1 (.sort (js-keys v1))
                    ks2 (.sort (js-keys v2))]
                (if (garr/equals ks1 ks2)
                  (garr/every
                   ks1
                   (fn [k]
                     (js-equals (aget v1 k) (aget v2 k))))
                  false))
              :else
              false))))
              
(defn js-copy
  [o]
  (gobj/clone o))

(defn js-initial-value [v]
  (let [t (js/goog.typeOf v)]
    (cond (= t "object") (js-obj)
          (= t "array")  (array)
          :else v)))

(defn js-deep-extend
  ([to from]
     (let [visited   (array from)
           visitedlu (array to)]
       (js-deep-extend to from visited visitedlu)
       to))
  ([to from visited visitedlu]
     (doseq [k  (js-keys from)]
       (let [v  (aget from k)
             vn (js-initial-value v)]
         (cond (not= v vn)
               (let [i  (.indexOf visited v)]
                 (if (= -1 i)
                   (do (.push visited v)
                       (.push visitedlu vn)
                       (js-deep-extend vn v visited visitedlu)
                       (aset to k vn))
                   (aset to k (aget visitedlu i))))
               :else
               (aset to k v))))
     to))

(defn js-deep-copy [value]
  (let [vn (js-initial-value value)]
    (if (not= value vn)
      (js-deep-extend vn value)
      value)))

(defn js-deep-replace [o1 o2]
  (js-empty o1)
  (js-deep-extend o1 o2))

(defn js-map* [f & args]
  (let [output (array)
        len (apply min (map (fn [ar] (.-length ar)) args))]
    (doseq [i (range len)]
      (.push output (apply f (map #(aget % i) args))))
    output))

(defn js-mapcat* [f & args]
  (let [output (array)
        len (apply min (map (fn [ar] (.-length ar)) args))]
    (loop [i 0 output output]
      (if (< i len)
       (let [res (apply f (map #(aget % i) args))]
         (recur (inc i) (.concat output res)))
        output))))

(defn js-map [f & args]
  (let [output (array)
        len (apply min (map (fn [ar] (count ar)) args))]
    (doseq [i (range len)]
      (.push output (apply f (map #(nth % i) args))))
    output))

(defn js-mapcat [f & args]
  (let [output (array)
        len (apply min (map (fn [ar] (count ar)) args))]
    (loop [i 0 output output]
      (if (< i len)
       (let [res (apply f (map #(nth % i) args))]
         (recur (inc i) (.concat output res)))
        output))))

(defn js-concat [& args]
  (let [output (array)]
    (doseq [ar args
            v  ar]
      (.push output v))
    output))

(defn js<- [obj]
  (clj->js obj))

(defn log
  ([x] (if (coll? x)
         (.log js/console (str x) x)
         (.log js/console (str x))) x)
  ([x y] (if (coll? x)
           (.log js/console (str x ":") (str y) y)
           (.log js/console (str x ":") (str y))) y))
