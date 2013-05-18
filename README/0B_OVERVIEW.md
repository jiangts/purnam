

- use of javascript ([dot](Syntax-Overview#js-notation)) notation semantics
- constructors
  - primitive objects ([obj](Syntax-Overview#obj)) 
  - primitive arrays ([arr](Syntax-Overview#arr))
- accessors 
  - getters ([?](Syntax-Overview#getter)) 
  - setters ([!](Syntax-Overview#setter))
  - call on object ([!>](Syntax-Overview#callobj))
- existential pointers
  - contextual pointer ([this](Syntax-Overview#this))
  - self referential pointer ([self](Syntax-Overview#self))
- additional forms allowing javascript dot notation
  - call ([?>](Syntax-Overview#calln))
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

### obj

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

### arr

##### 3.1
The `arr` form constructs a javascript array primitive:

```clojure
(arr 1 2 3 4 5)
```
produces this javascript:
```javascript
[1, 2, 3, 4, 5]
```

##### 3.2
The `arr` form also supports nested objects and arrays like that in obj:

```clojure
(arr {:data [1 2 3 4 5]} {:data [6 7 8 9 10]}))
```
produces this javascript:
```javascript
[{"data": [1,2,3,4,5]}, {"data": [6,7,8,9,10]}]
```

<a name=getter></a>
### ? 

##### 4.1
The `?` form provides dot notation access:
```clojure
(def o (obj :a 1 :b 2 :c 3))
(+ (? o.a) (? o.b) (? o.c)) 
;; => 6 
```

##### 4.2
Pipe notation `object.|key|` provides symbol lookup:
```clojure
(def k "a")
(- (? o.b) (o.|k|))
;; => 1
```

##### 4.3
Because `?` is a transformation using `aget`, it also works on javascript arrays
```clojure
(def o (arr [[1 2 3] [4 5 6] [7 8 9]]))
(- (? o.2.2) (? o.0.0)) 
;; => 8
```

##### 4.4


<a name=setter></a>
### !

### this

##### 6.1
The controversial `this` construct is now back into the language. Now, `this` really means `this`. Use it with extreme care! The function uses the object where it is bound, exactly the way javascript does it. The following illustrates this use.

```clojure
(def objA (obj :a 10 
               :func (fn [] this.a)))
(.func objA)  
;; => 10

(def objB (obj :a 20
               :func objA.func)   ;; Also the first introduction to the js dot notation
(.func objB)  
;; => 20
```

There should be no suprises here. Everything works the way it does in javascript. The rational for adding `this` back into our language is that when a piece of a program really needs to work with existing javascript libraries (and it usually does), then clojurescript should give allow the flexibility to do that without adding additional noise to the code.

##### 6.2
Note that `this` can only be used

### self

##### 7.1
A new existential construct has been added, it be use **only** within the `obj` form. It is used to refer to the object itself and does not change contexts the way `this` does. It is really a shortcut for:
(let [objC (obj)]
  ())


### def.n
If you use this notation in a normal clojurescript `defn` form, the cljs compiler will scream at you.

    (defn add-inner [a b]
      (+ a.inner b.inner)) ;;=> unknown var `ainner`, `binner`     

    (defn print-this []
      (js/console.log this)) 
     
    (print-this)  ;;=> 'undefined'     

purnam provides the `def.n` construct which allows both js-notation and this.

    (def.n add-inner [a b]
      (+ a.inner b.inner))

    (def.n print-this []
      (js/console.log this))

    (print-this) ;; => <window>

The rational for adding the `this` back into our language is that when a piece of a program really needs to work with existing javascript libraries, then clojurescript should give allow the flexibility to do that without adding additional noise to the code.


### def.n
If you use this notation in a normal clojurescript `defn` form, the cljs compiler will scream at you.

    (defn add-inner [a b]
      (+ a.inner b.inner)) ;;=> unknown var `ainner`, `binner`     

    (defn print-this []
      (js/console.log this)) 
     
    (print-this)  ;;=> 'undefined'     

purnam provides the `def.n` construct which allows both js-notation and this.

    (def.n add-inner [a b]
      (+ a.inner b.inner))

    (def.n print-this []
      (js/console.log this))

    (print-this) ;; => <window>

The rational is that when you really really need to work with 


