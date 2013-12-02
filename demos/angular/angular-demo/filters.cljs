(ns angular-demos.filters
  (:use [purnam.native :only [aget-in aset-in]])
  (:require [purnam.angular.filters :as f])
  (:use-macros [purnam.core :only [obj arr ! !> def.n]]
               [purnam.angular :only
                [def.module def.config def.factory
                 def.provider def.service def.controller]]))

(def.module filtersDemo [purnam.filters])

(def.controller filtersDemo.filtersMainCtrl [$scope]
  
  (! $scope.not
    (fn [& xs]
      (fn [v]
        (every? #(not= v %) xs))))
  
  (! $scope.add
    (fn [& xs]
      (apply + xs)))
      
  (! $scope.add5
     (fn [x]
       (+ x 5)))
  
  (! $scope.isEven even?)
  
  (! $scope.numbers
     (arr 1 2 3 4 5 6 7 8))
     
  (! $scope.nested
     (arr {:main {:val 1}} {:main {:val 2}} {:main  {:val 3}} {:main  {:val 4}} 
          {:main  {:val 5}} {:main {:val 6}} {:main  {:val 7}} {:main  {:val 8}}))
  )

