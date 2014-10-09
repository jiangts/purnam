(ns midje-doc.purnam-guide)

[[:chapter {:title "Installation"}]]

"Add to `project.clj` dependencies: 

`[im.chit/purnam `\"`{{PROJECT.version}}`\"`]`"

"Looking for documentation for `purnam.angular`? The library has been renamed and moved to a new home at [http://purnam.github.io/gyr](http://purnam.github.io/gyr)"

[[:chapter {:title "Motivation"}]]

"
[purnam](https://www.github.com/purnam/purnam) is a *clojurescript* library designed to provide better clojurescript/javascript interop, testing and documentation tools to the programmer. Current projects requiring interface with external javascript libraries will greatly benefit from `purnam` language extensions. 'Pure' clojure/clojurescript libraries will also benefit with its unit-testing and documentation workflows. The library was written to solve a number of pain points that are experienced in clojurescript development:

#### Better JS Interop

The first pain point was having to deal with the clojurish `(.dot syntax)` for javascript interop as well as a lack of functionality when working with native js objects. This made it especially hard for working with any external js library. `purnam` offers:

- [purnam.native.functions](#purnam-native-functions) - functions for native objects and arrays
- [purnam.native](#purnam-native) - clojure protocols for native objects and arrays
- [purnam.core](#purnam-js) - a set of macros allowing javascript-like syntax for better interop

#### In-Browser Testing

`purnam.test` was written to allow clojurescript tests to work with the karma test runner. There are various advantages to using the [karma](http://karma-runner.github.io/) test runner over existing clojurescript solutions:
  - Supports a variety of browsers including Chrome, Firefox, Safari, Phantomjs and [Node Webkit](https://github.com/intelligentgolf/karma-nodewebkit-launcher)
  - Uses [nodejs](http://nodejs.org) and so has a very quick startup time
  - It is supported by [Google](http://google.com) and the [Angularjs](http://angularjs.org) team
  - Very active community with lots of users and development activity

Furthermore, the javascript `dot.notational.style` to be used for better native integration. Since version `0.4`, there is much better support for error reporting and the library can now be used on its own, outside of all other purnam libraries. Currently two flavors are supported describing clojurescript tests:
  - [Jasmine style](#describe)  - `describe`, `it` and `is`. It gives a more fully featured interface and has [async](#async) support (`runs`, `waits-for`)
  - [Midje style](#fact)  - `fact` and *=>*. It can be combined with [MidjeDoc](https://www.github.com/zcaudate/lein-midje-doc) to generate documentation from unit tests (the current article being an auto-generated document using this technique).

#### Integrated Documentation

The third pain point was the lack of documentation tools for clojurescript as well as clojure. `purnam` is compatible with [midje-doc](https://www.github.com/zcaudate/lein-midje-doc) so that the integrated testing and documentation [workflow](http://z.caudate.me/combining-tests-and-documentation/) can be also used in clojurescript.
"

[[:file {:src "test/cljs/midje_doc/quickstart.cljs"}]]

[[:file {:src "test/cljs/midje_doc/api_purnam_native_functions.cljs"}]]

[[:file {:src "test/cljs/midje_doc/api_purnam_native.cljs"}]]
               
[[:chapter {:title "purnam.core" :tag "purnam-core"}]]

[[:file {:src "test/cljs/midje_doc/api_purnam_core.cljs"}]]

[[:chapter {:title "purnam.test" :tag "purnam-test"}]]

[[:file {:src "test/cljs/midje_doc/api_purnam_test_jasmine.cljs"}]]
    
[[:file {:src "test/cljs/midje_doc/api_purnam_test_sweet.cljs"}]]

[[:file {:src "test/clj/midje_doc/purnam_test_guide.clj"}]]

[[:chapter {:title "End Notes"}]]

"For any feedback, requests and comments, please feel free to lodge an issue on github or contact me directly.

Chris."