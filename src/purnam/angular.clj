(ns purnam.angular
  (:use [purnam.js :only [patch-dotted-syms]])
  (:require [clojure.string :as s]))

(defn- inj-array [params]
  (cons 'array (map str params)))

(defn- inj-fn [params body]
  (concat (cons 'array
                (if (empty? params) []
                  `(~@(map str params))))
          [(concat ['fn params]
                   (if (empty? body) nil
                       `(~@body)))]))

(defn- module-syms [sym]
  (let [symbols    (s/split (str sym) #"\.")
        _          (assert  (= 2 (count symbols))
                            "The controller must be defined in
                           the form <module>.<controller>")]
    symbols))

(defmacro def.module [sym params]
  (let [symbols  (s/split (str sym) #"\.")
        _        (assert  (= 1 (count symbols))
                          "The module must not have any `.` s")
        [mod] symbols]
    (list 'def (symbol mod)
          (list '.module 'js/angular mod (inj-array params)))))

(defmacro def.config [mod params & body]
  (list '.config (symbol mod) (inj-fn params body)))

(defn- value-fn [sym f body]
  (let [[module ctrl] (module-syms sym)]
    (list 'do
          (list 'def (symbol ctrl) body)
          (list f (list '.module 'js/angular module)
                ctrl (symbol ctrl)))))

(defn- module-fn [sym f params body]
  (let [fn-body       (inj-fn params (patch-dotted-syms body))]
    (value-fn sym f fn-body)))

(defn angular-macro [fn-k f]
  (list 'defmacro (symbol (str "def." f)) '[sym params & body]
        (list (symbol (str (name fn-k) "-fn")) 'sym (list 'quote (symbol (str "." f))) 'params 'body)))

(defmacro defangular [m]
  (apply list 'do
         (mapcat (fn [[fn-k fns]]
                   (map #(angular-macro fn-k %) fns))
                 m)))

(defangular {:module [controller service factory provider filter directive]
             :value  [constant value resource]})
