(ns purnam.common.expand-test
  (:use midje.sweet)
  (:require [purnam.common.expand :as j]
            [purnam.common :refer :all]
            [purnam.checks :refer :all]))

(add-symbols *exclude-expansion* 'go.n 'do.n)

(fact "expand-sym"
  (j/expand-sym 'this)
  => '(js* "this")

  (j/expand-sym 'a)
  => 'a

  (j/expand-sym 'j/a)
  => 'j/a

  (j/expand-sym 'j/.a)
  => 'j/.a

  (j/expand-sym 'j/.a.b)
  => '(purnam.common/aget-in j/.a ["b"])

  (j/expand-sym 'a.b)
  => '(purnam.common/aget-in a ["b"])

  (j/expand-sym 'j/a.b)
  => '(purnam.common/aget-in j/a ["b"])

  (j/expand-sym 'a.b.c)
  => '(purnam.common/aget-in a ["b" "c"])

  (j/expand-sym 'a.b.c/d)
  => 'a.b.c/d)

(defmacro test.sym [] nil)

(fact "expand"
  (j/expand '(test.sym 1))
  => '(test.sym 1)

  (j/expand '(let [a o.x]
               a.|b|.c))
  => '(let [a (purnam.common/aget-in o ["x"])] 
         (purnam.common/aget-in a [b "c"]))

  (j/expand '(purnam.common.expand-test/test.sym 1))
  => '(purnam.common.expand-test/test.sym 1)

  (j/expand '(a.b 1))
  => '(let [obj# (purnam.common/aget-in a [])
            fn#  (aget obj# "b")]
        (.call fn# obj# 1)))

(fact "expand with-exclusions"
  (j/expand '(go.n 1))
  => '(go.n 1)

  (j/expand '(do.n 1))
  => '(do.n 1)

  (j/expand 'go.n)
  => 'go.n)
  

(remove-symbols *exclude-expansion* 'go.n)
