(ns midje-doc.quickstart
  (:require [purnam.test]
            [purnam.native])
  (:use-macros [purnam.core :only [obj arr ? ?> ! !> f.n def.n def* def*n]]
               [purnam.test :only [describe is is-not it fact facts]]))

[[:chapter {:title "Quickstart"}]]

"
The quickest way to start is to look at some sample projects:

- [Crafty.js Example](https://github.com/purnam/example.purnam.game) uses [`purnam.core`](#purnam-js)
- [Karma Testing Example](https://github.com/purnam/example.purnam.test) uses [`purnam.test`](#purnam-test) and [`purnam.test.sweet`](#purnam-test-sweet)
"

[[:section {:title "Native Datastructures"}]]

"[obj](#obj), [arr](#arr) and [def*](#raw) in the [purnam.core](#purnam-core) namespace allow nested objects and arrays to be constructed. The examples below show equivalent objects in javascript and clojurescript"

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
"The [purnam.native](#purnam-native) namespace provide for idiomatic clojure syntax to be used:"

[[{:title "Idiomatic Clojure Protocols"}]]
(fact 
  (count (obj :a 1 :b 2 :c 3)) => 3
  
  (get (obj :a 1 :b 2 :c 3) "a") => 1
  
  (nth (arr :0 :1 :2 :3 :4) 3) => :3)
  
"The [purnam.native.functions](#purnam-native-functions) namespace also provide additional manipulation functions like [js-merge](#js-merge), [js-merge-nil](#js-merge-nil) and [js-deep-copy](#js-deep-copy). More examples can be seen in the [api]((#purnam-native-functions))."

[[:section {:title "DSL for Testing"}]]

"Choose between two styles of sytax for testing clojurescript code - jasmine style or midje style (in the [purnam.test](#purnam-test) namespace). Or you can mix and match both of them together."

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
