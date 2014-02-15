(ns lyah.chapter-12
  (:use [purnam.core :only [fmap <*> just curry]]
        [purnam.native :only [js-concat]])
  (:use-macros [purnam.core :only [obj arr ! range* $> do>]]
               [purnam.test :only [fact facts]]))

(fact
  (<*> (fmap (curry 2 *) (just 2)) (just 8))
  => (just 16)

  (fmap * (just 2) (just 8))
  => (just 16)

  (<*> (fmap (curry str) (just "klingon")) nil)
  => nil?

  (<*> (fmap (curry 2 -) [3 4]) [1 2 3])
  => [2 1 0 3 2 1]
)

(fact

  (fmap #(str % "!") (just "wisdom"))
  => (just "wisdom!")

  (fmap (partial str "!") nil)
  => nil?

  (<*> (just (partial + 3)) (just 3))
  => (just 6)

  (<*> nil (just "greed"))
  => nil?

  (<*> (just +) nil)
  => nil?

  (fmap max (just 3) (just 6) (just 9))
  => (just 9)

  (fmap max (just 3) nil)
  => nil?

  ((fn [x] (just (+ x 1))) 1)
  => (just 2)

  ((fn [x] (just (+ x 100))) 1)
  => (just 101)
  )