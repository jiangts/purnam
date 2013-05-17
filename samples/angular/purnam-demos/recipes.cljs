(ns purnam-demo.recipes
  (:use [purnam.cljs :only [aget-in aset-in]])
  (:require [goog.object :as o])
  (:use-macros [purnam.js :only [obj arr ! def.n]]
               [purnam.angular :only [def.module def.config def.factory
                                      def.controller def.service]]))

(def JSONP (obj))

(! js/window.jsonsearch
   (fn [data]
     (! JSONP.data data)))

(def.module recipesDemo [])

(def.controller recipesDemo.MainCtrl [$scope $http]
  (->
   ($http (obj :method "JSONP"
               :params {:q "cabbage"
                        :callback "jsonsearch"}
               :url "http://www.recipepuppy.com/api"))
   (.success (fn [res]))
   (.error (fn []
             (js/console.log JSONP.data)))))
