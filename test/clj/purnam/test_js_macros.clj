(ns purnam.test-js-macros
  (:use midje.sweet
        purnam.checks
        [purnam.js :only [obj]])
  (:require [purnam.js :as js]))

(fact "split-syms"
  (js/split-syms 'hello) => ["hello"]
  (js/split-syms 'hello.there) => ["hello" "there"]
  (js/split-syms 'hello.there.again) => ["hello" "there" "again"]
  (js/split-syms 'js/console.log) => ["js/console" "log"]
  (js/split-syms 'purnam.cljs/test.method) => ["purnam.cljs/test" "method"]
  (js/split-syms 'purnam.cljs/test.module.a) => ["purnam.cljs/test" "module" "a"])

(fact "dotted-sym?"
  (js/dotted-sym? 'hello) => false
  (js/dotted-sym?  'hello.there) => true
  (js/dotted-sym?  'hello.there/js.n) => true
  (js/dotted-sym?  'purnam.cljs/aget) => false)

(fact "patch-dotted-sym"
  (js/patch-dotted-sym 'hello) =>  'hello
  (js/patch-dotted-sym 'hello.there) => '(purnam.cljs/aget-in hello ["there"])
  (js/patch-dotted-sym 'hello.there.again) => '(purnam.cljs/aget-in hello ["there" "again"])
  (js/patch-dotted-sym "hello.there") => "hello.there")

(fact "patch-dotted-syms"
  (js/patch-dotted-syms '[hello.there])
  => '[(purnam.cljs/aget-in hello ["there"])]
  (js/patch-dotted-syms '(hello.there))
  => '(let [obj# (purnam.cljs/aget-in hello [])]
        (.there obj#)))

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

;; Macros
(fact "!"
  (macroexpand-1 '(js/! hello.there 10))
  => '(purnam.cljs/aset-in hello ["there"] 10)

  (macroexpand-1 '(js/! hello.there.again 10))
  => '(purnam.cljs/aset-in hello ["there" "again"] 10))

(fact "!>"
  (macroexpand-1 '(js/!> hello.lib.add 1 2 3 4 5))
  => '(let [obj# (purnam.cljs/aget-in hello ["lib"])]
        (.add obj# 1 2 3 4 5)))

(fact "?"
  (macroexpand-1 '(js/? hello.there))
  => '(purnam.cljs/aget-in hello ["there"])

  (macroexpand-1 '(js/? hello.there.again))
  => '(purnam.cljs/aget-in hello ["there" "again"]))

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
  => '(let [G__52827 (js-obj)]
        (aset G__52827 "a" 1)
        (aset G__52827 "b" (obj :a 2 :fn (fn [] this.a)))
        G__52827))
