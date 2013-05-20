(defproject purnam "0.1.0-alpha"
  :description "A better javascript experience on clojurescript"
  :url "http://www.github.com/zcaudate/purnam"
  :license {:name "The MIT License"
            :url "http://opensource.org/licencses/MIT"}
  :dependencies [[org.clojure/clojure "1.5.1"]
                 ;;[purnam/purnam-js      "0.1.0-alpha"]
                 ;;[purnam/purnam-angular "0.1.0-alpha"]
                 ]
  :sub 
    ["purnam-js"
     "purnam-angular"]
  :profiles {:dev {:dependencies [[midje "1.5.1"]]}}
  :plugins [[lein-sub "0.2.1"]
            [lein-cljsbuild "0.3.0"]
            [lein-midje "3.0.1"]]
  :test-paths ["purnam-js/src" "purnam-js/test" "purnam-angular/src" "purnam-angular/test"]
  :cljsbuild
  {:builds
   [{:source-paths ["purnam-js/src" "purnam-angular/src" "test"],
     :id "js-test",
     :compiler
     {:pretty-print true,
      :output-to "harness/unit/purnam-js-unit.js",
      :optimizations :whitespace}}
      {:source-paths ["purnam-js/src" "demos/crafty"],
       :id "crafty-demo",
       :compiler
       {:pretty-print true,
        :output-to "harness/app/scripts/crafty-demo.js",
        :optimizations :whitespace}}
    {:source-paths ["purnam-js/src" "purnam-angular/src" "demos/angular"],
     :id "angular-demo",
     :compiler
     {:pretty-print true,
      :output-to "harness/app/scripts/angular-demo.js",
      :optimizations :whitespace}}]})
