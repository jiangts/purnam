(ns purnam.hs.test-hs-types
  (:use [purnam.core :only [fmap pure fapply op]]
        [purnam.cljs :only [js-type js-mapcat]])
  (:use-macros [purnam.js :only [obj arr !]]
               [purnam.test :only [init]]
               [purnam.test.sweet :only [fact facts]]))

(init)

#_(fact
  @(pure (atom nil) 1) => 1

  (pure #{1 2 3} 1) => #{1}
  ;;(pure nil nil) => nil
  )

#_(fact
  (fapply [] []) => []

  (fapply [] [1 2 3]) => []

  (fapply [inc dec] []) => []

  (fapply [inc dec] [1 2 3]) => [2 3 4 0 1 2]

  (fapply [+ *] [1 2 3] [4 5 6]) => [5 7 9 4 10 18]

  (fapply (list) (list)) => (list)

  (fapply (list) (list 1 2 3)) => (list)

  (fapply (list inc dec) (list)) => (list)

  (fapply (list inc dec) (list 1 2 3))
  => (list 2 3 4 0 1 2)

  (fapply (list + *) (list 1 2 3) (list 4 5 6))
  => (list 5 7 9 4 10 18)

  (fapply (empty (seq [2])) (empty (seq [3])))
  => (empty (seq [1]))

  (fapply (empty (seq [33])) (seq [1 2 3]))
  => (empty (seq [44]))

  (fapply (seq [inc dec]) (empty (seq [1])))
  => (empty (seq [3]))

  (fapply (seq [inc dec]) (seq [1 2 3]))
  => (seq [2 3 4 0 1 2])

  (fapply (seq [+ *]) (seq [1 2 3]) (seq [4 5 6]))
  => (seq [5 7 9 4 10 18])

  (fapply (lazy-seq []) (lazy-seq []))
  => (lazy-seq [])

  (fapply (lazy-seq []) (lazy-seq [1 2 3]))
  => (lazy-seq [])

  (fapply (lazy-seq [inc dec]) (lazy-seq []))
  => (lazy-seq [])

  (fapply (lazy-seq [inc dec]) (lazy-seq [1 2 3]))
  => (lazy-seq [2 3 4 0 1 2])

  (fapply (lazy-seq [+ *])
          (lazy-seq [1 2 3])
          (lazy-seq [4 5 6]))
  => (lazy-seq [5 7 9 4 10 18])

  (fapply #{} #{}) => #{}

  (fapply #{} #{1 2 3}) => #{}

  (fapply #{inc dec} #{}) => #{}

  (fapply #{inc dec} #{1 2 3}) => #{2 3 4 0 1}

  (fapply #{+ *} #{1 2 3} #{4 5 6}) => #{5 7 9 4 10 18}

  (fapply {} {}) => {}

  (fapply {} {:a 1 :b 2 :c 3}) => {:a 1 :b 2 :c 3}

  (fapply {:a inc} {}) => {}

  (fapply {:a inc :b dec nil (partial * 10)}
          {:a 1 :b 2 :c 3 :d 4 nil 5})
  => {:a 2 :b 1 :c 30 :d 40 nil 50}

  (fapply {:a + :b * :c /} 
    {:a 1 :c 2} 
    {:a 3 :b 4} 
    {:c 2 :d 5})
  => {:a 4 :b 4 :c 1 :d 5}
  
  
  
  ;;(fapply (first {nil inc}) (first {nil 1}))
  ;;=> (first {nil 2})
  
  (js/console.log #_(first {nil 2})
    (js-type (first (seq {:a 2}))))
    
  (comment

    (fapply (first {:a inc}) (first {:b 1}))
    => (first {:b 1})

    (fapply (first {:a inc}) (first {:a 1}))
    => (first {:a 2})

    (fapply (first {nil inc}) (first {:a 1}))
    => (first {:a 2}))

)


(fact
  (op nil 1) => 1  
  
  (fmap + (arr 1 2 3 4) '(1 2 3 4))
  => (arr 2 4 6 8)

  (fmap + '(1 2 3 4) (arr 1 2 3 4))
  => '(2 4 6 8)

  (fmap + [1 2 3 4] (arr 1 2 3 4))
  => [2 4 6 8]
  
  (fapply (arr dec inc #(* 2 %)) (arr 1 2 3 4))
  => (arr 0 1 2 3 2 3 4 5 2 4 6 8)
  
  (fapply (arr dec inc #(* 2 %)) [1 2 3 4])
  => (arr 0 1 2 3 2 3 4 5 2 4 6 8)) 


(fact
  (fmap inc (cons 1 ())) => '(2)
  (fmap inc []) => []

  (fmap inc [1 2 3]) => [2 3 4]

  (fmap + [1 2] [3 4 5] [6 7 8]) => [10 13]

  (fmap inc (list)) => (list)

  (fmap inc (list 1 2 3)) => (list 2 3 4))

#_(fact ""
  (fmap inc [1 2 3]) => [2 3 4]
  (fmap inc #{1 2 3}) => #{2 3 4}
  (fmap + (arr 1 2 3) (arr 1 2 3) (arr 1 2 3)) => (arr 3 6 9)
  (fmap #(apply str "A" %&) "b" "c" "d") => "Abcd"
  
  (fmap inc {:a 1}) => {:a 2}
  (fmap + {:a 1} {:b 1}) => {:a 1 :b 1}
  
  (fmap inc '(1 2 3)) => '(2 3 4)
  ((fmap str +) 1 2 3) => "6"
  
  @(fmap inc (atom 1)) => 2)

