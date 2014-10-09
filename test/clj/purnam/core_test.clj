(ns purnam.core-test
  (:use midje.sweet
        purnam.checks
        [purnam.core :only [obj]])
  (:require [purnam.core :as j]))

(fact "?"
  (macroexpand-1 '(j/? <OBJ>))
  => '<OBJ>

  (macroexpand-1 '(j/? <OBJ>.<V1>))
  => '(purnam.common/aget-in <OBJ> ["<V1>"])

  (macroexpand-1 '(j/?  <OBJ>.<V1>.<V2>))
  => '(purnam.common/aget-in <OBJ> ["<V1>" "<V2>"]))

(fact "?>"
  (macroexpand-1
   '(j/?> <FUNC> <OBJ>.<V1> <OBJ>.<V2> <VALUE>))
  => '(<FUNC> (purnam.common/aget-in <OBJ> ["<V1>"])
              (purnam.common/aget-in <OBJ> ["<V2>"]) <VALUE>))

;; Macros
(fact "!"
  (macroexpand-1 '(j/! <OBJ>.<V1> <VALUE>))
  => '(purnam.common/aset-in-obj <OBJ> ["<V1>"] <VALUE>)

  (macroexpand-1 '(j/! <OBJ>.<V1>.<V2> <VALUE>))
  => '(purnam.common/aset-in-obj <OBJ> ["<V1>" "<V2>"] <VALUE>)

  (macroexpand-1 '(j/! <OBJ>.|<V1>|.<V2> <VALUE>))
  => '(purnam.common/aset-in-obj <OBJ> [<V1> "<V2>"] <VALUE>)

  (macroexpand-1 '(j/! <OBJ>.|<V1>.<V2>|.<V3> <VALUE>))
  => '(purnam.common/aset-in-obj
       <OBJ>
       [(purnam.common/aget-in <V1> ["<V2>"]) "<V3>"] <VALUE>))

(fact "!>"
  (macroexpand-1
   '(j/!> <OBJ>.<FN> <ARG1> <ARG2> <ARG3>))
  => '(let [obj# (purnam.common/aget-in <OBJ> [])
            fn# (aget obj# "<FN>")]
        (.call fn# obj# <ARG1> <ARG2> <ARG3>))

  (macroexpand-1  '(j/!> <OBJ>.<V1>.<FN> <ARG1> <ARG2> <ARG3>))
  => '(let [obj# (purnam.common/aget-in <OBJ> ["<V1>"])
            fn# (aget obj# "<FN>")]
        (.call fn# obj# <ARG1> <ARG2> <ARG3>)))

(fact "f.n"
  (macroexpand-1
   '(j/f.n ([] 0) ([n] n) ([n m] (+ n m))))
  => '(let [f# (clojure.core/fn ([] 0) ([n] n) ([n m] (+ n m)))]
        (aset f# "cljs$arities" [0 1 2])
        f#))

(fact "def.n"
  (macroexpand-1
   '(j/def.n <FUNCTION> [<ARG1> <ARG2>]
      (if <ARG1>.<V1>.<V2>
        <ARG2>.<W1>
        (<ARG2>.<FN> <X> <Y> <Z>))))
  => '(def <FUNCTION>
       (let [f# (clojure.core/fn
                  ([<ARG1> <ARG2>]
                     (if (purnam.common/aget-in <ARG1> ["<V1>" "<V2>"])
                       (purnam.common/aget-in <ARG2> ["<W1>"])
                       (let [obj# (purnam.common/aget-in <ARG2> [])
                             fn# (aget obj# "<FN>")]
                         (.call fn# obj# <X> <Y> <Z>)))))]
         (aset f# "cljs$arities" [2])
         f#)))


(fact "obj"
  (macroexpand-1
  '(obj :<K1> <X1>  :<FN> (fn [] (+ self.<X>
                                   this.<Y>))))
 => (matches '(let [%x (js-obj)]
               (aset %x "<K1>" <X1>)
               (aset %x "<FN>"
                     (fn [] (+ (purnam.common/aget-in %x ["<X>"])
                              (purnam.common/aget-in (js* "this") ["<Y>"]))))
               %x)))

(fact "obj self"
 (macroexpand-1
  '(obj :<A> <X1>
        :<B> (obj :<A> <X2>
                  :<FN> (fn [] self.<A>))))
 => (matches
     '(let [%x (js-obj)]
       (aset %x "<A>" <X1>)
       (aset %x "<B>" (obj :<A> <X2> :<FN> (fn [] self.<A>))) %x)))

(fact "arr"
  (macroexpand-1
   '(j/arr 1 2 <OBJ>.<X>))
  => '(array 1 2 (purnam.common/aget-in <OBJ> ["<X>"])))

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
         <Y> (purnam.core/obj :<W1> <W2>)
         <Z> (purnam.core/obj <W1> <W2>)]
     (purnam.core/obj <X> (array <X> <Y>)))))
