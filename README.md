# purnam

`purnam` - Javascript Language Extensions for Clojurescript

### Installation

In your project file, add

```clojure
[im.chit/purnam "0.1.8"]
```

### Starting Points:

- [Documentation](https://docs.caudate.me/purnam/)

- Examples
  - [Angular.js Example](https://github.com/zcaudate/purnam-angular-example)
  - [Crafty.js Example](https://github.com/zcaudate/purnam-crafty-game)
  - [Karma Testing Example](https://github.com/zcaudate/purnam-karma-testing)

### What's New

#### 0.3.0
- Bug fix for sets in js-expand
- Moved namespace around
- Ported fluokitten to cljs

todos:
- array - fapply
- object - fmap fapply pure

##### 0.1.8
- Bug fix for `describe.controller`

##### 0.1.6
- midje styles tests in the `purnam.test.sweet` namespace

### purnam
is a *clojurescript* library designed to provide better clojurescript/javascript interop, testing and documentation tools to the programmer. It also has very comprehensive modules for [angular.js](http://angularjs.org) applications. 

Current projects requiring interface with external javascript libraries will greatly benefit from this library. 'Pure' clojure/clojurescript libraries will also benefit with its unit-testing and documentation workflows. The library was written to solve a number of pain points that I have experienced in clojurescript development:

#### Better JS Interop

The first pain point was having to deal with the clojurish `(.dot syntax)` for javascript interop as well as a lack of functionality when working with native js objects. This made it especially hard for working with any external js library. Purnam offers:

- [purnam.native](http://docs.caudate.me/purnam/#purnam-cljs) - functions for native objects and arrays
- [purnam.core](http://docs.caudate.me/purnam/#purnam-js) - a set of macros allowing javascript-like syntax for better interop 
- [purnam.types](http://docs.caudate.me/purnam/#purnam-types) - clojure protocols for native objects and arrays


#### In-Browser Testing

The second pain point was the lack of testing tools that worked within the browser. Even though testing with [phantom.js](http://phantomjs.com) was fine for non-browser code, I wanted something with more debugging power and so unit testing is integrated with the [karma](http://karma-runner.github.io/) test runner using two different test styles:

- [purnam.test](http://docs.caudate.me/purnam/#purnam-test) - testing using [jasmine](http://pivotal.github.io/jasmine/) syntax
- [purnam.test.sweet](http://docs.caudate.me/purnam/#purnam-test-sweet) - testing using [midje](https://github.com/marick/Midje) syntax (compatible with [`midje-doc`](https://www.github.com/zcaudate/lein-midje-doc))

#### Angularjs on Clojurescript

The third pain point was the code bloat I was experiencing when developing and testing *angular.js* code using javascript. It was very easy to complect modules within large *angular.js* applications and I wanted to use clojure syntax so that my code was smaller, more readable and easier to handle. Purnam offers:

- [purnam.angular](http://docs.caudate.me/purnam/#purnam-angular) - a simple dsl for eliminating boilerplate *angular.js*
- [purnam.test.angular](http://docs.caudate.me/purnam/#purnam-test-angular) - testing macros for eliminating more boilerplate test code for services, controllers, directives and filters

#### Integrated Documentation

The fourth pain point was the lack of documentation tools for clojurescript as well as clojure. `purnam` is compatible with [midje-doc](https://www.github.com/zcaudate/lein-midje-doc) so that the integrated testing and documentation [workflow](http://z.caudate.me/combining-tests-and-documentation/) can be also used in clojurescript.

### A Taste of Purnam

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

##### Angular JS

```clojure
;; purnam.angular

(def.module my.app [])

(def.config my.app [$routeProvider]
  (-> $routeProvider
      (.when "/" (obj :templateUrl "views/main.html"))
      (.otherwise (obj :redirectTo "/"))))

(def.controller my.app.MainCtrl [$scope $http]
  (! $scope.msg "")
  (! $scope.setMessage (fn [msg] (! $scope.msg msg)))
  (! $scope.loginQuery
     (fn [user pass]
       (let [q (obj :user user
                    :pass pass)]
         (-> $http
             (.post "/login" q)
             (.success (fn [res]
                         (if (= res "true")
                           (! $scope.loginSuccess true)
                           (! $scope.loginSuccess false))))
             (.error (fn [] (js/console.log "error!!")))))))
```

##### AngularJS Testing
```clojure
;; purnam.test.angular

(describe.controller
 {:doc "A sample controller for testing purposes"
  :module my.app
  :controller MainCtrl}

 (it "should be able to change the message within the $scope"
  (is $scope.msg "Hello") 
  (do ($scope.setMessage "World!")
      (is $scope.msg "World!"))

  (do ($scope.setMessage "Angular Rocks!")
      (is $scope.msg "Angular Rocks!"))))
```


## License

Copyright © 2013 Chris Zheng

Distributed under the The MIT License.