(ns purnam.test-types
  (:require [purnam.cljs :as j]
            [purnam.types :as t]
            [goog.object :as o]
            [goog.array :as a])
  (:use-macros [purnam.core :only [obj arr !]]
               [purnam.test :only [init describe it is is-not beforeEach]]))

(init)

(describe
  {:doc "exploring js/goog.type"}
  (it "is exploring some "
    (is (js/goog.typeOf 1)     "number")
    (is (js/goog.typeOf 1.1)   "number")
    (is (js/goog.typeOf "1")   "string")
    (is (js/goog.typeOf #"\d") "object")
    (is (js/goog.typeOf nil)   "null")
    (is (js/goog.typeOf (arr)) "array")
    (is (js/goog.typeOf (obj)) "object")
    (is (js/goog.typeOf (js/Date.)) "object")))

(describe
  {:doc "exploring type"}
  (it "types"
    (is (identical? (type (obj)) js/Object) true)
    (is (identical? (type (js/Date.)) js/Object) false)
    (is (identical? (type #"\d") js/Object) false)
    (is (identical? (type 1) js/Number) true)
    (is (identical? (type "1") js/String) true)))

(describe
  {:doc "exploring type"}
  (it "types"
    (is (instance? js/Object (obj)) true)
    (is (instance? js/Date (js/Date.)) true)
    (is (instance? js/Date (obj)) false)))

(describe
  {:doc "ICounted implementation - count"
   :globals [o (obj :a 1 :b 2 :c 3)
             a (arr 1 2 3 4)]}
  (it "works for objects"
    (is (count o) 3))
  (it "works for arrays"
    (is (count a) 4)))

(describe
  {:doc "ILookup for objects and arrays - get and get-in"
   :globals [o (obj :a 1 :b {:c {:d 2}})
             a (arr 0 [1 [2 3]])]}
  
  (it "works for objects"
    (is (get o :a) 1)
    (is (get o :b) (obj :c {:d 2}))
    (is (get-in o [:b :c :d]) 2)
    (is (get-in o [:b :c]) (obj :d 2))
    (is (get o :c) nil?)
    (is (get o :c :not-found) :not-found)
    (is (get-in o [:c :d]) nil?))
  
  (it "works for arrays"
    (is (get a 0) 0)
    (is (get a :none) nil?)
    (is (get-in a [1 1 1]) 3)
    (is (get-in a [1 1 2]) nil?)
    (is (get-in a [1 1 2] :not-found) :not-found)
  ))

(describe
  {:doc "ITransientAssociative - assoc!"
   :globals [o (obj :a 1)
             a (arr 1 2 3)]}
 (it "works for objects"
   (is (assoc! o :b 2) (obj :a 1 :b 2))
   (is o (obj :a 1 :b 2)))
 (it "works for arrays"
   (is (assoc! a 3 4) (arr 1 2 3 4))
   (is a (arr 1 2 3 4))))

(describe 
 {:doc "ITransientCollection - conj!"
  :globals [o (obj :a 1)
            a (arr 1 2 3)]}
 (it "works for objects"
   (is (conj! o (arr :b 2)) (obj :a 1 :b 2))
   (is o (obj :a 1 :b 2)))
 (it "works for arrays"
   (is (conj! a 4) (arr 1 2 3 4))))

(describe
 {:doc "IEmptyableCollection - empty"
  :globals [o (obj :a 1 :b 2 :c 3)
            a (arr 1 2 3 4)]}
 (it "works for objects"
   (is (empty o) (obj))
   (is o (obj :a 1 :b 2 :c 3)))
 (it "works for arrays"
   (is (empty a) (arr))
   (is a (arr 1 2 3 4))))

(describe
 {:doc "ICollection - conj"
  :globals [o (obj :a 1)
            a (arr 1 2 3)]}
 (it "works for objects"
   (is (conj o (arr :b 2)) (obj :a 1 :b 2))))


(describe
  {:doc "ICollection - conj"
   :globals [o (obj :a 1)
             a (arr 1 2 3)]}
  (beforeEach (j/js-replace o (obj :a 1)))
  
  (it "works for objects"
    (is (conj o (arr :b 2)) (obj :a 1 :b 2))))

#_(describe
 {:doc "strkey will convert any key to a string"}

 (it "works on "
   (is (t/strkey :a)  "a")
   (is (t/strkey "a") "a")
   ;(is (t/strkey 'a)  "a") ;; does not work on symbols
   (is (t/strkey 1)  "1")))
