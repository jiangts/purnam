# purnam

Javascripish syntax for clojurescript

##Why?

Because the javascript dot-notation is awesome!

#### Include
 
    (ns <app>.core
      (:use [purna.cljs :only [aset-in aget-in]])
      (:require-macros
       [purna.js :refer [! ? !> ?> def.n f.n obj]]))

#### Javascript

Four macros are defined for use in functions: 

    `?`  Getter
    `?>` Call 
    `!`  Setter
    `!>` Invoke

Simple Usage

    (def o1 (obj :a 1 :b 2 :c 3))
    
    (? o2.a) ;=> 1
    (? o2.b) ;=> 2
    (? o2.c) ;=> 3
    (? o2.d) ;=> undefined

    (def o2 (obj :array [1 {:2 [3]} 4]))
    (? o2.0) ;=> 1
    (? o2.1.2.0) ;=> 3
    (? o2.2) ;=> 4

`?>` Example:

    (defn set3rd [arr value]
      (if (?> => arr.length 3)
         (! arr.2 value)))

    (def arr1 (array 1 2 3))
    (set3rd arr1 10)
    arr1 ;=> [1 2 10]

`!>` Example:

    (defn append [arr value]
      (!> arr.push value))


    (def arr2 (array 1 2))
    (append arr2 3)
    arr2 ;=> [1 2 3]

#### functions

When `f.n` and `def.n` are used for function definitions, there is no need to write `?`, `?>` and `!>` within the form as it is handled automatically:

Example:

    (def.n set-static-breadcrumbs [app v]
      (js/console.log "Setting breadcrumbs")
      (! app.layout.breadcrumbs
         (let [arr (array)]
           (doseq [i v.trail]
             (arr.push (aget app.static i)))
           (arr.push v)
           arr)))

#### angularjs

Angularjs macros help alleviate the amount of callback functions that one has to write

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

#### jasmin

Jasmin macros for clearer tests

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


## Todos

- a clearer introduction
- better documentation and examples (maybe copy off coffeescript page)
- more tests
- how to do testing with karma and cljsbuild


## License

Copyright © 2013 Chris Zheng

Distributed under the Eclipse Public License, the same as Clojure.
