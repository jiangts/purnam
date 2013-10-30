(ns purnam.test
  (:require [clojure.string :as s])
  (:use [purnam.js :only [js-expand change-roots-map cons-sym-root hash-map? do.n obj !]]))

(defmacro init []
  '(purnam.js/do.n
    (js/beforeEach
     (fn []
       (this.addMatchers
        (purnam.js/obj
         :toSatisfy
         (fn [expected tactual texpected]
           (let [actual this.actual
                 actualText (str actual)
                 actualText (if (= actualText "[object Object]")
                               (js/JSON.stringify actual)
                               actualText)
                 notText (if this.isNot "Not " "")]
             (aset this "message"
                   (fn []
                     (str "Expression: " tactual
                          "\n  Expected: " notText texpected
                          "\n  Actual: " actualText)))
             (cond (= (js/goog.typeOf expected) "array")
                   (purnam.cljs/js-equals expected actual)

                   (fn? expected)
                   (expected actual)

                   :else
                   (or (= expected actual)
                       (purnam.cljs/js-equals expected actual)))))))))))
                       
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
