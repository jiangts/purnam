(defproject im.chit/purnam "0.4.3"
  :description "Language extensions for clojurescript"  
  :url "http://www.github.com/purnam/purnam"
  :license {:name "The MIT License"
            :url "http://opensource.org/licencses/MIT"}
  :dependencies [[org.clojure/clojure "1.5.1"]
                 [im.chit/purnam.native "0.4.3"]
                 [im.chit/purnam.core   "0.4.3"]
                 [im.chit/purnam.test   "0.4.3"]]
  :test-paths ["test/clj"]
  :profiles {:dev {:dependencies [[org.clojure/clojurescript "0.0-2138"]
                                  [midje "1.6.0"]]
                   :plugins [[lein-cljsbuild "1.0.0"]
                             [lein-midje "3.1.3"]]}}
  :documentation {:files {"doc/index"
                        {:input "test/midje_doc/purnam_guide.clj"
                         :title "purnam"
                         :sub-title "Language extensions for clojurescript"
                         :author "Chris Zheng"
                         :email  "z@caudate.me"
                         :tracking "UA-31320512-2"}}}
  :cljsbuild {:builds [{:source-paths ["src" "test"]
                        :compiler {:output-to "target/purnam-guide.js"
                                   :optimizations :whitespace
                                   :pretty-print true}}]})