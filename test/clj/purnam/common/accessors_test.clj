(ns purnam.common.accessors-test
  (:use midje.sweet)
  (:require [purnam.common.accessors :as j]
            [purnam.checks :refer :all]))

(fact "aget-in-form"
  (j/aget-in-form 'dog []) => 'dog

  (j/aget-in-form 'dog ["leg"])
  => (matches
      '(if-let [%1 (aget dog "leg")]
         %1))
  (j/aget-in-form 'dog ['leg])
  => (matches
      '(if-let [%1 (aget dog leg)]
         %1))

  (j/aget-in-form 'dog ["leg" "count"])
  => (matches
      '(if-let [%1 (aget dog "leg")]
         (if-let [%2 (aget %1 "count")]
           %2))))

(fact "nested-val-form"
  (j/nested-val-form ["a" "b"] 'hello)
  => (matches
      '(let [%1 (js* "{}")]
         (aset %1 "a"
               (let [%2 (js* "{}")]
                 (aset %2 "b" hello)
                 %2))
         %1)))

(fact "aset-in-form"
  (j/aset-in-form 'dog ["a"] "hello")
   => '(do (aset dog "a" "hello") dog)

  (j/aset-in-form 'dog ["a" "b"] "hello")
  => (matches
      '(do (if-let [%1 (aget dog "a")]
             (aset %1 "b" "hello")
             (aset dog "a"
                   (let [%2 (js* "{}")]
                     (aset %2 "b" "hello")
                     %2)))
           dog))
  (j/aset-in-form 'dog ["a" "b" "c"] "hello")
  => (matches
      '(do (if-let [%1 (aget dog "a")]
            (if-let [%2 (aget %1 "b")]
              (aset %2 "c" "hello")
              (aset %1 "b"
                    (let [%3 (js* "{}")]
                      (aset %3 "c" "hello") %3)))
            (aset dog "a"
                  (let [%4 (js* "{}")]
                    (aset %4 "b"
                          (let [%5 (js* "{}")]
                            (aset %5 "c" "hello")
                            %5))
                    %4)))
           dog)))

(j/adelete-in* 'a ["a"])
