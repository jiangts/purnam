(ns purnam.test-js-macros
  (:use midje.sweet
        purnam.checks
        [purnam.js :only [obj]])
  (:require [purnam.js :as js]))


(fact "split-dotted"
  (js/split-dotted "a") => ["a"]
  (js/split-dotted "a.b") => ["a" "b"]
  (js/split-dotted "a.b.c") => ["a" "b" "c"]
  (js/split-dotted "a.||") => ["a" "||"]
  (js/split-dotted "a.|b|.c") => ["a" "|b|" "c"]
  (js/split-dotted "a.|b|.|c|") => ["a" "|b|" "|c|"]
  (js/split-dotted "a.|b.c|.|d|") => ["a" "|b.c|" "|d|"]
  (js/split-dotted "a.|b.|c||.|d|") => ["a" "|b.|c||" "|d|"]
  (js/split-dotted "a.|b.|c||.|d|") => ["a" "|b.|c||" "|d|"]
  (js/split-dotted "a.|b.|c.d.|e|||.|d|") => ["a" "|b.|c.d.|e|||" "|d|"])

(fact "split-dotted exceptions"
  (js/split-dotted "|a|") => (throws Exception)
  (js/split-dotted "a|") => (throws Exception)
  (js/split-dotted "a.") => (throws Exception)
  (js/split-dotted "a.|||") => (throws Exception)
  (js/split-dotted "a.|b.|e|") => (throws Exception))

(fact "symbol-with-ns?"
  (js/symbol-with-ns? 'clojure.core/add) => true
  (js/symbol-with-ns? 'js/console) => true
  (js/symbol-with-ns? 'add) => falsey
  (js/symbol-with-ns? 'js/console.log) => falsey
  (js/symbol-with-ns? 'js/console.log) => falsey)

(fact "js-exp?"
  (js/js-exp? 'add) => false
  (js/js-exp? 'js/console) => false
  (js/js-exp? 'java.util.Set.) => false
  (js/js-exp? 'java.math.BigInteger/probablePrime) => false
  (js/js-exp? 'clojure.core/add) => false
  (js/js-exp? 'clojure.core) => true
  (js/js-exp? 'x.y/a.|b|.c) => true
  (js/js-exp? 'x.|y|.a) => true)

(fact "js-split-first"
  (js/js-split-first 'js/console.log) => '("js/console" ".log")
  (js/js-split-first 'a.b.c) => ["a" ".b.c"]
  (js/js-split-first 'a|b|.b.c) => nil
  (js/js-split-first 'js/console) => nil)

(fact "js-split-syms"
  (js/js-split-syms 'js/console.log) => ["js/console" "log"]
  (js/js-split-syms 'a.b.c/d.e.f) => ["a.b.c/d" "e" "f"]
  (js/js-split-syms 'a.b.c) => ["a" "b" "c"]
  (js/js-split-syms 'a.|b|.b.c) => ["a" "|b|" "b" "c"]
  (js/js-split-syms 'a|b|.b.c) => (throws Exception)
  (js/js-split-syms 'a.|b|./b.c) => (throws Exception)
  (js/js-split-syms 'a.|b|.c/b.c) => (throws Exception)
  (js/js-split-syms 'ns/a.|ns/b.c|) => ["ns/a" "|ns/b.c|"]
  (js/js-split-syms 'ns/b.c) => ["ns/b" "c"])

(fact "match"
  (match '(1 1) '(1 1)) => true
  (match '(1 1) '(%1 %1)) => true
  (match '(1 2) '(%1 %1)) => false
  (match '(let [x 1] (+ x 2))
            '(let [%x 1] (+ %x 2)))
  => true
  '(let [G__42879 (js-obj)]
     (aset G__42879 "a" (fn [] (? G__42879.val)))
     (aset G__42879 "val" 3) G__42879)
  =>
  (matches '(let [%x (js-obj)]
              (aset %x "a" (fn [] (? %x.val)))
              (aset %x "val" 3) %x)))

(defn expands-into [result]
  (fn [form]
    (match (macroexpand-1 form) result)))

;; Macros
(fact "!"
  '(js/! hello.there 10)
  => (expands-into
      '(purnam.cljs/aset-in hello ["there"] 10))

  '(js/! hello.there.again 10)
  => (expands-into
      '(purnam.cljs/aset-in hello ["there" "again"] 10))

  '(js/! a.|b|.c 10)
  => (expands-into
      '(purnam.cljs/aset-in a [b "c"] 10))

  '(js/! a.|b.c|.d 10)
  => (expands-into
      '(purnam.cljs/aset-in a [(purnam.cljs/aget-in b ["c"]) "d"] 10)))

(fact "!>"
  '(js/!> hello.lib.add 1 2 3 4 5)
  => (expands-into
      '(let [obj# (purnam.cljs/aget-in hello ["lib"])]
         (.add obj# 1 2 3 4 5))))

(fact "?"
  '(js/? hello.there)
  => (expands-into
      '(purnam.cljs/aget-in hello ["there"]))

  '(js/? hello.there.again)
  => (expands-into
      '(purnam.cljs/aget-in hello ["there" "again"])))

(fact "def.n"
  (macroexpand-1
   '(js/def.n app-func [p x]
      (if p.module.name
        x.one
        (x.func 1 2 3))))
  =>
  '(clojure.core/defn app-func [p x]
     (if (purnam.cljs/aget-in p ["module" "name"])
       (purnam.cljs/aget-in x ["one"])
       (let [obj# (purnam.cljs/aget-in x [])]
         (.func obj# 1 2 3)))))

(fact "has-root?"
  (js/has-root? 'hello 'hello) => true
  (js/has-root? 'hello 'NONE) => false
  (js/has-root? 'hello.there 'hello) => true
  (js/has-root? 'hello.there 'hello.there) => false
  (js/has-root? 'hello.there 'NONE) => false)

(fact "change-root"
  (js/change-root 'hello 'change) => 'change
  (js/change-root 'hello.there 'change) => 'change.there
  (js/change-root 'hello.there.again 'change) => 'change.there.again)


(fact "change-root"
  (js/change-root 'hello 'hello 'change) => 'change
  (js/change-root 'hello.there 'hello 'change) => 'change.there
  (js/change-root 'hello.there.again 'hello 'change) => 'change.there.again
  (js/change-root ['hello.there] 'hello 'change) => '[change.there]
  (js/change-root {:a 'hello.there} 'hello 'change) => '{:a change.there})

(fact "obj"
  (macroexpand-1
   '(obj :a 1  :fn (fn [] this.a)))
  => (matches '(let [%x (js-obj)]
                 (aset %x "a" 1)
                 (aset %x "fn"
                       (fn [] (purnam.cljs/aget-in %x ["a"])))
                 %x))

  (macroexpand-1
   '(obj :a 1
         :b  (obj :a 2
                  :fn (fn [] this.a))))
  => (matches '(let [%x (js-obj)]
                (aset %x "a" 1)
                (aset %x "b" (obj :a 2 :fn (fn [] this.a)))
                %x)))

