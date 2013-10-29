### Overview ###

Jasmine test macros are defined in the `purnam.test` namespace. 

<a name="describe"></a>
<a name="is"></a>
<a name="it"></a>
A test suite looks something like this:

```clojure
(describe                             ;; Top Level Form
 {:doc "? - object/array accessor"    ;; Optional Doc String
  :bindings [o (obj :a 1 :b 2 :c 3)   
             ka "a"                   ;; Test bindings
             kb "b"]}

 (it "fulfils 3.1"                    ;; `it` form groups functionality
     (is 1 (? o.a))
     (is 6 (+ (? o.a) (? o.b) (? o.c))))   ;; `is` form does the test

 (it "fulfils 3.2"
     (is 1 (? o.|ka|))
     (is 3 (+ (? o.|ka|) (? o.|kb|))))

 (it "fulfils 3.3"
     (let [o (arr [1 2 3]
                  [4 5 6]
                  [7 8 9])]
       (is 8 (- (? o.2.2) (? o.0.0))) ))

 (it "fulfils 3.4"
     (let [o (obj :a {:data 1})]
       (is (? o.a.data) 1)
       (is (? o.b.data) nil)
       (is (? o.any.nested.syntax) nil))))
```

[[◄ Back (API purnam.js)|API   purnam js]] `      ` [[Next (API purnam.angular) ►|API   purnam angular]]