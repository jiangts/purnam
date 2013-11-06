(ns midje-doc.quickstart
  (:use [purnam.cljs :only [aget-in aset-in]])
  (:use-macros [purnam.js :only [obj arr ? ?> ! !> f.n def.n def* def*n]]
               [purnam.angular :only [def.module def.controller def.service]]
               [purnam.test :only [init describe is is-not it]]
               [purnam.test.sweet :only [fact facts]]
               [purnam.test.angular :only [describe.ng describe.controller it-uses]]))

[[{:hide true}]]
(init)

[[:chapter {:title "Quickstart"}]]

"
The quickest way to start is to look at some sample projects:

- [Crafty.js Example](https://github.com/zcaudate/purnam-crafty-game) uses [`purnam.js`](#purnam-js)
- [Angular.js Example](https://github.com/zcaudate/purnam-angular-example) uses [`purnam.angular`](#purnam-angular) and [`purnam.test.angular`](#purnam-test-angular)
- [Karma Testing Example](https://github.com/zcaudate/puram-karma-testing) uses [`purnam.test`](#purnam-test) and [`purnam.test.sweet`](#purnam-test-sweet)
"

[[:section {:title "Native Datastructures"}]]

"[obj](#obj), [arr](#arr) and [def*](#raw) in the [purnam.js](#purnam-js) namespace allow nested objects and arrays to be constructed. The examples below show equivalent objects in javascript and clojurescript"

[[{:lang "js" :title "js object"}]]
[[:code 
"var user = {ids: [1, 2, 3], account: {username: 'user', password: 'pass'}}"]]

[[{:title "cljs object - obj"}]]
(def user (obj :ids [1 2 3] :account {:username "user" :password "pass"}))

[[{:title "cljs object - def*"}]]
(def* user {:ids [1 2 3] :account {:username "user" :password "pass"}})

"Arrays are constructed using [arr](#arr). The two examples below show equivalent arrays in javascript and clojurescript:"

[[{:lang "js" :title "js array"}]]
[[:code 
"var kids = [{name: 'Sam' age: 3}, {name: 'Bob' age: 10}]"]]

[[{:title "cljs array - arr"}]]
(def kids (arr {:name "Sam" :age 3} {:name "Bob" :age 10}))

[[{:title "cljs array - def*"}]]
(def* kids [{:name "Sam" :age 3} {:name "Bob" :age 10}])

[[:section {:title "Native Functions"}]]

"[def.n](#dot-defn) and [f.n](#dot-fn) allow functions to be defined using the javascript `dot-notation` syntax. The following are examples of the square function defined in javascript and clojurescript."

[[{:lang "js" :title "js function"}]]
[[:code 
"square = function(x) {return x.value * x.value}"]]

[[{:title "cljs function - def.n"}]]
(def.n square [x]
  (obj :value (* x.value x.value)))

[[{:title "cljs function - f.n"}]]
(def square 
  (f.n [x]
    (obj :value (* x.value x.value))))

"As a comparison, here is the same function in clojurescript without the macro helpers."

[[{:title "cljs function - defn"}]]
(defn square [x]
  (let [o (js-obj)
        v (aget x "value")]
    (aset o "value" (* v v))
    o))

[[:section {:title "Native Protocols"}]]
"The [purnam.types](#purnam-types) namespace provide for idiomatic clojure syntax to be used:"

[[{:title "Idiomatic Clojure Protocols"}]]
(fact 
  (count (obj :a 1 :b 2 :c 3)) => 3
  
  (get (obj :a 1 :b 2 :c 3) "a") => 1
  
  (nth (arr :0 :1 :2 :3 :4) 3) => :3)
  
"The [purnam.cljs](#purnam-cljs) namespace also provide additional manipulation functions like [js-merge](#js-merge), [js-merge-nil](#js-merge-nil) and [js-deep-copy](#js-deep-copy). More examples can be seen in the [api]((#purnam-cljs))."

[[:section {:title "DSL for Testing"}]]

"Choose between two styles of sytax for testing clojurescript code - jasmine style (default style in the [purnam.test](#purnam-test) namespace) or midje style (in the [purnam.test.sweet](#purnam-test-sweet) namespace). Or you can mix and match both of them together."

[[{:title "testing - jasmine style"}]]
(describe "Addition"
  (it "should add things"
    (is (+ 1 1) 2)
    (is (+ 1 2) 3)
    (is (+ 1 3) 4)
    (is-not (+ 1 4) 0)))

[[{:title "testing - midje style"}]]
(fact 
  (fact [[{:doc "Addition should add things"}]]
    (+ 1 1) => 2
    (+ 1 2) => 3
    (+ 1 3) => 4
    (+ 1 4) => #(not= 0 %)))
    
"Although there are currently more features available when using the jasmine style syntax, using midje style syntax also allow compilation of your test files into beautiful documentation using [midje-doc](https://www.github.com/zcaudate/lein-midje-doc). The current document has been generated in this way."

[[:section {:title "DSL for Angular"}]]

"[Angular.js](http://angularjs.org) is the premier javascript framework for building large-scale, single-page applications. [purnam.angular](#purnam-angular) allows clean, readable definitions of angular.js modules and tests. Below is the definition of an angular.js module, a storage service, and a controller that uses the service."

[[{:title "module definition"}]]

(def.module myApp [])

(def.service myApp.storage []
  (let [store (atom {})]
    (obj :put (fn [k v] (swap! store #(assoc % k v)))
         :get (fn [k] (js/console.log  k (@store k))
                (@store k))
         :clear (fn [] (reset! store {}))
         :print (fn [] (js/console.log (clj->js @store))))))

(def.controller myApp.AppCtrl [$scope storage]
  (! $scope.key "hello")
  (! $scope.val "world")  
  (! $scope.printStore storage.print) 
  (! $scope.clearStore storage.clear)
  (! $scope.putStore storage.put)
  (! $scope.getStore storage.get))

"The full api can be seen [here](#purnam-angular)."

[[:section {:title "Testing for Angular"}]]
  
"Testing angular.js apps are quite brain intensive when using pure javascript. The [purnam.test.angular](#purnam-test-angular) namespace takes care of all the injections for us"

[[{:title "testing services"}]]
(describe.ng
  {:doc "Storage"
   :module myApp
   :inject [storage]}
  (it "allows saving and retriving"
    (storage.put "hello" "world")
    (is (storage.get "hello") "world")
    
    (storage.clear)
    (is (storage.get "hello") nil)))

[[{:title "testing controllers"}]]
(describe.controller 
  {:doc "AppCtrl"
   :module myApp
   :controller AppCtrl}
   
 (it "has key and val within the scope"
   (is $scope.key "hello")
   (is $scope.val "world"))
 
 (it "has put and get functionality"
   ($scope.putStore $scope.key $scope.val)
   (is ($scope.getStore "hello") "world"))
   
 (it "additional tests"
   (! $scope.key "bye")
   ($scope.putStore $scope.key $scope.val)
   (is ($scope.getStore "hello") nil)
   (is ($scope.getStore "bye") "world")))

"More complete examples can be seen [here](https://www.github.com/zcaudate/purnam-angular-examples)"

