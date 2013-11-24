(ns purnam.types.test-monoid
  (:use [purnam.core :only [id]])
  (:use-macros [purnam.core :only [obj arr !]]
               [purnam.test.sweet :only [fact facts]]))

(facts
 "Data structures: id."

 (id [2]) => []

 (id (list 4 5 6)) => (list)

 (id (seq [1 2])) => (empty (seq [2]))

 (id (lazy-seq [1 23])) => (lazy-seq [])

 (id #{2 3}) => #{}

 (id {:1 2}) => {}
 
 (id (array 1 2 3 4)) => (array)
 
 (id (obj :a 1)) => (obj))