(ns purnam.core.raw
  (:require [purnam.common :refer :all]
            [purnam.common.scope :refer [change-roots-map]]))

(declare walk-raw)

(defn walk-binding-form [[f bindings & body]]
  (let [b (partition 2 bindings)
        res (-> (mapcat (fn [[k v]] [k (walk-raw v)]) b)
                vec)]
    (apply list f res (walk-raw body))))

(defn walk-lambda-form [[f bindings & body]]
  (apply list f bindings (walk-raw body)))

(defn walk-raw [form]
  (cond (vector? form)
        (apply list 'array (map walk-raw form))

        (hash-map? form)
        (apply list 'purnam.core/obj
               (mapcat (fn [[k x]] [k (walk-raw x)]) form))

        (set? form)
        (apply list `set (map walk-raw form))

        (seq? form)
        (cond (or (get @*binding-forms* (resolved-sym (first form)))
                  (get @*binding-forms* (first form)))
              (walk-binding-form form)

              (= 'fn (first form))
              (walk-lambda-form form)

              :else
              (apply list (map walk-raw form)))
        :else form))
