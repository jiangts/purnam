(ns purnam.types.test-monad
  (:use [purnam.core :only [bind join return]])
  (:use-macros [purnam.js :only [obj arr !]]
               [purnam.test.sweet :only [fact facts]]))
               
(facts
 "Data structures: Monad"

 (def increment (comp return inc))
 (def add (comp return +))

 (bind [] increment) => []

 (bind [1 2 3] increment) => [2 3 4]

 (bind [1 2 3] [4 5 6] add) => [5 7 9]

 (bind (list) increment) => (list)

 (bind (list 1 2 3) increment) => (list 2 3 4)

 (bind (list 1 2 3) (list 4 5 6) add) => (list 5 7 9)

 (bind (empty (seq [2])) increment)
 => (empty (seq [3]))

 (bind (seq [1 2 3]) increment) => (seq [2 3 4])

 (bind (seq [1 2 3]) (seq [4 5 6]) add)
 => (seq [5 7 9])

 (bind (lazy-seq []) increment)
 => (lazy-seq [])

 (bind (lazy-seq [1 2 3]) increment)
 => (lazy-seq [2 3 4])

 (bind (lazy-seq [1 2 3]) (lazy-seq [4 5 6]) add)
 => (lazy-seq [5 7 9])

 (bind #{} increment) => #{}

 (bind #{1 2 3} increment) => #{2 3 4}

 (bind #{1 2 3} #{4 5 6} add) => #{5 7 9}

 (bind {} increment) => {}

 (bind {:a 1} increment)
 => {:a 2}
 
 (bind {:a 1} increment)
 => {:a 2}

 (bind {:a 1  :b 2 :c 3} #(hash-map :increment (inc %)))
 => {[:a :increment] 2 [:b :increment] 3 [:c :increment] 4}

 (bind {:a 1} {:a 2 :b 3} {:b 4 :c 5}
       (fn [& args] {:sum (apply + args)}))
 => {[:a :sum] 3 [:b :sum] 7 [:c :sum] 5})
 
(fact
 (join [[1 2] [3 [4 5] 6]]) => [1 2 3 [4 5] 6]

 (join (list (list 1 2) (list 3 (list 4 5) 6)))
 => (list 1 2 3 (list 4 5) 6)

 (join (seq (list (list 1 2) (list 3 (list 4 5) 6))))
 => (seq (list 1 2 3 (list 4 5) 6))

 (join (lazy-seq (list (list 1 2) (list 3 (list 4 5) 6))))
 => (lazy-seq (list 1 2 3 (list 4 5) 6))

 (join #{#{1 2} #{3 #{4 5} 6}}) => #{1 2 3 #{4 5} 6}

 (join {:a 1 :b {:c 2 :d {:e 3}}}) => {:a 1 [:b :c] 2 [:b :d] {:e 3}})