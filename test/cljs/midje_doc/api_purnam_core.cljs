(ns midje-doc.api-purnam-core
  (:require [purnam.test])
  (:use-macros [purnam.common :only [set-safe-aget]]
               [purnam.core :only [? ?> ! !> f.n def.n do.n
                                 obj arr def* do*n def*n f*n]]
               [purnam.test :only [fact facts]]))

(set-safe-aget true)

[[:section {:title "init" :tag "init-core"}]]

"`purnam.core` extensions are packaged as macros. They are accessible via `:use-macro` declaration."

(comment
  (:use-macros [purnam.core :only [? ?> ! !> f.n def.n do.n
                                  obj arr def* do*n def*n f*n]]))

[[:section {:title "obj"}]]

(facts  [[{:doc "obj"}]]
  
  "Raw js objects are constructed in clojurescript with `obj`:"
  
  (obj "key1" "val1" "key2" "val2")
  => (js* "{key1: 'val1', key2: 'val2'}")

  "Keywords can be used instead of strings for improved legibility. The previous example can also be written as:"

  (obj :key1 "val1" :key2 "val2")
  => (js* "{key1: 'val1', key2: 'val2'}")

  "Symbol are evalutated. This will produce an equivalent object to the previous examples:"
  
  (let [s1 "key1"
        s2 "key2"]
    (obj s1 "val1" s2 "val2"))
  => (js* "{key1: 'val1', key2: 'val2'}")
  
  "Note that the symbols have to represent strings to get the same output as previous. The following WILL NOT construct the equivalent object as before:"

  (let [s1 :key1
        s2 :key2]
    (obj s1 "val1" s2 "val2"))
  => #(not (= % (js* "{key1: 'val1', key2: 'val2'}")))
  
  "The `obj` form can be used to set up nested js objects and arrays primitives. Using `[]` with the form will create a new js array, `{}` will create a new js object."
    
  (obj :data [{:id 1 :name "one"}
              {:id 2 :name "two"}])
  => (js* "{data: [{id:1,name:'one'},
                   {id:2,name:'two'}]}")
 
  "The nesting notation alleviates the use of the clj->js transform for constructing large javascipt variables. In this way, deeply nested javascript object structures can be created in clojurescript in the same way clojure maps and arrays are created."
                   
  (obj :name "l1" :data [1 2 3]
       :next {:name "l2" :data [4 5 6]
              :next {:name "l3" :data [7 8 9]}})
  => (js* "{name: 'l1',
            data: [1,2,3],
            next: {name: 'l2',
                   data: [4,5,6],
                   next: {name: 'l3',
                          data: [7,8,9]}}}"))

[[:section {:title "arr"}]]

