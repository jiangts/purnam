(ns purnam.jasmin
  (:require [clojure.string :as s])
  (:use [purnam.js :only [js-expand change-roots-map cons-sym-root]]))

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
  (js-expand
   (list 'let bindings
         (list 'js/describe desc
               `(fn [] ~@body
                  nil)))))

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

(def l list)


(defn controller-default-injections [controller]
  {:$scope '($rootScope.$new)
   :$ctrl (l '$controller
             (str controller) 'spec)})

(defn controller-set-injection [isym icmd]
  (l '! (symbol (str "spec." isym)) icmd))

(defmacro describe.controller [desc options & body]
  (let [{:keys [module controller inject]} options
        ijm  (merge (controller-default-injections controller)
                    inject)
        ikeys (->> (dissoc ijm :$scope :$ctrl)
                    keys sort)
        inames (map name ikeys)
        icmds  (map ijm ikeys)
        isyms  (map symbol inames)
        bsyms  (conj isyms '$scope '$ctrl)
        tsyms  (map #(cons-sym-root % 'spec) bsyms)
        tmap   (zipmap bsyms tsyms)]
    (apply
     l 'describe desc
       ['spec '(js-obj)]
     (l 'js/beforeEach
        (l 'js/module (str module)))
     (l 'js/beforeEach
        (l 'js/inject
           (concat
            (l 'array "$rootScope" "$controller")
            inames
            (l (concat
                (l 'fn (apply vector '$rootScope '$controller isyms)
                   (l '! 'spec.$scope (ijm :$scope)))
                (map controller-set-injection isyms icmds)
                (l  (l '! 'spec.$ctrl (ijm :$ctrl))))))))
     (change-roots-map body tmap))))
