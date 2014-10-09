(ns midje-doc.purnam-test-guide)

[[:chapter {:title "Running Tests"}]]

"To run tests, the [karma](http://karma-runner.github.io/) test library is required. It can be installed using [npm](https://www.npmjs.org). There are two files that needs to be updated:
   - `/project.clj`
   - `/karma.conf.js`

In `project.clj`, add your clojurescript builds. Usually the test code is built to a seperate file than the application code. In the following case, the tests are compiled to `target/example-test.js`."

[[{:title "project.clj - cljsbuild"}]]
(comment
  :cljsbuild {:builds [{:source-paths ["src"]
                        :compiler {:output-to "target/example.js"
                                   :optimizations :whitespace
                                   :pretty-print true}}
                       {:source-paths ["src", "test/cljs"]
                        :compiler {:output-to "target/example-test.js"
                                   :optimizations :whitespace
                                   :pretty-print true}}]})

"A `karma.conf.js` file is required at the root level of your project. It is created by running `karma init` in the root project directory and following a bunch of instructions:

![karma-setup](https://raw.github.com/purnam/purnam/master/karma-setup.png)

Make sure that `autoWatch` is set to `true` and that the the compiled output from cljsbuild is included as one of the files."

[[{:lang "js" :title "karma.conf.js" :tag "c-py-1"}]]
[[:code 
  "....
  
  files: [
    'target/example-test.js'
  ],
  
  autoWatch: true,
  
  ...."]]

"
Run in one window: `lein cljsbuild auto`.

Run in another window: `karma start`.

Note that you will have to restart karma if the *.js file was not found before the test runner starts. The following video shows the entire process in detail:

[![karma-testing](https://raw.github.com/purnam/example.purnam.test/master/karma-testing.png)](http://www.youtube.com/watch?v=9mryE5vggR0&feature=youtu.be)
"

[[:chapter {:title "Generating Documentation"}]]

"If the midje flavor testing is used, [MidjeDoc](https://www.github.com/zcaudate/lein-midje-doc) can be used to auto generate documentation from test cases. The following should be added to `project.clj`:"

[[{:title "project.clj - documentation"}]]
(comment
  :profiles {:dev {:plugins [...
                             [lein-midje-doc "0.0.18"]
                             ... ]}}
                             
  :documentation {:files {"index"
                          {:input "test/cljs/midje_doc/example_guide.cljs"
                           :title "example"
                           :sub-title "this is an example"
                           :author "Your Name"
                           :email  "example@email.com"
                           :tracking "UA-31320512-2"}}})

"
Running in a third window: `lein midje-doc` will watch files for changes and generate a pretty viewable output to `index.html` on any change. A demonstration of how this works can be seen here:

[![Demo](https://raw.github.com/zcaudate/lein-midje-doc/master/documentation_tool.png)](http://youtu.be/8FjvhDPIUWE)
"
