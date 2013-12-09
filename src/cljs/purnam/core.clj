(ns purnam.core
  (:require [clojure.string :as s]
            [purnam.js :refer
              [js-expand js-split-syms js-expand-sym js-parse-var
              js-parse-sub-exp js-expand-fn make-var make-js-array
              walk-js-raw]]))

(defmacro import-from [nssym fns]
  (let [imp-fn (fn [f]
                 (list 'def f (list 'symbol (list 'str nssym "/" f))))]
  `(do ~@(map imp-fn fns))))

(defmacro ? [sym]
  (js-expand-sym sym))

(defmacro ?> [& args]
  (apply list (map js-expand args)))

(defmacro ! [sym & [val]]
   (let [[var & ks] (js-split-syms sym)]
     (list 'purnam.native/aset-in (js-parse-var var)
         (vec (map js-parse-sub-exp ks))
         (js-expand val))))

(defmacro !> [sym & args]
  (js-expand-fn sym args))

(defmacro h.n [args & body]
  (list 'let ['f# `(fn ~args ~@(js-expand body))]
        (list 'aget 'f# "cljs$arity" 2#_(js-arities ))))

(defn multi-fn?
  "tests for varidicity"
  [body]
  (if (->> body (drop-while string?) first vector?) false true))

(defn args-arity [args]
  (let [n (count args)
        nv (count (take-while #(not= % '&) args))]
    (if (= n nv) n [nv])))

(defn all-arities [body]
  (cond (multi-fn? body)
        (->> body
             (filter list?)
             (map first)
             (mapv args-arity))

        :else
        [(->> body (drop-while string?) first args-arity)]))

(defn fn-body [body]
  (if (multi-fn? body)
    (mapcat fn-body (filter list? body))
    (let [dbody (drop-while string? body)]
      [[(first dbody) (rest dbody)]])))

(defn fn-prebody [body]
  (take-while #(or (string? %) (symbol? %)) body))

(defmacro f.n [& body]
  (list 'let ['f# `(fn ~@(fn-prebody body)
                     ~@(map (fn [[args forms]]
                              `(~args ~@(js-expand forms)))
                            (fn-body body)))]
        (list 'aset 'f# "cljs$arities" (all-arities body))
        'f#))

(defmacro def.n [sym & body]
  (list 'do
        `(defn ~sym ~@(fn-prebody body)
           ~@(map (fn [[args forms]]
                    `(~args ~@(js-expand forms)))
                  (fn-body body)))
        (list 'aset sym "cljs$arities" (all-arities body))))

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
               (purnam.native/js-replace (? ~sym) ~'v)
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

(defmacro range* [n]
  `(array ~@(range n)))
