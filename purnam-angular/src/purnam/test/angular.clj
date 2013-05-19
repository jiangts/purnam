(ns purnam.test.angular
  (:require [clojure.string :as s])
  (:use [purnam.js :only [js-expand change-roots-map cons-sym-root]]))

(def l list)

(defmacro describe.ng [mopts & body]
  (let [{:keys [module bindings]} mopts]
    (l 'let ['spec '(js-obj)]
       (apply
        l 'describe mopts
        (l 'js/beforeEach
           (l 'js/module (str module)))
        body))))

(defmacro ng [[name] desc & body]
  (let [[desc body]
        (if (string? desc)
          [desc body]
          ["" (cons desc body)])])
  (l 'js/it desc
     (l 'js/inject
        (l 'array (str name)
           (concat (l 'fn [name])
                   body)))))

(defn controller-default-injections [controller]
  {:$scope '($rootScope.$new)
   :$controller (l '$controller
             (str controller) 'spec)})

(defn controller-set-injection [isym icmd]
  (l '! (symbol (str "spec." isym)) icmd))

(defmacro describe.controller [mopts & body]
  (let [{:keys [module controller inject bindings]} mopts
        ijm  (merge (controller-default-injections controller)
                    inject)
        ikeys (->> (dissoc ijm :$scope :$controller)
                    keys sort)
        inames (map name ikeys)
        icmds  (map ijm ikeys)
        isyms  (map symbol inames)
        bsyms  (conj isyms '$scope)
        tsyms  (map #(cons-sym-root % 'spec) bsyms)
        tmap   (zipmap bsyms tsyms)]
    (l 'let ['spec '(js-obj)]
       (apply
        l 'describe mopts
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
                   (l  (ijm :$controller)))))))
        (change-roots-map body tmap)))))
