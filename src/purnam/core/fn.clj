(ns purnam.core.fn
  (:require [purnam.common.expand :refer [expand]]))

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

(defn construct-fn [f body]
  (list 'let ['f# `(fn ~@(fn-prebody body)
                     ~@(map (fn [[args forms]]
                              `(~args ~@(expand (f forms))))
                            (fn-body body)))]
        (list 'aset 'f# "cljs$arities" (all-arities body))
        'f#))

