(defproject im.chit/purnam "0.1.8"
  :description "A better javascript experience on clojurescript"
  :url "http://www.github.com/zcaudate/purnam"
  :license {:name "The MIT License"
            :url "http://opensource.org/licencses/MIT"}
  :dependencies [[org.clojure/clojure "1.5.1"]
                 [org.clojure/clojurescript "0.0-1978"]]
  :profiles {:dev {:dependencies [[midje "1.5.1"]]}}
  :plugins [[lein-cljsbuild "0.3.4"]
            [lein-midje "3.0.1"]]
  :source-paths ["src/cljs"]
  :test-paths ["test/clj"]
  :documentation {:files {"doc/index"
                          {:input "test/cljs/midje_doc/purnam_guide.cljs"
                           :title "purnam"
                           :sub-title "javascript on clojurescript"
                           :author "Chris Zheng"
                           :email  "z@caudate.me"
                           :tracking "UA-31320512-2"}}}
  :cljsbuild
  {:builds
   [{:source-paths ["src/cljs" "test/cljs"],
     :id "js-test",
     :compiler {:pretty-print true,
                :output-to "harness/unit/purnam-js-unit.js",
                :optimizations :whitespace}}]})