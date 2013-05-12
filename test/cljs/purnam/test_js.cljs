(ns purnam.test-js
  (:use [purnam.cljs :only [aget-in aset-in]])
  (:require-macros [purnam.js :as j])
  (:use-macros [purnam.js :only [obj ? ?> ! !> f.n def.n]]
               [purnam.jasmin :only [init-jasmin describe it is is-not equals]]))

(init-jasmin)

(describe
 "objs contain js arrays"
 [o1 (obj :array [1 2 3 4])]

 (it "describes something"
  (is (? o1.array.0) odd?)
  (is (? o1.array.1) 2)
  (is (? o1.array.2) 3)
  (is (? o1.array.3) 4)
  (is (? o1.array.4) js/undefined)
  (is-not (? o1.array) (array 1 2 3 4))
  (equals (? o1.array) (array 1 2 3 4))
  (is-not (array 1 2 3 4) (array 1 2 3 4))
  (equals (array 1 2 3 4) (array 1 2 3 4))))

(describe
 "obj"
 [o1 (obj :array [1 2 3 4])
  o2 (obj :a 1 :b 2 :c 3)
  n1 "a" n2 "b" n3 "c"]
 (it
  "can create js-objects and allow arbitrary accessors"
  (is (? o2.a) 1)
  (is (? o2.b) 2)
  (is (? o2.b) even?)
  (is (? o2.b) (fn [v] (= v 2)))
  (is (? o2.c) 3)
  (is (? o2.|n1|) 1)
  (is (? o2.|n2|) 2)
  (is (? o2.|n3|) 3)
  (is (? o1.array.|o2.a|) 2)
  (is (? o1.array.0) 1)
  (is (? o1.array.|o1.array.0|) 2)
  (is (? o1.array.|o1.array.|o1.array.0||) 3)
  (is (? o1.array.|o1.array.|o1.array.|o1.array.0|||) 4)
  (is (? o2.d) js/undefined)))

(describe
 "obj.this"
 [o3 (obj :a 2 :fn (fn [] this.a))
  o4 (obj :a 3 :fn o3.fn)
  a  (? o3.fn)]
 (it
  "is different to `this` in js"
  (is (aget (aget-in o3 []) "a") 2)
  (is (!> o3.fn) 2)
  (is (!> o4.fn) 2)
  (is (a) 2)
  (! o3.a 4)
  (is (!> o3.fn) 4)
  (is (!> o4.fn) 4)
  (is (a) 4)))

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
