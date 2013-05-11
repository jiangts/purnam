(ns purnam.test-js
  (:require [goog.testing.jsunit :as jsunit]
            [purnam.cljs :as p])
  (:require-macros [purnam.js :as j])
  (:use-macros [purnam.js :only [obj ? ?> ! !> f.n def.n]]
               [purnam.jasmin :only [describe it is is-not]]))

(describe
 "objs contain js arrays"
 [o1 (obj :array [1 2 3 4])]

 (it "describes something"
  (is (? o1.array.0) 1)
  (is (? o1.array.1) 2)
  (is (? o1.array.2) 3)
  (is (? o1.array.3) 4)
  (is (? o1.array.4) js/undefined)))

(describe
 "objs"
 [o2 (obj :a 1 :b 2 :c 3)]
 (it "can access objects"
   (is (? o2.a) 1)
   (is (? o2.b) 2)
   (is (? o2.c) 3)
   (is (? o2.d) js/undefined)))

(describe
 "obj fns"
 [a1 (obj :a 1
            :b {:a 2
                :fn (fn [] this.a)})
  a2 (obj :a 1
            :b  (obj :a 2
                     :fn (fn [] this.a)))]
 (it "Can run functions"
     (is (!> a1.b.fn) 1)
     (is (!> a2.b.fn) 2)))

(describe
 "? and !"
 [a (js-obj)
  _ (! a.b.c.d.e "e")
  b (? a.b)
  c (? b.c)
  d (? c.d)
  e (? d.e)
  _ (! d.e "f")]

 (it "does the right thing"
     (is e "e")
     (is (? d.e) "f")
     (is-not (? d.e) e)))


(describe
 "equivalence of objects"
 [a (js-obj)
  _ (! a.b.c.d.e "e")
  b (? a.b)
  c (? b.c)
  d (? c.d)
  e (? d.e)]
 (it "uses js"
     (is b (? a.b))
     (is c (? a.b.c))
     (is d (? a.b.c.d))
     (is e (? a.b.c.d.e))))
