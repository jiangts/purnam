(ns purnam.test.common
 (:require [goog.object :as gobj]
           [goog.array :as garr]))
 
 (defn js-equals [v1 v2]
   (if (= v1 v2) true
       (let [t1 (js/goog.typeOf v1)
             t2 (js/goog.typeOf v2)]
         (cond (= "array" t1 t2)
               (garr/equals v1 v2 js-equals)

               (= "object" t1 t2)
               (let [ks1 (.sort (js-keys v1))
                     ks2 (.sort (js-keys v2))]
                 (if (garr/equals ks1 ks2)
                   (garr/every
                    ks1
                    (fn [k]
                      (js-equals (aget v1 k) (aget v2 k))))
                   false))
               :else
               false))))