(facts [[{:doc "arr"}]]
 
  "`arr` constructs a javascript array primitive, the same way as `array`"
  (arr 1 2 3 4 5)
  => (js* "[1,2,3,4,5]")

  "`arr` supports nesting of native objects and arrays much like `obj`"
  (arr {:data [1 2 3 4 5]}
       {:data [6 7 8 9 10]})
   => (js* "[{data: [1,2,3,4,5]},
              {data: [6,7,8,9,10]}]"))

[[:section {:title "getter - ?" :tag "getter"}]]

(facts [[{:doc "? - getter"}]]

  "`?` provides javascript-like dot notation access for objects"
  (let [o (obj :a 1 :b 2 :c 3)]
    (+ (? o.a) (? o.b) (? o.c))) 
  => 6

  "Pipe notation `object.|key|` provides symbol lookup"
  (let [o (obj :a 1 :b 2 :c 3)
        k "a"]
    (- (? o.b) (? o.|k|)))
  => 1
  
  "`?` also works on javascript arrays"
  (let [o (arr [1 2 3] [4 5 6] [7 8 9])]
     (- (? o.2.2) (? o.0.0))) 
  => 8
  
  "If any of the keys are missing, `?` will not throw an object `undefined` exception but will return `nil`."
  (let [o (obj)]
    (? o.any.nested.syntax))
  => nil?)

[[:section {:title "setter - !" :tag "setter"}]]
(facts [[{:doc "! - setter"}]]

  "The `!` form provides setting using dot notation:"
  (let [o (obj)]
    (! o.a 6)  
    (? o.a)) => 6

  "Pipe notation `object.|key|` also works"
  (let [o (obj)
        k "a"]
    (! o.|k| 6)
    (? o.a)) => 6
    
  "If there is no value or the value is `nil`, `!` will delete the key from the object:"
  (let [o (obj :a 1)]
    (! o.a)
    o) => (obj)

  "If the hierachy of nested objects does not exist, `!` will create it"
  (let [o (obj)]
    (! o.a.b.c 10)
    (? o.a.b.c)) => 10

  "If one of the keys in the object accessor is not an object, `!` WILL NOT create nested structures"
  (let [o (obj :a 1)]
    (! o.a.b.c 10)
    [(? o.a.b.c) (? o.a)])
  => [nil 1])

[[:section {:title "call - ?>" :tag "call"}]]

(facts [[{:doc "?> - call"}]]
  "?> allows function calls with dot-notation syntax."

  (let [o1 (obj :a 1)
        o2 (obj :a 3)]
    (?> + o1.a o2.a)) 
  => 4
  
  "Inner forms within ?> are automatically interpreted using dot-notation. There is no need to write `?`."

  (?> .map (arr {:a 1} {:a 2} {:a 3})
           (fn [x] (inc x.a))) ;; no need to write (inc (? x.a))
  => (arr 2 3 4)
)

[[:section {:title "call on - !>" :tag "call-on"}]]

(facts [[{:doc "!> - call on"}]]

  "The !> form allows for writing dot-notation function calls."
  (let [a (arr)]
    (!> a.push 1)
    (!> a.push 2)
    a) => (arr 1 2)

  "We can also use pipe notation to dynamically invoke our function."
  (let [a (arr)
        k "push"]
    (!> a.|k| 1)
    (!> a.|k| 2)
    a) => (arr 1 2))

[[:section {:title "this"}]]

(facts [[{:doc "this"}]]

"The rational for adding `this` back into our language is that when a piece of a program really needs to work with existing javascript libraries (and it usually does), then clojurescript should give allow the flexibility to do that without adding additional noise to the code. Use with care!"

  (let [o1 (obj :a 10 
                :func (fn [] this.a))
        o2 (obj :a 20
                :func o1.func)]
    [(!> o1.func) (!> o2.func)])  
  => [10 20]

  "When `this` is nested, it works within the scope of the nested object"

  (let [o (obj :a 10
               :func (fn [] this.a)
               :b {:a 20
                   :func (fn [] this.a)})]
    [(!> o.func) (!> o.b.func)])
  => [10 20])
  
[[:section {:title "self"}]]
(facts [[{:doc "self"}]]
  "A new existential construct has been added, it be use **only** within the `obj` form. It is used to refer to the object itself and does not change contexts the way `this` does. It provides a somewhat safer self reference which does not change when the context is changed."

"`self` is similar to `this`. Note that the two keywords both refer to the object itself."

  (let [o (obj :a 1
               :thisfn (fn [] this.a)
               :selffn (fn [] self.a))]
      [(!> o.thisfn) (!> o.selffn)])
  => [1 1]
     
"We can quickly see the difference by creating another object. `o1` has been initiated with functions defined `o`. If we invoke the `o1` functions, it can be seen that the context for `o.thisfn` has changed and so it returns `o1.a` (2). While `o1.selffn` returns the value `o.a` (1)"

  (let [o (obj :a 1
               :thisfn (fn [] this.a)
               :selffn (fn [] self.a))
        o1 (obj :a 2
               :thisfn o.thisfn
               :selffn o.selffn)]
    [(!> o1.thisfn) (!> o1.selffn)])
  => [2 1]

"A useful property of `obj` and `self` can be seen in the next example. Even though both have the same structure, `self` in `a1` refers to a1 whereas `self` in a2 refers to a2.b. This was due to the fact that in `a1`, a hashmap was used to construct :b as opposed to the `obj` form in `a2`."

  (let [a1 (obj :a 1
                :b {:a 2         ;; Note {} is used
                    :func (fn [] self.a)})
        a2 (obj :a 1
                :b (obj :a 2   ;; Note obj is used
                        :func (fn [] self.a)))]
      [(!> a1.b.func) (!> a2.b.func)])
  => [1 2]
)

[[:section {:title "def.n" :tag "dot-defn"}]]

"`def.n` allow construction of functions with the javascript dot-notation. Within the forms, there is no need to add [?](#getter), [?>](#call) and [!>](#call-on) forms:"

"? getters are automatic"
 
(comment (? a.b) => a.b)

"?> call syntax is automatically applied"

(comment (?> + a.b c.d) => (+ a.b c.d))

"!> syntax is also automatically applied:"

(comment (!> a.b c.d) => (a.b c.d)) 
  
"The `defn` function:"

(defn dostuff0 [a b c]
  (!> b.func 10 10)
  (?> + a.val b.val)
  (inc (? c.val)))

"Can be written more succinctly using `def.n`:"
(def.n dostuff1 [a b c]
  (b.func 10 10)
  (+ a.val b.val)
  (inc c.val))

[[:section {:title "f.n" :tag "dot-fn"}]]

"`f.n` is the equivalent dot-notation counterpart for `fn`"

(def dostuff2 
  (f.n [a b c]
    (b.func 10 10)
    (+ a.val b.val)
    (inc c.val)))

[[:section {:title "do.n" :tag "dot-do"}]]

(facts [[{:doc "dot-do"}]]
      
  "The `do.n` block is like `do` but allows dot-notation syntax:"
  (do.n
    (let [o (obj)]
      (! o.val.a 1)
      (! o.val.b 2)
      (+ o.val.a o.val.b)))
  => 3)

[[:section {:title "raw forms" :tag "raw"}]]
  
(facts [[{:doc "raw"}]]

  "For those that want to write clojurescript with `{}` interpreted as js objects and `[]` interpreted as arrays, then the raw js forms are very handy. `def*`, `def*n`, `f*n` and `do*n` allow this. The same function `raw-fn` can be defined the following four ways:"

  (def raw-fn (f*n [o] (! o.val [1 2 3 4 5])))

  (def* raw-fn (fn [o] (! o.val [1 2 3 4 5])))

  (def*n raw-fn [o] (! o.val [1 2 3 4 5]))

  (do*n (def raw-fn (fn [o] (! o.val [1 2 3 4 5]) o)))

  "where"

  (raw-fn (obj)) => (obj :val [1 2 3 4 5])

  "Within the raw js form, `self` refers to the top level object."

  (def* o (obj :a 10
               :b {:a 20
                   :func (fn [] self.a)}))
  (!> o.b.func)  => 10

  "The raw js forms will only recognise the `cljs.core` vector binding constructs: `let`, `loop`, `for`, `doseq`, `if-let` and `when-let`. Any custom macros within a raw form will run into problems."
)

(set-safe-aget false)

