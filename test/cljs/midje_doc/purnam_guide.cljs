(ns midje-doc.purnam-guide)

[[:chapter {:title "Installation"}]]

"Add to `project.clj` dependencies (use double quotes): 

    [im.chit/purnam \"{{PROJECT.version}}\"]"

[[:chapter {:title "Motivation"}]]

"
[purnam](https://www.github.com/zcaudate/purnam) is a *clojurescript* library designed to provide better clojurescript/javascript interop, testing and documentation tools to the programmer. It also has very comprehensive modules for [angular.js](http://angularjs.org) applications. 

Current projects requiring interface with external javascript libraries will greatly benefit from this library. 'Pure' clojure/clojurescript libraries will also benefit with its unit-testing and documentation workflows. The library was written to solve a number of pain points that I have experienced in clojurescript development:

#### Better JS Interop

The first pain point was having to deal with the clojurish `(.dot syntax)` for javascript interop as well as a lack of functionality when working with native js objects. This made it especially hard for working with any external js library. Purnam offers:

- [purnam.native](#purnam-cljs) - functions for native objects and arrays
- [purnam.core](#purnam-js) - a set of macros allowing javascript-like syntax for better interop 
- [purnam.types](#purnam-types) - clojure protocols for native objects and arrays

#### In-Browser Testing

The second pain point was the lack of testing tools that worked within the browser. Even though testing with [phantom.js](http://phantomjs.com) was fine for non-browser code, I wanted something with more debugging power and so unit testing is integrated with the [karma](http://karma-runner.github.io/) test runner using two different test styles:

- [purnam.test](#purnam-test) - testing using [jasmine](http://pivotal.github.io/jasmine/) syntax
- [purnam.test.sweet](#purnam-test-sweet) - testing using [midje](https://github.com/marick/Midje) syntax (compatible with [`midje-doc`](https://www.github.com/zcaudate/lein-midje-doc))

#### Integrated Documentation

The fourth pain point was the lack of documentation tools for clojurescript as well as clojure. `purnam` is compatible with [midje-doc](https://www.github.com/zcaudate/lein-midje-doc) so that the integrated testing and documentation [workflow](http://z.caudate.me/combining-tests-and-documentation/) can be also used in clojurescript.
"

[[:file {:src "test/cljs/midje_doc/quickstart.cljs"}]]

[[:file {:src "test/cljs/midje_doc/api/purnam_cljs.cljs"}]]

[[:file {:src "test/cljs/midje_doc/api/purnam_js.cljs"}]]

[[:file {:src "test/cljs/midje_doc/api/purnam_types.cljs"}]]

[[:file {:src "test/cljs/midje_doc/api/purnam_test.cljs"}]]

[[:file {:src "test/cljs/midje_doc/api/purnam_test_sweet.cljs"}]]

[[:chapter {:title "End Notes"}]]

"For any feedback, requests and comments, please feel free to lodge an issue on github or contact me directly.

Chris."