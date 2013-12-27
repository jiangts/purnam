(ns lyah.chapter-13
  (:use [purnam.core :only [bind join return pure curry]]
        [purnam.native :only [js-concat]])
  (:use-macros [purnam.core :only [obj arr ! range* $> do>]]
               [purnam.test :only [fact facts]]))