(ns purnam.common-test
  (:require [midje.sweet :refer :all]
            [purnam.common :as j]
            [purnam.checks :refer :all]))


(fact "aget-in-form"
  (j/aget-in-form 'dog []) => 'dog
  (j/aget-in-form 'dog ["leg"])
  => (matches
      '(if-let [%1 (aget dog "leg")]
         %1))
  (j/aget-in-form 'dog ["leg" "count"])
  => (matches
      '(if-let [%1 (aget dog "leg")]
         (if-let [%2 (aget %1 "count")]
           %2))))

(fact "nested-val-form"
  (j/nested-val-form ["a" "b"] 'hello)
  => (matches
      '(let [%1 (js-obj)]
         (aset %1 "a"
               (let [%2 (js-obj)]
                 (aset %2 "b" hello)
                 %2))
         %1)))

(fact "aset-in-form"
  (j/aset-in-form 'dog ["a"] "hello")
   => '(do (aset dog "a" "hello") dog)

  (j/aset-in-form 'dog ["a" "b"] "hello")
  => (matches
      '(do (if-let [%1 (aget dog "a")]
             (aset %1 "b" "hello")
             (aset dog "a"
                   (let [%2 (js-obj)]
                     (aset %2 "b" "hello")
                     %2)))
           dog))
  (j/aset-in-form 'dog ["a" "b" "c"] "hello")
  => (matches
      '(do (if-let [%1 (aget dog "a")]
            (if-let [%2 (aget %1 "b")]
              (aset %2 "c" "hello")
              (aset %1 "b"
                    (let [%3 (js-obj)]
                      (aset %3 "c" "hello") %3)))
            (aset dog "a"
                  (let [%4 (js-obj)]
                    (aset %4 "b"
                          (let [%5 (js-obj)]
                            (aset %5 "c" "hello")
                            %5))
                    %4)))
           dog)))

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

(defmacro test.macro [])

(fact "js-exp?"
  (j/js-exp? 'add) => false
  (j/js-exp? 'js/console) => false
  (j/js-exp? 'java.util.Set.) => false
  (j/js-exp? 'java.math.BigInteger/probablePrime) => false
  (j/js-exp? 'clojure.core/add) => false
  (j/js-exp? 'clojure.core) => true
  (j/js-exp? 'x/a.|b|.c) => true
  (j/js-exp? 'x.y/a.|b|.c) => true
  (j/js-exp? 'x.y.z/a.|b|.c) => true
  (j/js-exp? 'x.|y|.a) => true
  (j/js-exp? 'test.macro) => false
  (j/js-exp? 'purnam.common-test/test.macro) => false)

(fact "js-split-first"
  (j/js-split-first 'js/console.log) => '("js/console" ".log")
  (j/js-split-first 'a.b.c) => ["a" ".b.c"]
  (j/js-split-first 'a|b|.b.c) => nil
  (j/js-split-first 'js/console) => nil)

(fact "js-split-syms"
  (j/js-split-syms 'js/console.log) => ["js/console" "log"]
  (j/js-split-syms 'a.b.c/d.e.f) => ["a.b.c/d" "e" "f"]
  (j/js-split-syms 'a.b.c) => ["a" "b" "c"]
  (j/js-split-syms 'a.|b|.b.c) => ["a" "|b|" "b" "c"]
  (j/js-split-syms 'a|b|.b.c) => (throws Exception)
  (j/js-split-syms 'a.|b|./b.c) => (throws Exception)
  (j/js-split-syms 'a.|b|.c/b.c) => (throws Exception)
  (j/js-split-syms 'ns/a.|ns/b.c|) => ["ns/a" "|ns/b.c|"]
  (j/js-split-syms 'ns/b.c) => ["ns/b" "c"])


(fact "has-sym-root?"
  (j/has-sym-root? 'hello 'hello) => true
  (j/has-sym-root? 'hello 'NONE) => false
  (j/has-sym-root? 'hello.there 'hello) => true
  (j/has-sym-root? 'hello.there 'hello.there) => false
  (j/has-sym-root? 'hello.there 'NONE) => false
  (j/has-sym-root? 'hello.there 'NONE) => false)

(fact "change-root"
  (j/change-sym-root 'hello 'change) => 'change
  (j/change-sym-root 'hello.there 'change) => 'change.there
  (j/change-sym-root 'hello.there.again 'change) => 'change.there.again)

(fact "walk-and-transform"
  (j/walk-and-transform '(1 2 3 4) even? odd? inc)
  => '(1 3 3 5)

  (j/walk-and-transform '(a.b c.d a a)
                    #(j/has-sym-root? % #{'a})
                    ::none
                    (fn [x] 3))
  => '(3 c.d 3 3)

  (j/walk-and-transform '(a.b c.d a a)
                    #(j/has-sym-root? % {'a 'A.B})
                    ::none
                    (fn [x] (j/change-sym-root
                            x
                            ({'a 'A.B} (j/get-sym-root x)))))
  => '(A.B.b c.d A.B A.B))

(fact "change-roots"
  (j/change-roots 'hello 'hello 'change) => 'change
  (j/change-roots 'hello.there 'hello 'change) => 'change.there
  (j/change-roots 'hello.there.again 'hello 'change) => 'change.there.again
  (j/change-roots ['hello.there] 'hello 'change) => '[change.there]
  (j/change-roots {:a 'hello.there} 'hello 'change) => '{:a change.there})

(fact "change-roots-map"
  (j/change-roots-map 'hello {'hello 'change}) => 'change
  (j/change-roots-map 'hello.there {'hello 'change}) => 'change.there
  (j/change-roots-map 'hello.there.again {'hello 'change}) => 'change.there.again
  (j/change-roots-map ['hello.there] {'hello 'change}) => '[change.there]
  (j/change-roots-map {:a 'hello.there} {'hello 'change}) => '{:a change.there})
