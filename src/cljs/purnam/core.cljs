(ns purnam.core
  (:require [purnam.native :refer [js-map]]
            [purnam.common :refer [get-context]]
            [purnam.types.clojure]
            [purnam.types.functor]
            [purnam.types.applicative]
            [purnam.types.magma]
            [purnam.types.monoid]
            [purnam.types.foldable]
            [purnam.types.monad]
            [purnam.types.curried]
            [purnam.types.maybe]
            [purnam.protocols :as p])
  (:use-macros [purnam.test :only [init]]
               [purnam.types.macros :only [with-context]]
               [purnam.core :only [import-from]]))
  
#_(import-from purnam.native [js-map])  
  
(defn fmap 
  ([f]
    (if (= identity f) f
        (fn [functor & more]
          (apply fmap f functor more))))
  ([f functor]
    (p/fmap functor f))
  ([f functor & more]
    (p/fmap functor f more)))
    
(defn pure
  ([applicative]
     #(p/pure applicative %))
  ([applicative x]
     (p/pure applicative x)))

(defn fapply
  ([af]
    (fn [av & avs]
      (apply fapply af av avs)))
  ([af av]
    (p/fapply af av))
  ([af av & avs]
    (p/fapply af av avs)))

(defn op 
  ([x y]
     (p/op x y))
  ([x y & ys]
     (p/op x y ys)))

(defn id
 [x]
 (p/id x))

(defn fold
 [fd]
 (p/fold fd))

(defn foldmap
 ([f]
    (fn [fd]
      (p/foldmap fd f)))
 ([f fd]
    (p/foldmap fd f)))

(defn <*>
  ([af]
     (fn [a & as]
       (apply <*> af a as)))
  ([af av]
     (p/fapply af av))
  ([af av & avs]
     (reduce p/fapply af (cons av avs))))

(defn join
  [monadic]
  (p/join monadic))

(defn return
  [x]
  (p/pure (get-context) x))

(def unit return)

(defn bind
  ([f]
     (fn [monadic & ms]
       (apply bind monadic f ms)))
  ([monadic f]
     (with-context monadic
       (p/bind monadic f)))
  ([monadic monadic2 & args]
     (with-context monadic
       (p/bind monadic (last args)
               (cons monadic2 (butlast args))))))

(defn >>=
  ([monadic]
    (fn [f & fs]
      (apply >>= monadic f fs) ))
  ([monadic f]
    (bind monadic f))
  ([monadic f & fs]
    (reduce bind monadic (cons f fs))))

(defn >=>
  ([f]
     (fn [g & gs]
       (apply >=> f g gs)))
  ([f g]
     (fn
       ([x]
          (bind (f x) g))
       ([x & xs]
          (bind (apply f x xs) g))))
  ([f g & hs]
     (fn
       ([x]
          (apply >>= (f x) g hs))
       ([x & xs]
          (apply >>= (apply f x xs) g hs)))))

(defn <=<
  ([f]
     (fn [g & gs]
       (apply <=< f g gs)))
  ([f g]
     (>=> g f))
  ([f g & hs]
     (apply >=> (reverse (into [f g] hs)))))


(if (.-jasmine js/window)
  (init))