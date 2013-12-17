(ns purnam.types.test-curried
  (:use [purnam.core :only [fmap pure fapply op curry arities]]
        [purnam.native :only [js-type js-mapcat]])
  (:use-macros [purnam.core :only [obj arr ! f.n f.n> def.n def.n> f*n]]
               [purnam.test :only [fact facts]]))

(fact "setting stuff"
  (let [x (fn ([x] 1) ([]))
        _ (aset x "cljs$core$arity" "hello")]
    (.-cljs$core$arity x))
  => "hello")
          
(fact "f.n function"
  (def add (f.n ([] 0) ([n] n) ([n m] (+ n m))))
  (add) => 0
  (add 1) => 1
  (add 2 3) => 5
  (arities add) => [0 1 2]
  
  (def.n sub ([n] (- n)) ([n & more] (apply - n more)))
  (sub 1) => -1
  (sub 3 2) => 1
  (arities sub) => [1 [1]])

(fact "curried functions"
  (def.n> add [a b c] (+ a b c))
  (add 1 2 3) => 6
  (((add 1) 2) 3) => 6
  ((add 1 2) 3) => 6
  ((add 1) 2 3) => 6

  (def sub (f.n> [a b] (- a b)))
  (sub 1 2) => -1
  ((sub 1) 2) => -1)
  
