(ns purnam.test
  (:require [clojure.string :as s])
  (:use [purnam.js :only [js-expand change-roots-map cons-sym-root hash-map?]]))

(defmacro init []
  (list
   'js* "beforeEach(function(){
           this.addMatchers({
             toSatisfy: function(expected, tactual, texpected){
               var actual = this.actual;
               var notText = this.isNot ? 'Not ' : '';

               this.message = function(){
                 return 'Expression: ' + tactual +
                       '\\n   Expected result: ' + notText + texpected +
                       '\\n   Actual result: ' +  actual;}

               if(typeof(expected) == 'function'){
                 return expected(actual);
               } else { return expected === actual; }
         }})});"))

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

(defmacro is-equal [v expected]
  (list '.toEqual (list 'js/expect v) expected))

(defmacro is-not-equal [v expected]
  (list '.toEqual (list '.-not (list 'js/expect v)) expected))
