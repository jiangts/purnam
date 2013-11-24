### Overview ###

Javascript language extensions are defined in the `purnam.core` namespace. 

- use of javascript [`dot`](#dot-notation) notation semantics
- constructors
  - primitive objects [`obj`](#obj)
  - primitive arrays [`arr`](#arr)
- accessors 
  - getters [`?`](#getter)
  - call with dot notation [`?>`](#calln)
  - setters [`!`](#setter)
  - call on object [`!>`](#callobj)
- existential pointers
  - contextual pointer [`this`](#this)
  - self referential pointer [`self`](#self)
- additional forms allowing javascript dot notation
  - definitions [`def.n`](#defn), [`def*n`](#defnraw) and [`def*`](#defraw) 
  - lambdas [`f.n`](#defn) and [`f*n`](#fnraw)
  - do blocks [`do.n`](#don) and [`do*n`](#donraw)

<a name=dot-notation></a>
### dot notation
The `.` accessor is an extremely succinct language construct and now we can also use this in clojurescript. We can't use square brackets `[]` in clojure so instead, pipes `||` are used to
denote variable accessors. The following table shows javascript syntax and its clojurescript equivalent when using purnam:

|Javascript                               |Clojurescript                     |
|-----------------------------------------|----------------------------------|
|`a["hello"]` or `a.hello`                |`a.hello`                         |
|`a["hello"]["there"]` or `a.hello.there` |`a.hello.there`                   |
|`a[hello]`                               |<code>a.&#124;hello&#124;</code>  |
|`a[hello][there]`                        |<code>a.&#124;hello&#124;.&#124;there&#124;</code>    |
|`a[b.c[d.e]].f[g]`                       |<code>a.&#124;b.c.&#124;d.e&#124;&#124;.f.&#124;g&#124;</code>  |
|`a[0][1].obj`                            |`a.0.1.obj`                       |
|`a["+"]["hello-world!"]`                 |`a.+.hello-world!`                |

The `.` notation can only be used within purnam extension forms. Examples are given in getters [`?`](#getter), setters [`!`](Syntax-Overview#setter) and function definitions [`def.n`](Syntax-Overview#defn)

<a name=obj></a>
### `obj` - primitive object constructor

##### 1.1 Strings
Raw js objects are constructed in clojurescript with `obj`:

```clojure
(obj "key1" "val1" "key2" "val2")
```
produces this javascript:
```javascript
{"key1": "val1", "key2": "val2"} 
```

##### 1.2 Keywords
Keywords can be used instead of strings for improved legibility. The previous example can also be written as:

```clojure
(obj :key1 "val1" :key2 "val2") 
```

##### 1.3 Symbols
Symbol are evalutated. This will produce an equivalent object to the previous examples:

```clojure
(def s1 "key1")
(def s2 "key2")
(obj s1 "val1" s2 "val1")
```

note that the symbols have to represent strings to get the same output as previous. The following WILL NOT construct the equivalent object as keywords instead of strings will be used as keys

```clojure
(def s1 :key1)
(def s2 :key2)
(obj s1 "val1" s2 "val1")
```

#####  1.4 Nesting
The `obj` form can be used to set up nested js objects and arrays primitives. Using `[]` with the form will create a new js array, `{}` will create a new js object. 

```clojure
(obj :data [{:id 1 :name "one"} 
             {:id 2 :name "two"}]))
```
produces this javascript:
```javascript
{"data": [{"id": 1, "name":"one"}
          {"id": 2, "name":"two"}]}
```

##### 1.5 Deeper Nesting
The nesting notation alleviates the use of the `clj->js` transform for constructing large javascipt variables. In this way, deeply nested javascript object structures can be created in clojurescript very naturally.

```clojure
(obj :name "l1" :data [1 2 3] 
     :next {:name "l2" :data [4 5 6] 
            :next {:name "l3" :data [7 8 9]}}) 
```
produces this javascript:
```javascript
{name: "l1", 
 data: [1, 2, 3],    
 next: {name: "l2", 
        data: [4, 5, 6], 
        next: {name: "l3", 
               data: [7, 8, 9]}}}) 
```

<a name=arr></a>
### `arr` - primitive array constructor

##### 2.1 Array Construction
The `arr` form constructs a javascript array primitive:

```clojure
(arr 1 2 3 4 5)
```
produces this javascript:
```javascript
[1, 2, 3, 4, 5]
```

##### 2.2 Nesting
The `arr` form also supports nested objects and arrays like that in obj:

```clojure
(arr {:data [1 2 3 4 5]} {:data [6 7 8 9 10]}))
```
produces this javascript:
```javascript
[{"data": [1,2,3,4,5]}, {"data": [6,7,8,9,10]}]
```

<a name=getter></a>
### `?` - object/array accessor

##### 3.1 Objects
The `?` form provides dot notation access:
```clojure
(def o (obj :a 1 :b 2 :c 3))
(+ (? o.a) (? o.b) (? o.c)) 
;; => 6 
```

##### 3.2 Pipe Notation
Pipe notation `object.|key|` provides symbol lookup:
```clojure
(def o (obj :a 1 :b 2 :c 3))
(def k "a")
(- (? o.b) (o.|k|))
;; => 1
```

##### 3.3 Arrays
`?` also works on javascript arrays
```clojure
(def o (arr [1 2 3] [4 5 6] [7 8 9]))
(- (? o.2.2) (? o.0.0)) 
;; => 8
```

##### 3.4 Nil  
If any of the keys are missing, `?` will not throw an object `undefined` exception but will return `nil`.

```clojure
(def o (obj :a {:value 1}))
(? o.a.value)  
;; => 1
(? o.b.value)  ;;-> missing 'b'
;; => nil
(? o.any.nested.syntax)
;; => nil
```
<a name=calln></a>
### `?>` - call with notation
##### 4.1 Functions allowing Dot Notation 
The call with object `?>` form allows function calls with dot-notation syntax.

```clojure
(let [o1 (obj :a 1))
      o2 (obj :a 3))]
  (?> + o1.a o2.a) ;;=> 4
```

##### 4.2 Inner Function
Inner forms within `?>` are automatically interpreted as using dot-notation. There is no need to write `?` when inside of a `?>` form.

```clojure
(?> .map (arr {:a 1} {:a 2} {:a 3})
         (fn [x] (inc x.a))) ;; no need to write (inc (? x.a))
;=> [2 3 4]
```

<a name=setter></a>
### `!` - setter

##### 5.1 First look
The `!` form provides dot notation :
```clojure
(def o (obj))
(? o.a)    ;; => o.a is undefined

;; We now set its value
(! o.a 6)  
(? o.a)    ;; => 6
```

##### 5.2 Pipe Notation
Pipe notation `object.|key|` provides symbol set:
```clojure
(def o (obj))
(def k "a")
(? o.|k|)    ;; => o.a is undefined

;; We now set its value
(! o.|k| 6)
(? o.|k|)    ;; => 6
(? o.a)      ;; => 6
```

##### 5.3 Root Var Not Rebindable
Setting the root var will not work (same as in clojurescript). There is currently a compile time error.
```clojure
(def o (obj :a 1))
(! o (obj :a 2)) ;; Throws compilation exception
```

##### 5.4 Creating Nested Objects
If the hierachy of nested objects does not exist, `!` will create it
```clojure
(def o (obj))
(? o.a) ;; => nil
(? o.a.b.c) ;; => nil

;; Setting its value
(! o.a.b.c 10)
(? o.a.b.c)   ;;=> 10
```

##### 5.5 No Overwrites
If one of the keys in the object accessor is not an object, `!` WILL NOT overwrite the value and create nested structures
```clojure
(def o (obj :a 1))
(? o.a)     ;; => 1
(? o.a.b.c) ;; => nil

;; Attempting to setting its value
(! o.a.b.c 10)
(? o.a.b.c)   ;;=> nil
(? o.a)       ;;=> 1
```

<a name=callobj></a>
### `!>` - call on object

##### 6.1 Function calls
The `!>` form allows for writing dot-notation function calls.
```clojure
(def a (arr))
(!> a.push 1)
(!> a.push 2)
(? a.0) ;; => 1
(? a.1) ;; => 2
```

##### 6.2 Piped notation calls
We can also use pipe notation to dynamically invoke our function:

```clojure
(def a (arr))
(def k "push")
(!> a.|k| 1)
(!> a.|k| 2)
(? a.0) ;; => 1
(? a.1) ;; => 2
```
<a name=this></a>
### `this` - context operator
The controversial `this` construct is now back into the language. Now, `this` really means `this`. Use it with extreme care! 

##### 7.1 The way `this` used to be
The function uses the object where it is bound, exactly the way javascript does it. 

```clojure
(def o1 (obj :a 10 
               :func (fn [] this.a)))
(!> o1.func)  
;; => 10

(def o2 (obj :a 20
               :func o1.func) ;; We bound the function to objB
(!> o2.func)  
;; => 20            ;; And get a different result.
```

There should be no suprises here. Everything works the way it does in javascript. The rational for adding `this` back into our language is that when a piece of a program really needs to work with existing javascript libraries (and it usually does), then clojurescript should give allow the flexibility to do that without adding additional noise to the code.

##### 7.2 Nesting
The example shows how `this` works when nested

```clojure
(def func (fn [] this.a))

(def o1 (obj :a 10
             :func func
             :b {:a 20
                 :func func)

(!> o1.func) ;; => 10
(!> o1.b.func) ;; => 20 
```

<a name=self></a>
### self 
A new existential construct has been added, it be use **only** within the `obj` form. It is used to refer to the object itself and does not change contexts the way `this` does. It provides a somewhat safer self reference which does not change when the context is changed:

##### 8.1 Self Reference
How `this` and `self` are similar
```clojure
(def a1 (obj :a 1
             :thisfn (fn [] this.a)
             :selffn (fn [] self.a)))
     
(!> a1.thisfn) ;=> 1
(!> a1.selffn) ;=> 1
```
Note that `this` and `self` both refer to the object itself. 

##### 8.2 Differences with `this`
We can quickly see the difference by creating another object:

```clojure
(def a1 (obj :a 1
             :thisfn (fn [] this.a)
             :selffn (fn [] self.a)))

(def a2 (obj :a 2
             :thisfn a1.thisfn
             :selffn a1.selffn))

(!> a2.thisfn) ;=> 2  ;; Now refers to a2.a
(!> a2.selffn) ;=> 1  ;; Will still refer to a1.a
```

a2 has been defined with the methods of a1 in the previous section. If we invoke the methods, it can be seen that the context for a2.thisfn has changed and so it returns a2.a. While a2.selffn still returns a value of a1.a

<a name=8-3></a>
##### 8.3 Nesting `obj` and Hashmaps
A useful property of `obj` can be seen in this example below. 

```clojure
(def a1 (obj :a 1
             :b {:a 2        ;; Note {} is used
                 :func (fn [] self.a)})
(def a2 (obj :a 1
             :b  (obj :a 2   ;; Note obj is used
                      :func (fn [] self.a)))

(!> a1.func) ;=> 1
(!> a2.func) ;=> 2
```

Even though both have the same structure, `self` in `a1` refers to a1 whereas `self` in a2 refers to a2.b. This was due to the fact that in `a1`, a hashmap was used to construct :b as opposed to the `obj` form in `a2`.

<a name=fn></a>
<a name=defn></a>
### `f.n` and `def.n`
`f.n` and `def.n` allow construction of functions with the javascript dot-notation. Within the forms, there is no need to add `?`, `?>` and `!>` forms. 

 - `(? a.b)` becomes `a.b`
 - `(?> + a.b c.d)` becomes `(+ a.b c.d)`
 - `(!> a.b c.d)` becomes `(a.b c.d)` 

The `defn` function:

```clojure
(defn dostuff [a b c]
  (!> b.func 10 10)
  (?> + a.val b.val)
  (inc (? c.val)))
```

Can be written like this using `def.n`:

```clojure
(def.n dostuff [a b c]
  (b.func 10 10)
  (+ a.val b.val)
  (inc c.val))
```

##### 9.1 Function Definition with `dot` Notation
If you use this notation in a normal clojurescript `defn` form, the cljs compiler will scream at you.

```clojure
(defn add-inner [a b]
  (+ a.inner b.inner)) ;;=> Compiler: ReferenceError - ainner is not defined     

(def print-this 
  (fn [] (js/console.log this)) ;;=> Compiler: Use of undeclared Var purnam.test-overview/this
 
(print-this) ;; => null
```

The `def.n` and `f.n` constructs allow both `dot`-notation and `this`.

```clojure
(def.n add-inner [a b]
  (+ a.inner b.inner))

(def print-this 
  (f.n [] (js/console.log this)) 

(print-this) ;; => <window>
```

<a name=don></a>
### `do.n`
The `do.n` block is like `do` but allows `dot`-notation and `this`

##### 10.1 
```clojure
(def o (obj))
(let [o1 (obj :inner 1)
      o2 (obj :inner 2)
      o3 (obj :inner 3)]
  (do.n
    (! o.val.a (+ o1.inner o2.inner))
    (! o.val.b (* o2.inner o3.inner))
    o.val))
;; => `{"a": 3; "b": 6}`
```

<a name=defraw></a>
<a name=defnraw></a>
<a name=fnraw></a>
<a name=donraw></a>
### raw js forms - `def*`, `def*n`, `f*n`, `do*n`

For those that want to write clojurescript with `{}` interpreted as js objects and `[]` interpreted as arrays, then the raw js forms are very handy. Essentially, all these functions are the same but the vector `[1 2 3 4 5]` actually represents a javascript array.

##### 11.1 Raw forms
```clojure
(def f0 (f*n [o] (! o.val [1 2 3 4 5])))

(def* f1 (fn [o] (! o.val [1 2 3 4 5])))

(def*n f2 [o] (! o.val [1 2 3 4 5]))

(do*n
  (def f3 (fn [o] (! o.val [1 2 3 4 5]))))
```

##### 11.2 Self 
Within the raw js form, `self` only refers to the current object, unlike here where it refers to the top level [`obj`](#8-3).

```clojure
(def* o1 {:a 10
          :b {:a 20
              :func (fn [] self.a)}})
(def* o2 (obj :a 10
              :b {:a 20
                  :func (fn [] self.a)}))
(!> o1.b.func) ;; => 20
(!> o2.b.func) ;; => 20
```

##### 11.3 Use with care

The raw js forms will only recognise the core binding constructs: `let` `loop` `for` `doseq` `if-let` `when-let`. Any custom macros with vector bindings will run into problems.


[[◄ Back (Home)|Home]] `      ` [[Next (API purnam.test) ►|API   purnam test]]
