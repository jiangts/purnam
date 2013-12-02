(ns purnam.test-cljs
  (:require [purnam.native :as j])
  (:use-macros [purnam.core :only [obj arr]]
               [purnam.test :only [init describe it is is-not is]]))

(init)

(describe
  {:doc "js/goog.typeOf"}
  (it
    (is (js/goog.typeOf js/undefined) "undefined")
    (is (js/goog.typeOf "") "string")
    (is (js/goog.typeOf nil) "null")
    (is (js/goog.typeOf (obj)) "object")
    (is (js/goog.typeOf (arr)) "array")
    ))

(describe
  {:doc "array-reduce"}
  (it
    (is (array-reduce (arr 1 2 3 4) +) 10)))

(describe
  {:doc "js-keys"}
  (it
    (is (js-keys (arr 1 2 3 4)) (arr "0" "1" "2" "3"))
    (is (js-keys (obj :a 1 :b 2)) (arr "a" "b"))))

(describe
  {:doc "scratch"}
  (it
    (is (arr ["3" 4] ["4" 5]) (array (array "3" 4) (array "4" 5)))
    (is (arr 1 2 3 4) (array 1 2 3 4))
    (is (= (obj) (obj)) false)))

(describe
 {:doc "aset"
  :globals [o (obj)]}

  (it "sets the object, replacing nested keys"
      (is (aset o "a" 1) 1)
      (is o (obj :a 1))))

(describe
 {:doc "nested-val"}
 (it "should make a nested val"
     (is (j/nested-val [] 1) 1)
     (is (j/nested-val ["a" "b"] 1) (obj :a {:b 1}))))

(describe
 {:doc "aset-in"
  :globals [o (obj)
            o1 (obj :a {:c 1})]}

 (it "sets the object, keeps nested keys"
     (is (j/aset-in o (arr "a" "b") 1) (obj :a {:b 1}))
     (is o (obj :a {:b 1}))
     (j/aset-in o1 (arr "a" "b") 1)
     (is o1 (obj :a {:b 1 :c 1}))))

(describe
 {:doc "aget-in"
  :globals [o (obj :a {:c 1})]}

 (it "sets the object, keeps nested keys"
     (is (j/aget-in o []) o)
     (is (j/aget-in o ["a"]) (obj :c 1))
     (is (j/aget-in o ["a" "b"]) nil)
     (is (j/aget-in o ["a" "c"]) 1)))

(describe
 {:doc "js-empty"
  :globals [o (obj :a 1 :b {:c 1})
            a (arr 1 2 3 4 5)]}

 (it "deletes all keys in object/array"
     (is (j/js-empty o) (obj))
     (is o (obj))
     (is (j/js-empty a) (arr))
     (is a (arr))))


(describe
 {:doc "js-merge"
  :globals [o (obj :a 1 :b {:c 1})
            o1 (obj :b 2 :c 3)]}

 (it "merges objects "
     (is (j/js-merge o o1) (obj :a 1 :b 2 :c 3))
     (is  o (obj :a 1 :b 2 :c 3))))

(describe
 {:doc "js-merge with multiple args"
  :globals [o (obj :a 1 :b 2)
            o1 (obj :b 10 :c 3)
            o2 (obj :c 10 :d 4)]}

 (it "merges all into o"
     (is (j/js-merge o o1 o2) (obj :a 1 :b 10 :c 10 :d 4))
     (is o (obj :a 1 :b 10 :c 10 :d 4))))

(describe
 {:doc "js-merge-nil"
  :globals [o  (obj :a 1 :b 2)
            o1 (obj :b 10 :c 3)]}

 (it "merges all keys of o1 that does not exist in o"
     (is (j/js-merge-nil o o1) (obj :a 1 :b 2 :c 3))
     (is o (obj :a 1 :b 2 :c 3))))

(describe
 {:doc "js-merge-nil with multiple args"
  :globals [o  (obj :a 1 :b 2)
            o1 (obj :b 10 :c 3)
            o2 (obj :c 10 :d 4)]}

 (it "merges all keys of o1 that does not exist in o"
     (is (j/js-merge-nil o o1 o2) (obj :a 1 :b 2 :c 3 :d 4))
     (is o (obj :a 1 :b 2 :c 3 :d 4))))

(describe
 {:doc "js-replace"
  :globals [o  (obj :a 1)
            o1 (obj :b 2 :c 3)]}

 (it "sets the object, keeps nested keys"
     (is (j/js-replace o o1) (obj :b 2 :c 3))
     (is o (obj :b 2 :c 3))))

(describe
  {:doc "js-equals"
   :globals [o1  (obj :a [{:b [{:c 1}]}])
             o2  (obj :a [{:b [{:c 1}]}])]}

    (it "sets the object, keeps nested keys"
    (is (j/js-equals o1 o2) true)
    (is (j/js-equals o1 (obj :c 1)) false)))

(describe
  {:doc "js-copy"
   :globals [o1  (obj :a [{:b [{:c 1}]}])
             o2  (j/js-deep-copy o1)]}
  (it "sets the object, keeps nested keys"
    (is (j/js-equals o1 o2) true)))



#_(describe
 {:doc "js-deep-copy helpers"}

 (it "tests js-deep-copy-construct"
     (is (j/js-deep-copy-construct (obj)) (obj))
     (is (j/js-deep-copy-construct (obj :a 1)) (obj))
     (is (j/js-deep-copy-construct (js/Date.)) (obj))))
