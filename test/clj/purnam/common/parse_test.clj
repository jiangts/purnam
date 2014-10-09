(ns purnam.common.parse-test
  (:use midje.sweet)
  (:require [purnam.common.parse :as j]))

(fact "split-dotted"
  (j/split-dotted "a") => ["a"]
  (j/split-dotted "a.b") => ["a" "b"]
  (j/split-dotted "a.b.c") => ["a" "b" "c"]
  (j/split-dotted "a.||") => ["a" "||"]
  (j/split-dotted "a.|b|.c") => ["a" "|b|" "c"]
  (j/split-dotted "a.|b|.|c|") => ["a" "|b|" "|c|"]
  (j/split-dotted "a.|b.c|.|d|") => ["a" "|b.c|" "|d|"]
  (j/split-dotted "a.|b.|c||.|d|") => ["a" "|b.|c||" "|d|"]
  (j/split-dotted "a.|b.|c||.|d|") => ["a" "|b.|c||" "|d|"]
  (j/split-dotted "a.|b.|c.d.|e|||.|d|") => ["a" "|b.|c.d.|e|||" "|d|"])

(fact "split-dotted exceptions"
  (j/split-dotted "|a|") => (throws Exception)
  (j/split-dotted "a|") => (throws Exception)
  (j/split-dotted "a.") => (throws Exception)
  (j/split-dotted "a.|||") => (throws Exception)
  (j/split-dotted "a.|b.|e|") => (throws Exception))

(fact "symbol-with-ns?"
  (j/symbol-with-ns? 'clojure.core/add) => true
  (j/symbol-with-ns? 'js/console) => true
  (j/symbol-with-ns? 'add) => falsey
  (j/symbol-with-ns? 'js/console.log) => falsey
  (j/symbol-with-ns? 'js/console.log) => falsey)

(fact "symbol-contains-dot?"
  (j/symbol-contains-dot? 'add) => falsey
  (j/symbol-contains-dot? 'clojure.core/add) => true)

(def test.sym nil)
(def .?- nil)
(def .a nil)

(fact "js-exp?"
  (j/exp? 'test.sym) => false
  (j/exp? '.?-) => false

  (j/exp? 'purnam.common.parse-test/test.sym) => false
  (j/exp? 'purnam.common.parse-test/.?-) => false
  (j/exp? 'purnam.common.parse-test/.a) => false
  (j/exp? 'purnam.common.parse-test/.b) => false
  (j/exp? 'purnam.common.parse-test/test.non-sym) => true
  (j/exp? 'purnam.common.parse-test/.a.b.c) => true

  (j/exp? 'add) => false
  (j/exp? 'js/console) => false
  (j/exp? 'java.util.Set.) => false
  (j/exp? 'java.math.BigInteger/probablePrime) => false
  (j/exp? 'clojure.core/add) => false
  (j/exp? 'clojure.core) => true

  (j/exp? 'x.y/a.|b|.c) => true
  (j/exp? 'x.|y|.a) => true)

(fact "js-split-first"
  (j/split-first 'js/console.log) => '("js/console" ".log")
  (j/split-first 'a.b.c) => ["a" ".b.c"]
  (j/split-first 'a|b|.b.c) => nil
  (j/split-first 'js/console) => nil)

(fact "js-split-syms"
  (j/split-syms 'js/console.log) => ["js/console" "log"]
  (j/split-syms 'a.b.c/d.e.f) => ["a.b.c/d" "e" "f"]
  (j/split-syms 'a.b.c) => ["a" "b" "c"]
  (j/split-syms 'a.|b|.b.c) => ["a" "|b|" "b" "c"]
  (j/split-syms 'a|b|.b.c) => (throws Exception)
  (j/split-syms 'a.|b|./b.c) => (throws Exception)
  (j/split-syms 'a.|b|.c/b.c) => (throws Exception)
  (j/split-syms 'ns/a.|ns/b.c|) => ["ns/a" "|ns/b.c|"]
  (j/split-syms 'ns/b.c) => ["ns/b" "c"])


(fact "parse-exp"
  (j/parse-exp 'a.b)
  => '(purnam.common/aget-in a ["b"])

  (j/parse-exp 'a.this)
  => '(purnam.common/aget-in a ["this"])

  (j/parse-exp 'this.a.b)
  => '(purnam.common/aget-in (js* "this") ["a" "b"])
  
  (j/parse-exp 'a.|b|)
  => '(purnam.common/aget-in a [b])
  
  )
