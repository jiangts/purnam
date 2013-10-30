(ns purnam.test.sweet
  (:require [purnam.js :refer   [js-expand change-roots-map cons-sym-root]]
            [purnam.test :refer [describe-fn it-preprocess it-fn
                                 describe-default-options
                                 describe-roots-map]]))

(defn find-arrow-positions
  ([forms] (find-arrow-positions forms [] 0))
  ([[f & more] idxs count]
     (if f
       (recur more (if (= f '=>) (conj idxs count) idxs) (inc count))
       idxs)))

(defn fact-groups [forms]
  (let [forms (vec forms)
        idxs (find-arrow-positions forms)
        len  (count forms)]
    (vec (for [i idxs]
           (if (and (< (inc i) len) (>= (dec i) 0))
             [(nth forms (dec i)) (nth forms (inc i))])))))

(defn fact-is [[actual expected]]
  (list '.toSatisfy (list 'js/expect actual) expected (str actual) (str expected)))

(defn fact-fn [opts? body]
  (let [[opts? body]
        (cond (= '=> (first body))
              [{} (cons opts? body)]

              (string? opts?)
              [{:doc opts?} body]

              (instance? clojure.lang.APersistentMap opts?)
              [opts? body]

              :else [{} (cons opts? body)])
        fgrps (fact-groups body)]
    (describe-fn opts?
                 [(it-fn ""
                         (map fact-is fgrps))])))

(defmacro fact [opts? & body]
  (fact-fn opts? body))

(defmacro facts [opts? & body]
  (fact-fn opts? body))
