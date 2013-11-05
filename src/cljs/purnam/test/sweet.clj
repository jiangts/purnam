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
        idxs  (set (find-arrow-positions forms))
        len   (count forms)]
    (->> (for [i (range len)]
          (cond (or (idxs (dec i)) (idxs (inc i)))
                nil

                (and (idxs i) (>= (dec i) 0))
                [::is (nth forms (dec i)) (nth forms (inc i))]

                :else
                [::norm (nth forms i)]))
         (filter identity)
         (vec))))

(defn fact-is [actual expected]
  (list '.toSatisfy (list 'js/expect actual) expected (str actual) (str expected)))

(defn fact-render [[type f1 f2]]
  (condp = type
    ::is (fact-is f1 f2)
    ::norm f1))

(defn double-vec-map? [ele]
  (and (vector? ele)
       (vector? (first ele))
       (instance? clojure.lang.APersistentMap (ffirst ele))))

(defn fact-fn [opts? body]
  (let [[opts? body]
        (cond (= '=> (first body))
              [{} (cons opts? body)]

              (string? opts?)
              [{:doc opts?} body]

              (double-vec-map? opts?)
              [(ffirst opts?) body]

              :else [{} (cons opts? body)])
        fgrps (fact-groups body)]
    (describe-fn opts?
                 [(it-fn ""
                         (map fact-render fgrps))])))

(defmacro fact [opts? & body]
  (fact-fn opts? body))

(defmacro facts [opts? & body]
  (fact-fn opts? body))
