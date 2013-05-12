(ns purnam.jasmin
  (require [clojure.string :as s]))

(defmacro init-jasmin []
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

(defmacro describe [desc bindings & body]
  (list 'let bindings
        (list 'js/describe desc
              `(fn [] ~@body
                 nil))))

(defmacro beforeEach [& body]
  (list 'js/beforeEach
        `(fn [] ~@body)))

(defmacro it [desc & body]
  (list 'js/it desc
        `(fn [] ~@body)))

(defmacro is [v expected]
  (list '.toSatisfy (list 'js/expect v) expected (str v) (str expected)))

(defmacro is-not [v expected]
  (list '.toSatisfy (list '.-not (list 'js/expect v)) expected (str v) (str expected)))

(defmacro not-equals [v expected]
  (list '.toEqual (list '.-not (list 'js/expect v)) expected))

(defmacro equals [v expected]
  (list '.toEqual (list 'js/expect v) expected))

(defmacro contains [v expected]
  (list '.toContain (list 'js/expect v) expected))
