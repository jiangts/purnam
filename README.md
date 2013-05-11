# purnam

javascript-ish syntax for clojurescript

##Why?

Because the javascript dot-notation is awesome! but mostly because I can't stand `aget` `aset` and `.-<blah>` accessors.

***WARNING*** 
Still under developement, not ready for production

```shell
git clone https://github.com/zcaudate/purnam.git
cd purnam
lein install
```

In your project file, add

```clojure 
[purnam "0.1.0-SNAPSHOT"]
```

#### Javascript


```clojure 
(ns <app>.core
  (:use [purnam.cljs :only [aset-in aget-in]])
  (:require-macros
   [purnam.js :refer [! ? !> ?> def.n f.n obj]]))
```

Four macros are defined for use in functions: 

    `?`  Getter
    `?>` Call 
    `!`  Setter
    `!>` Invoke

Simple Usage

```clojure
(def o1 (obj :a 1 :b 2 :c 3))

(? o2.a) ;=> 1
(? o2.b) ;=> 2
(? o2.c) ;=> 3
(? o2.d) ;=> undefined

(def o2 (obj :array [1 {:2 [3]} 4]))
(? o2.0) ;=> 1
(? o2.1.2.0) ;=> 3
(? o2.2) ;=> 4
```

`?>` Example:

```clojure
(let [arr (array 1 2 3)]
  (if (?> => arr.length 3)
     (! arr.2 value))
  arr)
;=> [1 2 10]
```

`!>` Example:

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
```

#### functions

When `f.n` and `def.n` are used for function definitions, there is no need to write `?`, `?>` and `!>` within the form as it is handled automatically:

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

Jasmin macros for clearer tests

```clojure
(ns purnam.test-js
  (:require [purnam.cljs :as p])
  (:use-macros [purnam.js :only [obj ?]]
               [purnam.jasmin :only [describe it is is-not]]))

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

## Todos

- a clearer introduction with coffeescript/clojurescript comparison
- better documentation and examples (maybe copy off coffeescript/lispyscript page)
- more tests
- how to do testing with karma and cljsbuild
- syntax for refered values: (! lookup.child.|query.|i|.mother|.name "Anne")

## License

Copyright © 2013 Chris Zheng

Distributed under the Eclipse Public License, the same as Clojure.
