(ns purnam.common.scope
  (:require [clojure.string :as s]
            [purnam.common :refer :all]))

(defn get-sym-root [sym]
  (let [syms  (s/split (name sym) #"\.")]
    (symbol (first syms))))

(defn has-sym-root? [sym root]
  (let [syms  (s/split (name sym) #"\.")]
    (cond (or (hash-map? root) (hash-set? root))
          (root (symbol (first syms)))
          :else
          (= (str root) (first syms)))))

(defn cons-sym-root [sym root]
  (symbol (str root "." sym)))

(defn change-sym-root
  ([sym new]
     (let [syms  (s/split (str sym) #"\.")
           nsyms (cons (str new) (rest syms))]
       (symbol (s/join "." nsyms))))
  ([sym old new]
     (if (has-sym-root? sym old)
       (change-sym-root sym new)
       sym)))

(defn walk-and-transform
  [form pred? ex-pred? transform]
  (let [r-fn #(walk-and-transform % pred? ex-pred? transform)]
    (cond (suppress (pred? form))     (transform form)
          (suppress (ex-pred? form))  form
          (vector? form)   (mapv r-fn form)
          (hash-set? form) (set (map r-fn form))
          (hash-map? form) (into {} (map (fn [[k x]] [k (r-fn x)]) form))
          (seq? form) (apply list (map r-fn form))
          :else form)))

(defn has-first-element [obj f]
  (and (or (list? obj) (lazy-seq? obj))
       (or (f (resolved-sym (first obj)))
           (f (first obj)))))

(defn change-roots
  ([form old new] (change-roots form old new #{}))
  ([form old new exclude]
     (walk-and-transform
      form
      #(has-sym-root? % old)
      #(has-first-element % exclude)
      #(change-sym-root % new))))

(defn change-roots-map
  ([form m] (change-roots-map form m @purnam.common/*exclude-scoping*))
  ([form m exclude]
     (walk-and-transform
      form
      #(has-sym-root? % m)
      #(has-first-element % exclude)
      #(change-sym-root % (m (get-sym-root %))))))
