(defproject purnam "0.1.0-alpha"
  :description "A better javascript experience on clojurescript"
  :url "http://www.github.com/zcaudate/purnam"
  :license {:name "The MIT License"
            :url "http://opensource.org/licencses/MIT"}
  :dependencies 
    [[purnam/purnam-js      "0.1.0-alpha"]
     [purnam/purnam-angular "0.1.0-alpha"]]
  :profiles {:dev {:dependencies [[midje "1.5.1"]]}}
  :plugins [[lein-sub "0.2.1"]
            [lein-cljsbuild "0.3.0"]
            [lein-midje "3.0.1"]]
  :test-paths ["purnam-js/test" "purnam-angular/test"]
  :cljsbuild
  {:builds
   [{:source-paths ["purnam-js/src" "test"],
     :id "js-test",
     :compiler
     {:pretty-print true,
      :output-to "harness/unit/purnam-js-unit.js",
      :optimizations :whitespace}}
      {:source-paths ["purnam-js/src" "samples/crafty"],
       :id "crafty-demo",
       :compiler
       {:pretty-print true,
        :output-to "harness/app/scripts/crafty-demo.js",
        :optimizations :whitespace}}
    {:source-paths ["purnam-js/src" "purnam-angular/src" "samples/angular"],
     :id "angular-demo",
     :compiler
     {:pretty-print true,
      :output-to "harness/app/scripts/angular-demo.js",
      :optimizations :whitespace}}]})
