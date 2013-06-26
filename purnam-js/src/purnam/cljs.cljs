(ns purnam.cljs
  (:require [goog.object :as o]
            [clojure.string :as st]))

(defn obj-in
 ([ks val] (obj-in (js-obj) ks val))
 ([obj ks val]
    (if-let [k (first ks)]
      (do (aset obj k (obj-in (next ks) val))
          obj)
      val)))

(defn aset-in [var arr val]
  (let [[k & ks] arr]
    (cond (nil? k) nil
          (empty? ks) (aset var k val)
          :else
          (if-let [svar (aget var k)]
            (aset-in svar ks val)
            (aset var k (obj-in ks val))))
    var))


(defn aclear [a]
  (o/forEach (o/getKeys a)
     (fn [k] (js-delete a k)))
  a)

(defn amerge [a b]
  (o/forEach (o/getKeys b)
             (fn [k] (aset a k (aget b k))))
  a)

(defn areplace [a b]
  (aclear a)
  (amerge a b))

(defn aget-in
  ([var] var)
  ([var arr]
     (cond  (= var js/undefined) nil
            (empty? arr) var
            (nil? var) nil
            :else (aget-in (aget var (first arr))
                           (next arr)))))

(defn js<- [obj]
  (clj->js obj))

(defn log
  ([x] (if (coll? x)
         (.log js/console (str x) x)
         (.log js/console (str x))) x)
  ([x y] (if (coll? x)
           (.log js/console (str x ":") (str y) y)
           (.log js/console (str x ":") (str y))) y))
           

(defn augment-fn-string [func]
 (if (string? func)
    (fn [x]
       (aget-in x (st/split func #"\.")))
     func))

(defn check-fn [func chk]
 (fn [x]
   (let [res (func x)]
     (if (fn? chk)
        (chk res)
        (= res chk)))))

