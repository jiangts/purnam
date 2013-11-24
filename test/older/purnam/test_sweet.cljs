(ns purnam.test-sweet
  (:use [purnam.cljs :only [aget-in aset-in]])
  (:use-macros [purnam.core :only [obj arr ? ?> ! !> f.n def.n]]
               [purnam.test :only [init]]
               [purnam.test.sweet :only [fact]]))


(init)

(fact [[{:doc "One Plus One"
         :globals [o1 (obj :a 1)]}]]

  (+ 1 1) => 2
  
  (str 1 2 3) => "123"
  
  o1 => (obj :a 1)
  
  (obj :a 1 :b 3) 
  => (obj :a 1 :b 3))