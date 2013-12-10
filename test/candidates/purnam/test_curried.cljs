(ns purnam.types.test-curried
  (:use [purnam.core :only [fmap pure fapply op curry]]
        [purnam.types.curried :only [curry arities]]
        [purnam.native :only [js-type js-mapcat]])
  (:use-macros [purnam.core :only [obj arr ! f.n def.n g.n f*n]]
               [purnam.test :only [fact facts]]))
          
(fact
  (let [x (fn ([x] 1) ([]))
        _ (aset x "cljs$core$arity" "hello")]
    (.-cljs$core$arity x))
  => "hello"
  
  ;;(def add (f.n ([] 0) ([n] n) ([n m] (+ n m))))
  ;;(add) => 0
  ;;(add 1) => 1
  ;;(add 2 3) => 5
  
  ;;(def.n sub ([n] (- n)) ([n m] (- n m)))
  ;;(sub 1) => -1
  ;;(sub 3 2) => 1
  ;;(obj :a 1 :b 2) => 2
  ;;(g.n 1 2 3) => 2
  ;;(f*n [] 1) => 1
  ;;(def add> (curry (fn [a b c] (+ a b c))))
  (def.n> add> [a b c] (+ a b c))
  (((add> 1) 2) 3) => 6
)
  
