(defproject purnam "0.1.0-alpha"
  :description "A better javascript experience on clojurescript"
  :url "http://www.github.com/zcaudate/purnam"
  :license {:name "The MIT License"
            :url "http://opensource.org/licencses/MIT"}
  :dependencies 
    [[purnam/purnam-js      "0.1.0-alpha"]
     [purnam/purnam-angular "0.1.0-alpha"]]
  :plugins [[lein-sub "0.2.1"]
            [lein-cljsbuild "0.3.0"]]
  :cljsbuild
  {:builds
   [{:source-paths ["purnam-js/src" "test/cljs"],
     :id "js-test",
     :compiler
     {:pretty-print true,
      :output-to "harness/purnam-js.js",
      :optimizations :whitespace}}
    {:source-paths ["purnam-js/src" "purnam-js/samples/crafty"],
     :id "crafty-demo",
     :compiler
     {:pretty-print true,
      :output-to "resources/app/scripts/crafty-demo.js",
      :optimizations :whitespace}}]})
