(ns purnam.core
  (:require [purnam.cljs]
            [purnam.types.clojure]
            [purnam.types.functor]
            [purnam.types.applicative]
            [purnam.types.magma]
            [purnam.protocols :as p]))

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
