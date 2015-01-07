(ns purnam.test.midje
  (:require [purnam.common :refer :all]
            [purnam.test.jasmine :refer [describe-fn it-fn is-fn]]))

(defn find-arrow-positions
  ([forms] (find-arrow-positions forms [] 0))
  ([forms idxs count]
   (if (empty? forms)
     idxs
     (recur (rest forms) (if (= (first forms) '=>) (conj idxs count) idxs) (inc count)))))

(defn fact-groups [forms]
  (let [forms (vec forms)
        idxs  (set (find-arrow-positions forms))
        len   (count forms)]
    (->> (for [i (range len)]
          (cond (or (idxs (dec i)) (idxs (inc i)))
                ::nil

                (and (idxs i) (>= (dec i) 0))
                (let [actual (nth forms (dec i))
                      expected (nth forms (inc i))]
                  [::is actual expected (str "'" actual "'") (str "'" expected "'")])

                :else
                [::norm (nth forms i)]))
         (filter #(not= ::nil %))
         (vec))))

(defn fact-render [[type f1 f2 f3 f4]]
  (condp = type
    ::is `(purnam.test/is ~f1 ~f2 ~f3 ~f4)
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
    `(purnam.test/describe ~opts?
                 (purnam.test/it ""
                      ~@(map fact-render fgrps)))))
