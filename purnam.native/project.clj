(defproject im.chit/purnam.native "0.4.0"
  :description "Native Javascript Methods"
  :url "http://www.github.com/zcaudate/purnam"
  :license {:name "The MIT License"
            :url "http://opensource.org/licencses/MIT"}
  :dependencies [[org.clojure/clojure "1.5.1"]]
  :profiles {:dev {:dependencies [[org.clojure/clojurescript "0.0-2080"]
                                  [midje "1.6.0"]]
                   :plugins [[lein-midje "3.1.3"]
                             [lein-cljsbuild "1.0.0"]]}}
   :cljsbuild
   {:builds
    [{:id "js-test",
      :compiler {:pretty-print true,
                 :output-to "harness/purnam-native-unit.js",
                 :optimizations :whitespace}}]})