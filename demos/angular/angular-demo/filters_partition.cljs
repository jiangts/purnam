(ns angular-demos.filters-partition
  (:use [purnam.native :only [aget-in aset-in]])
  (:require [purnam.angular.filters :as f])
  (:use-macros [purnam.core :only [obj arr ! !> def.n]]
               [purnam.angular :only
                [def.module def.config def.factory
                 def.provider def.service def.controller]]))

(def.module filtersPartitionDemo [purnam purnam.filters])

(def.service filtersPartitionDemo.Nested []
  (arr {:main {:val 1}} {:main {:val 2}} {:main  {:val 3}} {:main  {:val 4}} 
       {:main  {:val 5}} {:main {:val 6}} {:main  {:val 7}} {:main  {:val 8}}))

(def.controller filtersPartitionDemo.filtersPartitionMainCtrl [$scope Nested]   
  (! $scope.nested Nested))

