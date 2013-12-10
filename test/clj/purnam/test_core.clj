(ns purnam.test-core
  (:use midje.sweet
        purnam.checks
        [purnam.core :only [obj]])
  (:require [purnam.core :as j]))



(fact "?"
  '(j/? <OBJ>)
  => (expands-into '<OBJ>)

  '(j/? <OBJ>.<V1>)
  => (expands-into
      '(purnam.native/aget-in <OBJ> ["<V1>"]))

  '(j/?  <OBJ>.<V1>.<V2>)
  => (expands-into
      '(purnam.native/aget-in <OBJ> ["<V1>" "<V2>"])))

(fact "?>"
  (macroexpand-1
   '(j/?> <FUNC> <OBJ>.<V1> <OBJ>.<V2> <VALUE>))

  => '(<FUNC> (purnam.native/aget-in <OBJ> ["<V1>"])
              (purnam.native/aget-in <OBJ> ["<V2>"])
              <VALUE>))

;; Macros
(fact "!"
  '(j/! <OBJ>.<V1> <VALUE>)
  => (expands-into
      '(purnam.native/aset-in <OBJ> ["<V1>"] <VALUE>))

  '(j/! <OBJ>.<V1>.<V2> <VALUE>)
  => (expands-into
      '(purnam.native/aset-in <OBJ> ["<V1>" "<V2>"] <VALUE>))

  '(j/! <OBJ>.|<V1>|.<V2> <VALUE>)
  => (expands-into
      '(purnam.native/aset-in <OBJ> [<V1> "<V2>"] <VALUE>))

  '(j/! <OBJ>.|<V1>.<V2>|.<V3> <VALUE>)
  => (expands-into
      '(purnam.native/aset-in
        <OBJ>
        [(purnam.native/aget-in <V1> ["<V2>"]) "<V3>"] <VALUE>)))

(fact "!>"
  (macroexpand-1
   '(j/!> <OBJ>.<FN> <ARG1> <ARG2> <ARG3>))
  => '(let [obj# (purnam.native/aget-in <OBJ> [])
            fn# (aget obj# "<FN>")]
        (.call fn# obj# <ARG1> <ARG2> <ARG3>))

  (macroexpand-1  '(j/!> <OBJ>.<V1>.<FN> <ARG1> <ARG2> <ARG3>))
  => '(let [obj# (purnam.native/aget-in <OBJ> ["<V1>"])
            fn# (aget obj# "<FN>")]
        (.call fn# obj# <ARG1> <ARG2> <ARG3>)))

(fact "f.n"
  (macroexpand-1
   '(j/f.n ([] 0) ([n] n) ([n m] (+ n m))))
  => '(let [f# (clojure.core/fn ([] 0) ([n] n) ([n m] (+ n m)))]
        (aset f# "cljs$arities" [0 1 2]) f#))


(fact "f.n>"
  (macroexpand-1
   '(j/f.n> [a b c] (+ a b c)))
  =>
  '(purnam.core/curry
    (let [f# (clojure.core/fn ([a b c] (+ a b c)))]
      (aset f# "cljs$arities" [3]) f#)))


(fact "def.n"
  (macroexpand-1
   '(j/def.n <FUNCTION> [<ARG1> <ARG2>]
      (if <ARG1>.<V1>.<V2>
        <ARG2>.<W1>
        (<ARG2>.<FN> <X> <Y> <Z>))))
  =>
  '(def <FUNCTION>
     (let [f# (clojure.core/fn
                ([<ARG1> <ARG2>]
                   (if (purnam.native/aget-in <ARG1> ["<V1>" "<V2>"])
                     (purnam.native/aget-in <ARG2> ["<W1>"])
                     (let [obj# (purnam.native/aget-in <ARG2> []) fn#
                           (aget obj# "<FN>")]
                       (.call fn# obj# <X> <Y> <Z>)))))]
       (aset f# "cljs$arities" [2]) f#)))

(fact "property"
 (macroexpand-1 '(j/property <A>.<B>.<C>))
 => '(clojure.core/fn
         ([] (purnam.core/? <A>.<B>.<C>))
         ([v] (clojure.core/cond
                (clojure.core/= "object"
                (js/goog.typeOf (purnam.core/? <A>.<B>.<C>))
                (js/goog.typeOf v))
              (purnam.native/js-replace (purnam.core/? <A>.<B>.<C>) v)
              :else (purnam.core/! <A>.<B>.<C> v))))
 (macroexpand-1 '(j/property <A>.<B>.<C> true))
 => '(clojure.core/fn
       ([] (purnam.core/? <A>.<B>.<C>))
       ([v] (throw (js/Error "<A>.<B>.<C> is readonly")))))


(fact "obj"
 (macroexpand-1
  '(obj :<K1> <X1>  :<FN> (fn [] (+ self.<X>
                                   this.<Y>))))
 => (matches '(let [%x (js-obj)]
               (aset %x "<FN>"
                     (fn [] (+ (purnam.native/aget-in %x ["<X>"])
                              (purnam.native/aget-in (js* "this") ["<Y>"]))))
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

(fact "def*n"
 (macroexpand-1
  '(j/def*n <NAME> [<ARG1> <ARG2>]
     (let [<X> [<V1> <V2>]
           <Y> {:<W1> <W2>}
           <Z> {<W1> <W2>}]
       {<X> [<X> <Y>]})))
 =>
 '(def <NAME> (let [f# (clojure.core/fn
                         ([<ARG1> <ARG2>]
                            (let [<X> (array <V1> <V2>)
                                  <Y> (obj :<W1> <W2>)
                                  <Z> (obj <W1> <W2>)]
                              (obj <X> (array <X> <Y>)))))]
                (aset f# "cljs$arities" [2]) f#)))

(fact "f*n"
 (macroexpand-1
  '(j/f*n [o] (! o.val [1 2 3 4 5])))
 =>
 '(let [f# (clojure.core/fn
             ([o] (! o.val (array 1 2 3 4 5))))]
    (aset f# "cljs$arities" [1]) f#)

 (macroexpand-1
  '(j/f*n [<ARG1> <ARG2>]
    (let [<X> [<ARG1>.<V1> <V2>]
          <Y> {:<W1> this.<W2>}
          <Z> {:<W1> self.<W2>}]
      {<X> [<Y> <Z>]})))
 =>
 '(let [f# (clojure.core/fn
             ([<ARG1> <ARG2>]
                (let [<X> (array (purnam.native/aget-in <ARG1> ["<V1>"]) <V2>)
                      <Y> (obj :<W1> this.<W2>)
                      <Z> (obj :<W1> self.<W2>)]
                  (obj <X> (array <Y> <Z>)))))]
    (aset f# "cljs$arities" [2]) f#)

)
