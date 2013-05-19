

- use of javascript ([dot](Syntax-Overview#js-notation)) notation semantics
- constructors
  - primitive objects ([obj](Syntax-Overview#obj)) 
  - primitive arrays ([arr](Syntax-Overview#arr))
- accessors 
  - getters ([?](Syntax-Overview#getter)) 
  - call with dot notation([?>](Syntax-Overview#calln))
  - setters ([!](Syntax-Overview#setter))
  - call on object ([!>](Syntax-Overview#callobj))
- existential pointers
  - contextual pointer ([this](Syntax-Overview#this))
  - self referential pointer ([self](Syntax-Overview#self))
- additional forms allowing javascript dot notation
  - function definition ([def.n](Syntax-Overview#defn))
  - do block ([do.n](Syntax-Overview#don))
- forms interpreting primitives: 
  - definition ([def*](Syntax-Overview#defraw)) 
  - function definition ([def.n*](Syntax-Overview#defnraw)) 
  - lambdas ([fn*](Syntax-Overview#fnraw))
  - do blocks ([do*](Syntax-Overview#doraw))

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

The `.` notation can only be used within purnam extension forms. Examples are given in getters ([?](Syntax-Overview#getter)), setters ([!](Syntax-Overview#setter)) and function definitions ([def.n](Syntax-Overview#defn))

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
(def s1 :key1)
(def s2 :key2)
(obj s1 "val1" s2 "val1")
```

#####  1.4 Nesting
The `obj` form can be used to setup nested js objects and arrays primitives. Using `[]` with the form will create a new js array, `{}` will create a new js object. 

```clojure
(obj {:data [{:id 1 :name "one"} 
             {:id 2 :name "two"}]}))
```
produces this javascript:
```javascript
{"data": [{"id": 1, "name":"one"}
          {"id": 2, "name":"two"}]}
```

##### 1.5 Deeper Nesting
The nesting notation alleviates the use of the `clj->js` transform for constructing large javascipt variables. In this way, deeply nested javascript object structures can be created in clojurescript very naturally.

```clojure
(obj {:name "l1" :data [1 2 3] 
      :next {:name "l2" :data [4 5 6] 
            :next {:name "l3" :data [7 8 9]}}}) 
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

##### 3.1 Array Construction
The `arr` form constructs a javascript array primitive:

```clojure
(arr 1 2 3 4 5)
```
produces this javascript:
```javascript
[1, 2, 3, 4, 5]
```

##### 3.2 Nesting
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

##### 4.1 First look
The `?` form provides dot notation access:
```clojure
(def o (obj :a 1 :b 2 :c 3))
(+ (? o.a) (? o.b) (? o.c)) 
;; => 6 
```

##### 4.2 Pipe Notation
Pipe notation `object.|key|` provides symbol lookup:
```clojure
(def o (obj :a 1 :b 2 :c 3))
(def k "a")
(- (? o.b) (o.|k|))
;; => 1
```

##### 4.3 Arrays
Because `?` is a transformation using `aget`, it also works on javascript arrays
```clojure
(def o (arr [[1 2 3] [4 5 6] [7 8 9]]))
(- (? o.2.2) (? o.0.0)) 
;; => 8
```

##### 4.4 Nil  
If any of the keys are missing, `?` will not throw an object `undefined` exception but will return `nil` instead

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
##### 5.1 Functions allowing Dot Notation 
The call with object `?>` form allows function calls with dot-notation syntax.

```clojure
(let [o1 (obj :a 1))
      o2 (obj :a 3))]
  (?> + o1.a o2.a) ;;=> 4
```

##### 5.2 Inner Function
Inner forms within `?>` are automatically interpreted as using dot-notation. There is no need to write `?` when inside of `?>`.

```clojure
(?> mapv (fn [x] (inc x.a))   ;; no need to write (inc (? x.a)) 
         [(obj :a 1) (obj :a 2) (obj :a 3)])
;=> [2 3 4]
```

<a name=setter></a>
### `!` - setter

##### 6.1 First look
The `!` form provides dot notation :
```clojure
(def o (obj))
(? o.a)    ;; => undefined 
(! o.a 6)  ;;
(? o.a)    ;; => 6
```

##### 6.2 Pipe Notation
Pipe notation `object.|key|` provides symbol set:
```clojure
(def o (obj))
(def k "a")
(! o.|k| 6)
(? o.a)    ;; => 6
```

##### 6.3 Root Var Not Rebindable
Setting the root var will not work (same as in clojurescript)
```clojure
(def o (obj :a 1))
(! o (obj :a 2)) ;; Throws compilation exception
```

##### 6.4 Creating Nested Objects
If the hierachy of objects do not exist, `!` will create it
```clojure
(def o (obj))
(! o.a.b.c 10)
(? o.a.b.c)   ;;=> 10
```

##### 6.5 No Overwrites
If one of the keys in the object hierachy is not an object, `!` will not create more nested structures
```clojure
(def o (obj :a 1))
(! o.a.b.c 10)
(? o.a.b.c)   ;;=> undefined
```

<a name=callobj></a>
### `!>` - call on object
The `!>` form allows for writing dot-notation function calls.
```clojure
(def a (arr))
(!> a.push 1)
(? a.0)   ;;=> 1
```

### this

##### 7.1 Context Operator
The controversial `this` construct is now back into the language. Now, `this` really means `this`. Use it with extreme care! The function uses the object where it is bound, exactly the way javascript does it. The following illustrates this use.

```clojure
(def objA (obj :a 10 
               :func (fn [] this.a)))
(!> objA.func)  
;; => 10

(def objB (obj :a 20
               :func objA.func) 
(!> objB.func)  
;; => 20
```

There should be no suprises here. Everything works the way it does in javascript. The rational for adding `this` back into our language is that when a piece of a program really needs to work with existing javascript libraries (and it usually does), then clojurescript should give allow the flexibility to do that without adding additional noise to the code.

### self 

##### 8.1 Self Reference
A new existential construct has been added, it be use **only** within the `obj` form. It is used to refer to the object itself and does not change contexts the way `this` does. It provides a somewhat safer self reference which does not change when the context is changed:

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
(def a2 (obj :a 2
             :thisfn a1.thisfn
             :selffn a1.selffn))
```

a2 has been defined with the methods of a1 in the previous section. If we invoke the methods, it can be seen that the context for a2.thisfn has changed and so it returns a2.a. While a2.selffn still returns a value of a1.a

```clojure
(!> a2.thisfn) ;=> 2
(!> a2.selffn) ;=> 1
```

##### 8.3 Nesting `obj` and Hashmaps
A useful property of `obj` can be seen in this example below. Even though both have the same structure, `self` in `a1` refers to a1 whereas `self` in a2 refers to a2.b. This was due to the fact that in `a1`, a hashmap was used to construct :b as opposed to the `obj` form in `a2`.

```clojure
(def a1 (obj :a 1
             :b {:a 2
                 :func (fn [] self.a)})
(def a2 (obj :a 1
             :b  (obj :a 2
                      :func (fn [] self.a)))

(!> a1.func) ;=> 1
(!> a2.func) ;=> 2
```

### def.n
##### 9.1 Function Definition with dot Notation
If you use this notation in a normal clojurescript `defn` form, the cljs compiler will scream at you.

    (defn add-inner [a b]
      (+ a.inner b.inner)) ;;=> unknown var `ainner`, `binner`     

    (defn print-this []
      (js/console.log this)) 
     
    (print-this)  ;;=> 'undefined'     

The `def.n` construct which allows both js-notation and this.

    (def.n add-inner [a b]
      (+ a.inner b.inner))

    (def.n print-this []
      (js/console.log this))

    (print-this) ;; => <window>

Within 


### do.n
### def*
### def.n*
### fn*
### do*