(ns purnam.types.test-monad
  (:use [purnam.core :only [bind join return pure curry]]
        [purnam.native :only [js-concat]])
  (:use-macros [purnam.core :only [obj arr ! range* $> do>]]
               [purnam.test :only [fact facts]]))
               
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
 
 (bind (list 1 2 3) (list 4 5 6) (list 7 8 9) add) => (list 12 15 18)

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
 => {:a/increment 2 :b/increment 3 :c/increment 4}

 (bind {:a 1} {:a 2 :b 3} {:b 4 :c 5}
       (fn [& args] {:sum (apply + args)}))
 => {:a/sum 3 :b/sum 7 :c/sum 5}
 
 (bind (arr 1 2 3) increment)
 => (arr 2 3 4)
 
 (bind (obj :a 1) (obj :a 2 :b 3) (obj :b 4 :c 5)
       (fn [& args] (obj :sum (apply + args))))
 => (obj :a/sum 3 :b/sum 7 :c/sum 5))
 
(fact
 (join [[1 2] [3 [4 5] 6]]) => [1 2 3 [4 5] 6]

 (join (list (list 1 2) (list 3 (list 4 5) 6)))
 => (list 1 2 3 (list 4 5) 6)

 (join (seq (list (list 1 2) (list 3 (list 4 5) 6))))
 => (seq (list 1 2 3 (list 4 5) 6))

 (join (lazy-seq (list (list 1 2) (list 3 (list 4 5) 6))))
 => (lazy-seq (list 1 2 3 (list 4 5) 6))

 (join #{#{1 2} #{3 #{4 5} 6}}) => #{1 2 3 #{4 5} 6}

 (join {:a 1 :b {:c 2 :d {:e 3}}}) => {:a 1 :b/c 2 :b/d {:e 3}}
 
 (join (arr (arr 1 2 3) (arr 4 5) 6)) => (arr 1 2 3 4 5 6)
 
 (join (obj :a 1 :b {:c 2 :d {:e 3}})) => (obj :a 1 :b/c 2 :b/d {:e 3})
 
 )
 
(def returning-f (fn ([x]
                     (return (inc x)))
                 ([x & ys]
                    (return (apply + x ys)))))

(fact
  (bind (vec (range 1 500)) returning-f)
  => (range 2 501)

  (bind (vec (range 1 500))
        (vec (range 1 500))
        (vec (range 1 500))
        returning-f)
  => (range 3 1500 3)

  ;;(range* 10) => nil?
  (apply bind (concat (repeat 5 (array 1 2 3 4)) [returning-f]))
  => (array 5 10 15 20)

  (do> [a [1 2 3]] 
    (inc a)) => [2 3 4]

  (do> [a [1 2 3]
        b [4 5]
        c [6]]
    (* a b c))

  => (bind [1 2 3]
       (fn [a] 
         (bind [4 5] 
           (fn [b] 
             (bind [6]
               (fn [c]
                 (return (* a b c))))))))
                 

  (do> [a (array 1 2 3)
        b (array 4 5)]
    (* a b))
  => (array 4 5 8 10 12 15)
  
  (bind (array 1 2 3) (array 1 2 3) 
    (fn [a b] (bind (array 4 5 6)
                (fn [c] (return (+ a b c))))))
  => (array 6 7 8 8 9 10 10 11 12)
  
  (do> [a (array 1 2 3) | b (array 1 2 3)
        c (array 2 3 4)]
    (+ a b c))
  => (array 4 5 6 6 7 8 8 9 10)

  (.-length +) => 3
  
  ($> (curry +) 3 4 5)
  => 12

)