(ns purnam.test-js
  (:use midje.sweet
        purnam.checks
        [purnam.js :only [obj]])
  (:require [purnam.js :as j]))

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

(fact "js-exp?"
  (j/js-exp? 'add) => false
  (j/js-exp? 'js/console) => false
  (j/js-exp? 'java.util.Set.) => false
  (j/js-exp? 'java.math.BigInteger/probablePrime) => false
  (j/js-exp? 'clojure.core/add) => false
  (j/js-exp? 'clojure.core) => true
  (j/js-exp? 'x.y/a.|b|.c) => true
  (j/js-exp? 'x.|y|.a) => true)

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


(fact "?"
  '(j/? <OBJ>)
  => (expands-into '<OBJ>)

  '(j/? <OBJ>.<V1>)
  => (expands-into
      '(purnam.cljs/aget-in <OBJ> ["<V1>"]))

  '(j/?  <OBJ>.<V1>.<V2>)
  => (expands-into
      '(purnam.cljs/aget-in <OBJ> ["<V1>" "<V2>"])))

(fact "?>"
  (macroexpand-1
   '(j/?> <FUNC> <OBJ>.<V1> <OBJ>.<V2> <VALUE>))

  => '(<FUNC> (purnam.cljs/aget-in <OBJ> ["<V1>"])
              (purnam.cljs/aget-in <OBJ> ["<V2>"])
              <VALUE>))

;; Macros
(fact "!"
  '(j/! <OBJ>.<V1> <VALUE>)
  => (expands-into
      '(purnam.cljs/aset-in <OBJ> ["<V1>"] <VALUE>))

  '(j/! <OBJ>.<V1>.<V2> <VALUE>)
  => (expands-into
      '(purnam.cljs/aset-in <OBJ> ["<V1>" "<V2>"] <VALUE>))

  '(j/! <OBJ>.|<V1>|.<V2> <VALUE>)
  => (expands-into
      '(purnam.cljs/aset-in <OBJ> [<V1> "<V2>"] <VALUE>))

  '(j/! <OBJ>.|<V1>.<V2>|.<V3> <VALUE>)
  => (expands-into
      '(purnam.cljs/aset-in
        <OBJ>
        [(purnam.cljs/aget-in <V1> ["<V2>"]) "<V3>"] <VALUE>)))

(fact "!>"
  (macroexpand-1
   '(j/!> <OBJ>.<FN> <ARG1> <ARG2> <ARG3>))
  => '(let [obj# (purnam.cljs/aget-in <OBJ> [])
            fn# (aget obj# "<FN>")]
        (.call fn# obj# <ARG1> <ARG2> <ARG3>))

  (macroexpand-1  '(j/!> <OBJ>.<V1>.<FN> <ARG1> <ARG2> <ARG3>))
  => '(let [obj# (purnam.cljs/aget-in <OBJ> ["<V1>"])
            fn# (aget obj# "<FN>")]
        (.call fn# obj# <ARG1> <ARG2> <ARG3>)))


(fact "def.n"
  (macroexpand-1
   '(j/def.n <FUNCTION> [<ARG1> <ARG2>]
      (if <ARG1>.<V1>.<V2>
        <ARG2>.<W1>
        (<ARG2>.<FN> <X> <Y> <Z>))))
  =>
  '(clojure.core/defn <FUNCTION> [<ARG1> <ARG2>]
     (if (purnam.cljs/aget-in <ARG1> ["<V1>" "<V2>"])
       (purnam.cljs/aget-in <ARG2> ["<W1>"])
       (let [obj# (purnam.cljs/aget-in <ARG2> [])
             fn# (aget obj# "<FN>")]
         (.call fn# obj# <X> <Y> <Z>)))))

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

(fact "obj")

(fact "obj"
  (macroexpand-1
   '(obj :<K1> <X1>  :<FN> (fn [] (+ self.<X>
                                    this.<Y>))))
  => (matches '(let [%x (js-obj)]
                (aset %x "<FN>"
                      (fn [] (+ (purnam.cljs/aget-in %x ["<X>"])
                               (purnam.cljs/aget-in (js* "this") ["<Y>"]))))
                (aset %x "<K1>" <X1>)
                %x)))

(fact "obj self"
  (macroexpand-1
   '(obj :<A> <X1>
         :<B> (obj :<A> <X2>
                   :<FN> (fn [] self.<A>))))
  => (matches
      '(let [%x (js-obj)]
        (aset %x "<B>" (obj :<A> <X2> :<FN> (fn [] self.<A>)))
        (aset %x "<A>" <X1>) %x)))

(fact "def*"
  (macroexpand-1
   '(j/def* <NAME>
      (let [<X> [<V1> <V2>]
            <Y> {:<W1> <W2>}
            <Z> {<W1> <W2>}]
        {<X> [<X> <Y>]})))
  =>
  '(def <NAME>
    (let [<X> (array <V1> <V2>)
          <Y> (obj :<W1> <W2>)
          <Z> (obj <W1> <W2>)]
      (obj <X> (array <X> <Y>)))))

(fact "def.n*"
  (macroexpand-1
   '(j/def.n* <NAME> [<ARG1> <ARG2>]
      (let [<X> [<V1> <V2>]
            <Y> {:<W1> <W2>}
            <Z> {<W1> <W2>}]
        {<X> [<X> <Y>]})))
  =>
  '(clojure.core/defn <NAME> [<ARG1> <ARG2>]
    (let [<X> (array <V1> <V2>)
          <Y> (obj :<W1> <W2>)
          <Z> (obj <W1> <W2>)]
      (obj <X> (array <X> <Y>)))))


(fact "fn*"
  (macroexpand-1
   '(j/fn* [o] (! o.val [1 2 3 4 5])))
  => '(clojure.core/fn [o] (! o.val (array 1 2 3 4 5)))




  (macroexpand-1
   '(j/fn* [<ARG1> <ARG2>]
     (let [<X> [<ARG1>.<V1> <V2>]
           <Y> {:<W1> this.<W2>}
           <Z> {:<W1> self.<W2>}]
       {<X> [<Y> <Z>]})))
  =>
  '(clojure.core/fn [<ARG1> <ARG2>]
     (let [<X> (array (purnam.cljs/aget-in <ARG1> ["<V1>"]) <V2>)
           <Y> (obj :<W1> this.<W2>)
           <Z> (obj :<W1> self.<W2>)]
       (obj <X> (array <Y> <Z>)))))
