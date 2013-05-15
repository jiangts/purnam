(ns purnam.test.angular
  (:require [clojure.string :as s])
  (:use [purnam.js :only [js-expand change-roots-map cons-sym-root]]))
  
(def l list)

(defmacro describe.ng [desc mopts & body]
  (let [{:keys [module bindings]} mopts]
    (apply
     l 'describe desc
     (vec (concat ['spec '(js-obj)] bindings))
     (l 'js/beforeEach
        (l 'js/module (str module)))
     body)))

(defmacro service [desc [name] & body]
  (l 'js/inject
     (l 'array (str name)
        (concat (l 'fn [name])
                body))))

(defn controller-default-injections [controller]
  {:$scope '($rootScope.$new)
   :$ctrl (l '$controller
             (str controller) 'spec)})

(defn controller-set-injection [isym icmd]
  (l '! (symbol (str "spec." isym)) icmd))

(defmacro describe.controller [desc mopts & body]
  (let [{:keys [module controller inject bindings]} mopts
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
     (vec (concat ['spec '(js-obj)] bindings))
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
