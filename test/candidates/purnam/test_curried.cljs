(ns purnam.types.test-curried
  (:use [purnam.core :only [fmap pure fapply op]]
        [purnam.types.curried :only [curry arities]]
        [purnam.native :only [js-type js-mapcat]])
  (:use-macros [purnam.core :only [obj arr !]]
               [purnam.test :only [fact facts]]))
               
(fact 1 => 1
  ;;(arities (fn [])) => 1
  
  (let [x (fn ([x] 1) ([]))]
    (.-cljs$core$IFn$_invoke$arity$1 x))
  => 1
  
  )
  
