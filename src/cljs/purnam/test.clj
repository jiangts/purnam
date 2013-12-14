(ns purnam.test
  (:require [clojure.string :as s])
  (:use [purnam.js :only [js-expand change-roots-map cons-sym-root hash-map?]]))

(defmacro init []
  '(purnam.core/do.n
    (js/beforeEach
     (fn []
       (this.addMatchers
        (purnam.core/obj
         :toSatisfy
         (fn [expected tactual texpected]
           (let [actual this.actual
                 actualText (str actual)
                 actualText (if (= actualText "[object Object]")
                               (let [ks (js/goog.object.getKeys actual)
                                     vs (js/goog.object.getValues actual)]
                                 (into {} (map (fn [x y] [x y]) 
                                             ks vs)))
                               actualText)
                 notText (if this.isNot "Not " "")]
             (aset this "message"
                   (fn []
                     (str "Expression: " tactual
                          "\n  Expected: " notText texpected
                          "\n  Actual: " actualText)))
             (cond (= (js/goog.typeOf expected) "array")
                   (purnam.native/js-equals expected actual)

                   (fn? expected)
                   (expected actual)

                   :else
                   (or (= expected actual)
                       (purnam.native/js-equals expected actual)))))))))))
                       
(def l list)

(def describe-default-options
  {:doc  ""
   :spec 'spec
   :vars []
   :globals []})

(defn describe-bind-vars
  [spec vars]
  (let [bindings (partition 2 vars)]
    (apply list
           (map (fn [[v b]]
                  (list 'aset spec (str v) b))
                bindings))))

(defn describe-roots-map
  [spec vars]
  (let [bindings (partition 2 vars)]
    (into {}
          (map (fn [[v _]]
                 [v (symbol (str spec "." v))])
                bindings))))

(defn describe-fn [options body]
  (let [[options body]
        (if (hash-map? options)
          [(merge describe-default-options options) body]
          [describe-default-options (cons options body)])
        {:keys [doc spec globals vars]} options]
    (js-expand
     (concat (l 'let (apply vector spec '(js-obj) globals))
             (describe-bind-vars spec vars)
             (l (l 'js/describe doc
                   `(fn [] ~@(change-roots-map
                             body
                             (describe-roots-map spec vars))
                      nil)))))))

(defmacro describe [options & body]
  (describe-fn options body))

(defn it-preprocess [desc body]
  (if (string? desc)
    [desc body]
    ["" (cons desc body)]))

(defn it-fn [desc body]
  (list 'js/it desc
        `(fn [] ~@body)))

(defmacro it [desc & body]
  (let [[desc body] (it-preprocess desc body)]
    (it-fn desc body)))

(defmacro beforeEach [& body]
  (list 'js/beforeEach `(fn [] ~@body)))

(defmacro is [v expected]
  (list '.toSatisfy (list 'js/expect v) expected (str v) (str expected)))

(defmacro is-not [v expected]
  (list '.toSatisfy (list '.-not (list 'js/expect v)) expected (str v) (str expected)))

(defmacro runs [& body]
  "Specs are written by defining a set of blocks with calls to runs, which usually finish with an asynchronous call.
  Once the asynchronous conditions have been met, another runs block defines final test behavior. This is usually expectations based on state after the asynch call returns."
  (list 'js/runs `(fn [] ~@body)))

(defmacro waits-for [fail-msg timeout & body]
  "The waitsFor block takes a latch function, a failure message, and a timeout."
  `(js/waitsFor (fn [] ~@body) ~fail-msg ~timeout))


;;; ------------ FACT FORMS ------------


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
                ::nil

                (and (idxs i) (>= (dec i) 0))
                [::is (nth forms (dec i)) (nth forms (inc i))]

                :else
                [::norm (nth forms i)]))
         (filter #(not= ::nil %))
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
