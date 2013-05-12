# purnam

Purnam - AngularJs Language Extensions for Clojurescript

Inspired by [lispyscript](http://lispyscript.com), [coffescript](http://coffeescript.org) and [clang](https://github.com/pangloss/clang‎)


#### Installation

In your project file, add

```clojure
[purnam "0.0.10"]
```

#### Features
Purnam has three main components:

1. Angular Language Extensions
2. Jasmin Language Extensions for TDD with Karma
3. Clojurescript Language Extensions (which the previous two are built upon)

#### Why not use lispyscript/coffeescript/clang?
I like each of the languages for their own features:

   - coffeescript for its succinctness
   - lispyscript for its syntax and macros
   - clang for its sheer brilliance and audacity

However, in using each language I did find some weaknesses

   - coffeescript and its ambiguous syntax that changes meaning with whitespace
   - lispyscript is too new for me and not widely adopted
   - clang is to ambitious in what it is trying to do (make angular work with clojure) and I think there are definite performance implications in doing so.

The goal of this project is to provide opt-in language extensions for clojurescript to have the same sort of succintness when working with angular and all other javascript libraries.

#### Angular
Write angular.js like its angular.cljs!

I'll put up a simpler example soon. But this code is taken off a current project that I am working on
using angular, angular-ui, ui-bootstrap and ui-router:

```clojure
(ns <app>.core
  (:use [purnam.cljs :only [aset-in aget-in]])
  (:require-macros
   [purnam.js :refer [! def.n obj]]
   [purnam.angular :refer [def.module def.config
                          def.controller def.service]]))
                          
(def.module app [ui ui.bootstrap ui.compat])

(def.config app [$locationProvider $routeProvider]
  (doto $locationProvider (.hashPrefix "!"))
  (doto $routeProvider
    (.when "" (obj :redirectTo "/home"))))

(def.controller app.MainCtrl [$scope $state App AppFn]
  (! $scope.app App)
  (! $scope.fn AppFn)
  (! $scope.state $state)
  ($state.transitionTo "home"))

(def.service app.$markdown [] 
  (js/Showdown.converter.))
  
(def.service app.App []
  (obj :catalog {}
       :cart {}
       :user {}
       :defaults {:catalog {:products {:list  {:page 1 :perPage 20}}}}
       :layout   {:breadcrumbs []
                  :meta {:seo []
                         :keywords []}}))

(def.service app.AppFn [$markdown]
  (obj :md (fn [data] 
              (.makeHtml $markdown (str data)))))
```

#### Jasmin
Angular has a great testing facility and you can hook right into it with the purnam.jasmin library to be run with Karma for TDD.

```clojure
(ns purnam.test-js
  (:use [purnam.cljs :only [aget-in aset-in]])
  (:require-macros [purnam.js :as j])
  (:use-macros [purnam.js :only [obj ? ?> ! !> f.n def.n]]
               [purnam.jasmin :only [init-jasmin describe it is is-not equals]]))

(init-jasmin)

(describe
 "objs contain js arrays"
 [o1 (obj :array [1 2 3 4])]
 (it "describes something"  
   (is o1.array.0 odd?)
   (is o1.array.1 2)
   (is o1.array.2 3)
   (is o1.array.3 4)
   (is o1.array.4 js/undefined)
   (! o1.array.4 5)
   (is o1.array.4 5))

(describe
 "obj.self refers to the object"
 [o3 (obj :a 2 :fn (fn [] self.a))
  o4 (obj :a 3 :fn o3.fn)
  fn1  o3.fn]
 (it "is different to `this` in js"
  (is (aget (aget-in o3 []) "a") 2)
  (is (o3.fn) 2)
  (is (o4.fn) 2)
  (is (fn1) 2)
  (! o3.a 4)
  (is (o3.fn) 4)
  (is (o4.fn) 4)
  (is (fn1) 4)))
```

#### Language Extensions

Javascript dot-notation is really handy to have around.

#### Dots and Pipes as Accessors
We can't use square brackets `[]` in clojure so instead, pipes `||` are used to
denote variable accessors. The following translates to javascript syntax:

    cljs: a.hello
    ->js:  a["hello"] or a.hello

    cljs: a.|hello|
    ->js:  a[hello]

    cljs: a.|b.c.|d.e||.f.|g|
    ->js:  a[b.c[d.e]].f[g]

##### def.n

The `def.n` form is the same as `defn` but allows dot-notation within

```clojure
;;
;; Add Comparison
;; 
(defn add-vals [val1 val2]      ;; defn
 (+ (.. val1 -inner -count)
    (.. val2 -inner -count)))

(def.n add-vals [val1 val2]     ;; def.n
  (+ val1.inner.count 
     val2.inner.count)))

;;
;; Function call with index lookup
;;
(defn call-vals [app arg1 arg2 n]      ;; defn
  (.call (.-methods app)
      (.-value (aget arg1 n))
      (.-value (aget arg2 n))))

(def.n call-vals [app arg1 arg2 n]     ;; def.n
  (app.methods.call 
     arg1.|n|.value 
     arg2.|n|.value))

;;
;; Setters
;;
(defn bad-code [o val]         ;; defn
 (aset o "inner" "number" 10)
 (aset val "inner" "count"
       (+ 10 (aget o "inner" "count"))))

(def.n bad-code [o val]
 (! o.inner.number 10)
 (! val.inner.count
    (+ 10 o.inner.count)))
```

##### obj

`obj` is an extension of `js-object` with data constructor and self-reference
within the obj form, `{}` are interpreted as js-objects and `[]` are interpreted 
as arrays

```clojure
(def o1 (obj :arr [1 2 3 4]
             :l1 {:l2 [1 2 3 4]}))     
```

makes the equivalent object in javascript:

```javascript
var o1 = {"arr": [1,2,3,4],
          "l1": {"l2": [1, 2, 3, 4]}}
```

So essentially it is almost identical syntax but if there is a case where
scope is needed, you can nest `obj` calls to differ between scope

```clojure
(def v1 (obj :a 1           ;; self.a refers to this a
             :b {:a 2
                 :fn (fn [] self.a)})
(def v2 (obj :a 1
             :b  (obj :a 2  ;; self.a refers to this a
                      :fn (fn [] self.a)))]

(do.n     
  (js/console.log (v1.b.fn))   ;=> 1
  (js/console.log (v2.b.fn)))  ;=> 2
```

#### other extensions

There are other macros that are defined:

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
  -  `do.n` Do block
  -  `f.n` Lambdas (rarely needed)

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
```


#### Creating a Simple TDD Workflow

Angular TDD workflow can be automated using cljsbuild and karma

##### Step 1

Install [karma](http://github.com/karma-runner/karma) and the lein-cljsbuild plugin

##### Step 2 - Configuring Cljsbuild

Add `purnam` as a project dependency:

```clojure
[purnam "0.0.10"]
```

Put a `test` build in your `project.clj` file:

```clojure
:cljsbuild {:builds [{:source-paths ["src" "test/cljs"]   ;; <- where your 
                      :id "test",
                      :compiler
                      {:pretty-print true,
                       :output-to "<PATH-OF-TEST.JS>" ;; eg: "harness/test-app.js"
                       :optimizations :whitespace}}
                      ...
                      ... other builds
                      ...]}
```

##### Step 3 - Configuring Karma
Open a terminal screen in your project directory, run `karma init`:

```
Which testing framework do you want to use ?
Press tab to list possible options. Enter to move to the next question.
> jasmine

.....


Which files do you want to test ?
You can use glob patterns, eg. "js/*.js" or "test/**/*Spec.js".
Enter empty string to move to the next question.
> "<PATH-OF-TEST.JS>"

......

```

##### Step 4 - Running Tests
Run Karma in your first window

```bash
karma start
```

Open another terminal window in you project directory and run

```bash
lein cljsbuild auto test
```

##### Step 5 - Write a Test

Use any editor. Create a new test in the `test/cljs` folder

```clojure
;;; test-app.js ;;;
(ns purnam.test-js
  (:use [purnam.cljs :only [aget-in aset-in]])
  (:use-macros [purnam.jasmin :only [init-jasmin describe it is is-not equals]]))

(init-jasmin)

(describe 
  "my first test using purnam"
  [o1 (obj :array [1 2 3 4])]
  (it "describes something"
   (is o1.array.0 odd?)
   (is o1.array.1 even?)
   (is o1.array.2 even?)
   (is o1.array.3 4)
   (is o1.array.4 2)))
```

You will now see the output at the bottom of the karma window:

    Expression: (purnam.cljs/aget-in o1 ["array" "2"])
       Expected result: even?
       Actual result: 3
    Expression: (purnam.cljs/aget-in o1 ["array" "4"])
       Expected result: 2
       Actual result: undefined

##### Step 6 - Fix the Test

Make a change:

     (is o1.array.2 odd?) to (is o1.array.2 odd?)
     (is o1.array.4 js/undefined)

And now all the tests are passed:

    Safari 6.0 (Mac): Executed 1 of 1 SUCCESS (0.423 secs / 0.012 secs)




## License

Copyright © 2013 Chris Zheng

Distributed under the Eclipse Public License, the same as Clojure.
