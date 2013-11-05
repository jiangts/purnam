(ns midje-doc.purnam-guide
  (:use [purnam.cljs :only [aget-in aset-in]])
  (:use-macros [purnam.js :only [obj arr ? ?> ! !> f.n def.n def* def*n]]
               [purnam.test :only [init]]
               [purnam.test.sweet :only [fact]]))

[[{:hide true}]]
(init)

[[:chapter {:title "Installation"}]]

"Add to `project.clj` dependencies (use double quotes): 

    [im.chit/purnam '{{PROJECT.version}}']"

[[:chapter {:title "Motivation"}]]

"
[purnam](https://www.github.com/zcaudate/purnam) is a *clojurescript* library designed to provide better js interop as well as simple testing and documentation workflows. Although primarily designed for working with [angular.js](http://angularjs.org), it works great with any project requiring interface with external javascript library. Pure clojurescript libraries will also benefit with its unit-testing and documentation workflows. The library was written to solve a number of pain points that I experience in clojurescript development:

#### Better JS Interop

The first pain point was having to deal with the clojurish *.dot syntax* for javascript interop as well as a lack of functionality when working with native js objects. This made it especially hard for working with any external js library. Purnam offers:

- [`purnam.js`](#purnam-js) - a set of macros allowing javascript-like syntax for better interop 
- [`purnam.cljs`](#purnam-cljs) - functions for native objects and arrays
- [`purnam.types`](#purnam-types) - clojure protocols for native objects and arrays implemented on top of `purnam.cljs`.


#### In-Browser Testing

The second pain point was the lack of testing tools that worked within the browser. Even though testing with [phantom.js](http://phantomjs.com) was fine for non-browser code, I wanted something with more debugging power and so unit testing is integrated with the [karma](http://karma-runner.github.io/) test runner using two different test styles:

- [`purnam.test`](#purnam-test) - testing using [jasmine](http://pivotal.github.io/jasmine/) syntax
- [`purnam.test.sweet`](#purnam-test-sweet) - testing using [midje](https://github.com/marick/Midje) syntax (compatible with [`midje-doc`](https://www.github.com/zcaudate/lein-midje-doc))

#### Angularjs on Clojurescript

The third pain point was the code bloat I was experiencing when developing and testing *angular.js* code using javascript. It was very easy to complect modules within large *angular.js* applications and I wanted to use clojure syntax so that my code was smaller, more readable and easier to handle. Purnam offers:

- [`purnam.angular`](#purnam-angular) - a simple dsl for eliminating boilerplate *angular.js*
- [`purnam.test.angular`](#purnam-test-angular) - testing macros for eliminating more boilerplate test code for services, controllers, directives and filters

#### Integrated Documentation

The fourth pain point was the lack of documentation tools for clojurescript as well as clojure. `purnam` is compatible with [`midje-doc`](https://www.github.com/zcaudate/lein-midje-doc) so that the integrated testing and documentation [workflow](http://z.caudate.me/combining-tests-and-documentation/) can be also used in clojurescript.
"

[[:chapter {:title "Quickstart"}]]

"
The quickest way to start is to look at some sample projects:

- [Crafty.js Example](https://github.com/zcaudate/purnam-crafty-game) uses [`purnam.js`](#purnam-js)
- [Angular.js Example](https://github.com/zcaudate/purnam-angular-example) uses [`purnam.angular`](#purnam-angular) and [`purnam.test.angular`](#purnam-test-angular)
- [Karma Testing Example](https://github.com/zcaudate/puram-karma-testing) uses [`purnam.test`](#purnam-test) and [`purnam.test.sweet`](#purnam-test-sweet)
"

[[:section {:title "Native Javascript"}]]

"`obj` and `arr` in the `purnam.js` namespace allow nested objects and arrays to be constructed."

[[:section {:title "Objects"}]]

"The js code:"

[[{:lang "js" :numbered false}]]
[[:code 
"var user = {ids: [1, 2, 3], account: {username: 'user', password: 'pass'}}"]]

"Can be constructed very easily using `obj`:"

[[{:numbered false}]]
(def user (obj :ids [1 2 3] 
               :account {:username "user"
                         :password "pass"}))

"or using the `def*` form:"

[[{:numbered false}]]
(def* user {:ids [1 2 3] 
            :account {:username "user"
                      :password "pass"}})



"In contract, it is very difficult to define nested objects and arrays using native clojurescript. For example, the same "

[[:section {:title "Native Functions"}]]

(comment
  (defn square [x]
    (let [o (js-obj)
          v (aget x "value")]
      (aset o "value" (* v v)))
      o)


  (def.n square [x]
    (obj :value (* x.value x.value)))

  (def*n square [x]
    {:value (* x.value x.value)}))

"
"