(ns purnam.hs.test-hs-types
  (:use [purnam.core :only [fmap]])
  (:use-macros [purnam.js :only [obj arr !]]
               [purnam.test :only [init]]
               [purnam.test.sweet :only [fact facts]]))

(init)

(fact 
  (obj) => (obj)
  (fmap inc nil) => nil
  (fmap inc [1 2 3]) => [2 3 4]
  ((fmap str +) 1 2 3) => "7")