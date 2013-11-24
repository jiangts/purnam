(ns purnam.core
  (:require [clojure.string :as s]
            [purnam.js :refer 
              [js-expand js-split-syms js-expand-sym js-parse-var
              js-parse-sub-exp js-expand-fn make-var make-js-array
              walk-js-raw]]))

(defmacro ? [sym]
  (js-expand-sym sym))

(defmacro ?> [& args]
  (apply list (map js-expand args)))

(defmacro ! [sym & [val]]
   (let [[var & ks] (js-split-syms sym)]
     (list 'purnam.cljs/aset-in (js-parse-var var)
         (vec (map js-parse-sub-exp ks))
         (js-expand val))))

(defmacro !> [sym & args]
  (js-expand-fn sym args))

(defmacro f.n [args & body]
  `(fn ~args ~@(js-expand body)))

(defmacro def.n [sym args & body]
  `(defn ~sym ~args
     ~@(js-expand body)))

(defmacro do.n [& body]
  `(do ~@(js-expand body)))

(defmacro property [sym & [readonly]]
  `(fn ([] (? ~sym))
       ([~'v]
        ~(if readonly
           `(throw (js/Error ~(str sym " is readonly")))
           `(cond (= "object" 
                  (js/goog.typeOf (? ~sym))
                  (js/goog.typeOf ~'v))
               (purnam.cljs/js-replace (? ~sym) ~'v)
              :else
              (! ~sym ~'v))))))



(defmacro obj [& args]
    (let [m (apply hash-map args)]
      (js-expand (make-var m))))

(defmacro arr [& args]
  (let []
     (js-expand (make-js-array args))))

(defmacro def* [name form]
 `(def ~name
       ~(js-expand (walk-js-raw form))))

(defmacro def*n [name args & body]
 `(defn ~name ~args
        ~@(js-expand (walk-js-raw body))))

(defmacro f*n [args & body]
 `(fn ~args ~@(js-expand (walk-js-raw body))))

(defmacro do*n [& body]
 `(do ~@(js-expand (walk-js-raw body))))
