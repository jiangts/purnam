# purnam

Introducing javascript-ish language extensions for clojurescript. Inspired by [lispyscript](http://lispyscript.com), [coffescript](http://coffeescript.org) and [clang](https://github.com/pangloss/clang‎)

#### Why?

Because the javascript dot-notation is awesome and the javascript/clojurescript interop (`aget` `aset`, `.<fn>` and `.-<prop>` accessors) make for really ugly code. Using the language-extension macros, clojurescript becomes more than twice as concise when working with existing javascript libraries (I'm mainly working with angularjs).

So the use case can be seen below:

###### Getters:

    ## javascript (12 keystrokes):
    object.a.b.c

    ## clojurescript (45 keystrokes):
    (-> object
      (aget "a")
      (aget "b")
      (aget "c"))

    ## clojurescript + purnam (16 keystrokes):
    (? object.a.b.c)

###### Setters:

    ## javascript (17 keystrokes):
    object.a.b.c = 10

    ## clojurescript (48 keystrokes):
    (-> object
      (aget "a")
      (aget "b")
      (aset "c" 10))

    ## clojurescript + purnam (19 keystrokes):
    (! object.a.b.c 10)

###### Functions:

These are really bad examples of code but its what usually happens when working with existing javascript libraries. Using the dot-notation can save alot of screen and head space:

    ## javascript (~100 chars):
    var bad_code = function(obj, val){
      obj.inner.number = 10;
      val.inner.count = obj.inner.count + 10;}

    ## clojurescript (~180 chars):
    (defn bad-code [obj val]
      (-> obj (aget "inner") (aset "number" 10))
      (-> val
          (aget "inner")
          (aset "count"
                (+ 10 (-> obj (aget "inner") (aget "count")))))
      nil)

    ## clojurescript + purnam (~110 chars):
    (def.n bad-code [obj val]
      (! obj.inner.number 10)
      (! val.inner.count
         (+ 10 obj.inner.count))
      nil)

#### Installation

In your project file, add

```clojure
[purnam "0.0.9"]
```

#### Javascript

```clojure
(ns <app>.core
  (:use [purnam.cljs :only [aset-in aget-in]])
  (:require-macros
   [purnam.js :refer [! ? !> ?> def.n do.n f.n obj]]))
```

The following are macros are defined for extending clojurescript:

  -  `?`  Getter
  -  `?>` Call
  -  `!`  Setter
  -  `!>` Invoke
  -  `obj` Creates nested objects
  -  `def.n` Function definitions
  -  `do.n` Function definitions
  -  `f.n` is rarely needed

The javascript.dot.notation can be used inside any of the forms

##### Simple Usage

```clojure
(def o1 (obj :a 1 :b 2 :c 3))

(? o1.a) ;=> 1
(? o1.b) ;=> 2
(? o1.c) ;=> 3
(? o1.d) ;=> undefined
(! o1.d 4)
(? o1.d) ;=> 4

(def o2 (obj :array [1 {:2 [3]} 4]))
(? o2.0) ;=> 1
(? o2.1.2.0) ;=> 3
(? o2.|o1.a|.|o1.b|.0) ;=> 3
(? o2.2) ;=> 4
```

##### `?>` Example:

```clojure
(let [arr (array 1 2 3)]
  (if (?> => arr.length 3)
     (! arr.2 value))
  arr)
;=> [1 2 10]
```

##### `!>` Example:

```clojure
(let [arr (array 1 2)]
  (!> arr.push 3)
  arr)
;=> [1 2 3]

;; Can use `this` as well
(def o3 (obj :a 2
             :fn (fn [] this.a)))

(!> o3.fn)
;=> 2


;; Notice that this.a is used
;; instead of (this-as me
;;               (aget me "a"))
;;
;; Note, the `this` in purnam is implemented as a
;; pointer to the object, not the `this` in javascript.
;;
;; I don't really advise using `this` in your
;; clojurescript code. This is more an experimental
;; feature that I'm playing around with so that
;; functions are more explicit in what they are doing,
;; instead of swapping scope when they go into another
;; object.
;;

(def o4 (obj :a 3
             :fn o3.fn))

(!> o4.fn)
;=> 2 (points to o3.a instead of o4.a)

(! o3.a 4)
(!> o4.fn)
;=> 4

(let [a (o3.fn)]
  (a))
;=> 4
```


#### functions

When `f.n` and `def.n` are used for function definitions, there is no need to write `?`, `?>` and `!>` within the form as it is handled automatically. Actually, the short hand is avaliable Within any of the macro forms.

Typing:
   - a.b.c is the same as typing `(? a.b.c)`:
   - `(inc a.b.c 1)` is the same as typing `(?> inc a.b.c 1)`:
   - `(a.call arg1 arg2)` is the same as typing `(!> a.call arg1 arg2)`:
   - Only the setter function `(! a.b.c (new value))` remains the same.

Example:

```clojure
(def.n set-static-breadcrumbs [app v]
  (js/console.log "Setting breadcrumbs")
  (! app.layout.breadcrumbs
     (let [arr (array)]
       (doseq [i v.trail]
         (arr.push (aget app.static i))) ;; future syntax: app.static|i|
       (arr.push v)
       arr)))
```

## Other Libraries

#### angularjs

Angularjs macros help alleviate the amount of callback functions that one has to write

```clojure
(ns <app>.core
  (:use [purnam.cljs :only [aset-in aget-in]])
  (:require-macros
   [purnam.js :refer [! def.n obj]]
   [purnam.angular :refer [def.module def.config
                          def.controller def.service]]))

(def.module app [ui ui.bootstrap ui.compat])

(def.config app [$locationProvider]
  (doto $locationProvider (.hashPrefix "!")))


(def.config app [$routeProvider]
  (doto $routeProvider
    (.when "" (obj :redirectTo "/home"))))

(def.controller app.MainCtrl [$scope $state App AppFn]
  (! $scope.app App)
  (! $scope.fn AppFn)
  (! $scope.state $state)
  ($state.transitionTo "home"))
```

#### jasmin

Jasmin macros for clearer tests. The .cljs tests are defined using jasmin macros (which are really kinda cool)

```clojure
(ns purnam.test-js
  (:require [purnam.cljs :as p])
  (:use-macros [purnam.js :only [obj ?]]
               [purnam.jasmin :only [describe it is is-not]]))

(init-jasmin) ;; installs the `.toSatisfy` function that `is` and `is-not` templates

(describe
 "objs contain js arrays"
 [o1 (obj :array [1 2 3 4])]

 (it "describes something"
  (is (? o1.array.0) 1)
  (is (? o1.array.1) 2)
  (is (? o1.array.2) 3)
  (is (? o1.array.3) 4)
  (is (? o1.array.4) js/undefined)))
```


#### TDD Workflow

######.cljs files
Open two terminal window in the project directory. You will require [karma](http://github.com/karma-runner/karma):

In the first:

```bash
lein cljsbuild auto
```

In the second:

```bash
karma start
```

Use any editor. cljsbuild will compile all `.cljs` files to `harness/purnam.js` and will be run by karma.

######.clj files

These are tests for macro helper functions written in `.clj` in the `src` directory.

```bash
lein midje :autotest
```


## Todos

- a clearer introduction with coffeescript/clojurescript comparison
- better documentation and examples (maybe copy off coffeescript/lispyscript page)
- more tests
- how to do testing with karma and cljsbuild
- syntax for refered values: (! lookup.child.|query.|i|.mother|.name "Anne")

## License

Copyright © 2013 Chris Zheng

Distributed under the Eclipse Public License, the same as Clojure.
