# purnam

Javascript Language Extensions for Clojurescript

[![Build Status](https://travis-ci.org/purnam/purnam.png?branch=master)](https://travis-ci.org/purnam/purnam)

## Installation

In your project file, add

```clojure
[im.chit/purnam "0.4.3"]
```

#### Documention

- [all in one](https://purnam.github.io/purnam/)
- [purnam.core](https://purnam.github.io/purnam.core/)
- [purnam.test](https://purnam.github.io/purnam.test/)
- [purnam.native](https://purnam.github.io/purnam.native/)

#### Examples

- [Crafty.js Example](https://github.com/purnam/example.purnam.game)
- [Karma Testing Example](https://github.com/purnam/example.purnam.test)

#### Related Projects

- [purnam.common](https://github.com/purnam/purnam.common)
- [brahmin](https://github.com/purnam/brahmin)
- [gyr](https://github.com/purnam/gyr)


#### History

In its earliest incarnation, purnam was more or less a set of scattered ideas about how to play nicely with existing javascript libraries using clojurescript. What initially started off as experimental language extensions for working with clojurescript and [angularjs](http://angularjs.org) has matured into a synergistic set of libraries and workflows for crafting clojurescript applications (now decoupled from angular)

The original [purnam](https://github.com/purnam/purnam/tree/old) library was way too big.  It has now been broken up into more reasonably sized pieces. The core functionality of purnam have been seperated into independent libraries so that developers can have a choice about including one or all of them inside their projects.

- [purnam.core](https://github.com/purnam/purnam.core) provides macros for better and more intuitive javascript interop.
- [purnam.test](https://github.com/purnam/purnam.test) provides macros for testing with the karma test runner.
- [purnam.native](https://github.com/purnam/purnam.native) provides utility function and clojure protocol support for native arrays and objects.

Support for [angular.js](http://angularjs.org) was a major reason that `purnam` became a popular choice for frontend development. From a functional point of view however, the language extensions could be completely decoupled from angularjs support and so `purnam.angular` has been renamed to [gyr](https://github.com/purnam/gyr).

As the `purnam` style syntax could be extended to [meteorjs](https://www.meteor.com), [reactjs](facebook.github.io/react/‎) as well as the thousands of javascript libaries out there, decoupling the code-walking component [purnam.common](https://github.com/purnam/purnam.common) from the main project took the most of the time in the redesign. Hopefully, this library will enable other developers to write their own purnam-flavored macros.

Lastly, `purnam.category` namespace has been moved to [brahmin](https://github.com/purnam/brahmin). I started looking at category theory after a conversation with [Logan Campbell](https://github.com/logaan) about [conditional restarts](https://github.com/zcaudate/ribol) for asynchronous calls. Despite porting [fluokitten](https://github.com/uncomplicate/fluokitten) to clojurescript, I still don't understand what a Monad is. So it is a highly experimental (but fun) project.

#### Mailing List

A Google Group for purnam has been setup [here](https://groups.google.com/forum/#!forum/purnam). Comments, Questions, Feedback, Contributions are most definitely welcome!


## Quick Start

##### Functions
```javascript
// javascript
function square(x){
  return {value: x.value * x.value};
}
```
```clojure
;; clojurescript + purnam
(def.n square [x]
  (obj :value (* x.value x.value)))

;; or
(def*n square [x]
  {:value (* x.value x.value)})

```
```clojure
;; clojurescript
(defn square [x]
  (let [o (js-obj)
        v (aget x "value")]
    (aset o "value" (* v v)))
    o)
```

##### Objects
```javascript
// javascript
var user = {id: 0 
            account: {username: "user"
                      password: "pass"}}
```
```clojure
;; clojurescript + purnam
(def user (obj :id 0 
               :account {:username "user"
                         :password "pass"}))
;; or

(def* user {:id 0 
            :account {:username "user"
                      :password "pass"})})
```
```clojure
;; clojurescript
(def user
  (let [acc (js-obj)
        user (js-obj)]
    (aset acc "username" "user")
    (aset acc "password" "pass")
    (aset user "account" acc)
    (aset user "id" 0)
    user)) 

;; clojurescript using clj->js (slower)
(def user 
  (clj->js {:id 0 
            :account {:username "user"
                      :password "pass"})})
```

##### Midje Tests
```
(fact [[{:doc "an example test description"
         :globals [ka "a"
                   kb "b"]
         :vars [o (obj :a 1 :b 2 :c 3)]}]]

 "dot notation for native objects"
 o.a => 1
 (+ o.a o.b o.c) => 6

 "support for both native and cljs comparisons"
 o => (obj :a 1 :b 2 :c 3)
 [1 2 3 4] => [1 2 3 4]
 
 "support for function comparison"
  2 => even?
  3 => (comp not even?)
  
 "globals"
  o.|ka| => 1
  (+ o.|ka| o.|kb|) => 3
  
  "vars are allowed to be rebound"
  (! o (arr [1 2 3]
            [4 5 6]
            [7 8 9]))
            
  (- o.2.2 o.0.0) => 8)
```


## License

Copyright © 2014 Chris Zheng

Distributed under the The MIT License.