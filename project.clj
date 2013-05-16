(defproject purnam "0.1.0-SNAPSHOT"
  :description "Clojurescript macros for working with various javascript frameworks
                (currently angularjs and jasmin)"
  :url "http://www.github.com/zcaudate/purnam"
  :license {:name "Eclipse Public License"
            :url "http://www.eclipse.org/legal/epl-v10.html"}
  :dependencies [[org.clojure/clojure "1.5.1"]]
  :profiles {:dev {:dependencies [[midje "1.5.1"]]}}
  :plugins [[lein-cljsbuild "0.3.0"]]
  :test-paths ["test/clj"]
  :cljsbuild {:builds [{:source-paths ["src" "test/cljs" "samples/src" "samples/test"],
                         :id "test",
                         :compiler
                         {:pretty-print true,
                          :output-to "harness/test-purnam.js",
                          :optimizations :whitespace}}
                       {:source-paths ["src" "samples/src"],
                        :id "samples",
                        :compiler
                        {:pretty-print true,
                         :output-to "resources/app/scripts/samples.js",
                         :optimizations :whitespace}}]})
