(ns purnam.angular.directives
  (:use [purnam.cljs :only [aget-in aset-in]])
  (:require-macros [purnam.js :as j])
  (:use-macros [purnam.js :only [obj ! defv.ndef.n]]
               [purnam.angular :only [def.module def.directive]]))

(def.module purnam [])

(def.directive purnam.ngBlur [$parse]
  (fn [scope elem attrs]
    (let [f ($parse attrs.ngBlur)]
      (elem.bind
       "blur"
       (fn [e]
         (scope.$apply (fn [] (f scope (obj :$event e)))))))))

(def.directive purnam.ngFocus [$parse]
  (fn [scope elem attrs]
    (let [f ($parse attrs.ngFocus)]
      (elem.bind
       "focus"
       (fn [e]
         (scope.$apply (fn [] (f scope (obj :$event e)))))))))
