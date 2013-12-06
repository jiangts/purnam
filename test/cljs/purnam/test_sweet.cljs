(ns purnam.test-sweet
  (:require [purnam.core])
  (:use-macros [purnam.core :only [obj arr ? ?> ! !> f.n def.n]]
               [purnam.test :only [fact]]))

(fact [[{:doc "One Plus One"
         :globals [o1 (obj :a 1)]}]]

  (+ 1 1) => 2
  
  (str 1 2 3) => "123"
  
  o1 => (obj :a 1)
  
  (obj :a 1 :b 3) 
  => (obj :a 1 :b 3))