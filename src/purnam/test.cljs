(ns purnam.test
  (:require [purnam.test.common :refer [js-equals]]))

(if-not (.-jasmine js/window)
    (throw (ex-info "No Jasmine Framework Library Installed. Tests Cannot Proceed" {})))

(defn trim-quote [s]
 (second (re-find #"^\'(.*)\'$" s)))

(js/beforeEach
     (fn []
       (.addMatchers (js* "this")
         (let [o (js-obj)]
           (aset o "toSatisfy"
             (fn [expected tactual texpected]
               (let [actual (.-actual (js* "this"))
                     actualText (str actual)
                     actualText (if (= actualText "[object Object]")
                                   (let [ks (js/goog.object.getKeys actual)
                                         vs (js/goog.object.getValues actual)]
                                     (into {} (map (fn [x y] [x y])
                                                 ks vs)))
                                   actualText)
                     notText (if (.-isNot (js* "this")) "Not " "")]
                 (aset (js* "this") "message"
                       (fn []
                         (str "Expression: " (trim-quote tactual)
                              "\n  Expected: " notText (trim-quote texpected)
                              "\n  Result: " actualText)))
                 (cond (= (js/goog.typeOf expected) "array")
                       (js-equals expected actual)

                       (fn? expected)
                       (expected actual)

                       :else
                       (or (= expected actual)
                           (js-equals expected actual))))))
            o))))

