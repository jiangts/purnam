(ns purnam.core.var
  (:require [purnam.common :refer :all]
            [purnam.common.scope :refer [change-roots-map]]))
            
(declare make-var)

(defn make-js-object-resolve [sym]
  (cond (symbol? sym) sym
        (keyword? sym) (.substring (str sym) 1)
        :else (str sym)))

(defn make-js-object-aset [sym [k v]]
  (list 'aset sym (make-js-object-resolve k)
        (make-var
         (change-roots-map v {'self sym} #{'obj}))))

(defn make-js-object
  ([m] (make-js-object nil m))
  ([sym m]
     (let [sym  (or sym (gensym))
           context (gensym)
           body (map #(make-js-object-aset sym %) m)]
       (concat ['let [sym '(js-obj)]]
               body
               [sym]))))

(defn make-js-array [v]
  (apply list 'array
         (map make-var v)))

(defn make-var [v]
  (cond (hash-map? v)
        (make-js-object v)

        (vector? v)
        (make-js-array v)

        :else
        v))