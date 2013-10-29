### Objectives ###

This is a step by step guide on how to set up a clojurescript test-driven development environment, allowing immediate feedback to code changes during the clojurescript development process. The development workflow will be set up as follows:

    (1) DEVELOPER changes *.cljs code in [src] and [test] directories
          ^       |
          |       |-> (2) [src] and [test] directories are watched by cljsbuild and changes
          ^           trigger recompilation of *.cljs code to [harness/unit/test-myapp.js]
          |                |
          ^                |-> (3) [harness/unit/test-my-app.js] is watched by karma and
          |                    any changes to the file trigger rerunning of unit tests
          ^                         | 
          |                         |-> (4) KARMA UNIT TEST RESULTS
          ^                                  | 
          | < . < . < . < . < . < . < . < . <-
                  Near Instant Feedback


**A - Project Setup**
  1. [[Creating and Configuring your ClojureScript Project |Your-First-Project#a1-creating-and-configuring-your-clojurescript-project]]
  2. [[Creating a Karma Configuration for TDD |Your-First-Project#a2-creating-a-karma-configuration-for-tdd]]

**B - Test Driven Development for Clojurescript**
  1. [[Hello World in Clojurescript |Your-First-Project#b1-hello-world-in-clojurescript]]
  2. [[Your First Test |Your-First-Project#b2-writing-your-first-test]]
  3. [[Your First Purnam Function |Your-First-Project#b3-your-first-purnam-function]]

### Prerequisites ###

You will require:

- [leiningen](https://github.com/technomancy/leiningen)
- [karma](http://karma-runner.github.io/0.8/index.html)

If you do not have these already installed, please follow the links and install them first.

### Section A - Project Setup ###

##### A.1 Creating and configuring your ClojureScript Project

###### Create New Project
Open a terminal, go to your development directory and create a brand new leiningen project:

    > lein new myapp

###### Configure `project.clj`
Open `myapp/project.clj`. We will be adding a few entries into the original project in order to configure our project correctly.

```clojure
(defproject myapp "0.1.0-SNAPSHOT"
  :description "FIXME: write description"
  :url "http://example.com/FIXME"
  :license {:name "Eclipse Public License"
            :url "http://www.eclipse.org/legal/epl-v10.html"}

;; -- Add purnam to dependencies 
;;
  :dependencies [[org.clojure/clojure "1.5.1"]
                 [purnam "0.1.0-alpha"]]
                 
;; -- Add the `lein-cljsbuild` plugin
;;
  :plugins [[lein-cljsbuild "0.3.0"]]

;; -- Configure lein cljsbuild --
;; We will configure our unit test build here
;; 
  :cljsbuild    
  {:builds
   [{:id "tests"
     :source-paths ["src" "test"]
     :compiler {:output-to "harness/unit/test-myapp.js"}}]})
```

###### Run cljsbuild
Save your `project.clj` file. Now in the original terminal, download dependencies and run `lein cljsbuild auto` to automatically watch and compile files under `:source-paths` defined in your cljsbuild options in the previous section.

```bash
> cd myapp
> lein deps
 # If lein-cljsbuild is not installed, it will be:  
 # Retrieving lein-cljsbuild/lein-cljsbuild/0.3.0/lein-cljsbuild-0.3.0.pom from clojars
 # Retrieving lein-cljsbuild/lein-cljsbuild/0.3.0/lein-cljsbuild-0.3.0.jar from clojars
    
> lein cljsbuild auto

 # Compiling ClojureScript.
 # Retrieving purnam/purnam/0.1.0-alpha/purnam-0.1.0-alpha.pom from clojars
 # Retrieving purnam/purnam-js/0.1.0-alpha/purnam-js-0.1.0-alpha.pom from clojars
 # Retrieving purnam/purnam-angular/0.1.0-alpha/purnam-angular-0.1.0-alpha.pom from clojars
 # Retrieving purnam/purnam-angular/0.1.0-alpha/purnam-angular-0.1.0-alpha.jar from clojars
 # Retrieving purnam/purnam-js/0.1.0-alpha/purnam-js-0.1.0-alpha.jar from clojars
 # Retrieving purnam/purnam/0.1.0-alpha/purnam-0.1.0-alpha.jar from clojars
 # Compiling "harness/unit/test-myapp.js" from ["src" "test"]...
 # Successfully compiled "harness/unit/test-myapp.js" in 2.968875 seconds.
```

If you leave this process running, any changes to `*.clj` and `*.cljs` files under `src` and `test` will trigger compilation to `harness/unit/test-myapps.js`.

##### A.2 Creating a Karma configuration for TDD
There is now a running process: 
  - `lein cljsbuild auto` for compiling our clojurescript files.

Open up another terminal window in the project root directory. We will initialise a karma configuration file to watch all files in `harness/unit` and run tests on change.

```bash
> karma init

 # Which testing framework do you want to use ?
 # Press tab to list possible options. Enter to move to the next question.
 > jasmine

 # Do you want to use Require.js ?
 # This will add Require.js adapter into files.
 # Press tab to list possible options. Enter to move to the next question.
 > no

 # Do you want to capture a browser automatically ?
 # Press tab to list possible options. Enter empty string to move to the next question.
 > Chrome

 # Which files do you want to test ?
 # You can use glob patterns, eg. "js/*.js" or "test/**/*Spec.js".
 # Enter empty string to move to the next question.
 > harness/unit/*.js  
 >

 # Any files you want to exclude ?
 # You can use glob patterns, eg. "**/*.swp".
 # Enter empty string to move to the next question.
 >

 # Do you want Testacular to watch all the files and run the tests on change ?
 # Press tab to list possible options.
 > yes

Config file generated at "<ROOT>/karma.conf.js".
```

Now that we have configured karma, lets start it up:
```bash
> karma start
 # INFO [karma]: Karma server started at http://localhost:9876/
 # INFO [launcher]: Starting browser Chrome
 # INFO [Chrome 28.0 (Mac)]: Connected on socket id cxMy4YG1CWlwJzt3yjST
 # Chrome 28.0 (Mac): Executed 0 of 0 SUCCESS (0.112 secs / 0 secs)
```

The Clojurescript TDD pipeline has been setup. We are ready to code!

**Recap** of what just happened:

- The clojurescript project was configured to use `cljsbuild`. `purnam` was included as a dependency. The command `lein cljsbuild auto` watches `src` and `test` directories for changes; compiles unit tests to `harness/unit/test-myapp.js` 
- Karma Test Runner was configured and started to watch any file changes in `harness/unit` and run Unit Tests if it has detected changes to the directory.

### Section B - Test Driven Development for Clojurescript ###

##### B.1 Hello World in Clojurescript
In your root folder, remove generated scaffold files from your new project
```bash
> rm src/myapp/core.clj
> rm test/myapp/core_test.clj
```
Create a new file at `test/myapp/test_core.cljs` with the following two lines:
```clojure
(ns myapp.test-core)

(js/console.log "Hello World")
```
Now save the file.

You will see over on the `lein cljsbuild` terminal:
```bash
 # Compiling "harness/unit/test-myapp.js" from ["src" "test"]...
 # Successfully compiled "harness/unit/test-myapp.js" in 1.150932 seconds.
```

Then over on the `karma` terminal
```bash
 # INFO [watcher]: Changed file "<ROOT>/harness/unit/myapp-tests.js".
 # Chrome 28.0 (Mac) LOG: 'Hello World'
 # Chrome 28.0 (Mac): Executed 0 of 0 SUCCESS (0.396 secs / 0 secs)
```
Congratulations! You are now writing TDD style Clojurescript!
 
##### B.2 Writing Your First Test

###### Update Test File
Make the following changes to `test/myapp/test_core.cljs`
```clojure
(ns myapp.test_core
;;
;;-- Load our helper functions (required by macros)
;;
  (:use [purnam.cljs :only [aget-in aset-in]])
;;
;;-- Load our macros
;; 
  (:use-macros [purnam.test :only [init describe it is is-not]]))

;;-- Initialise Test Suite (Requires)
;;
  (init)

;;-- Write our First Test
(describe
  (it "One Plus One Equals" 
     (is (+ 1 1) 11)))
```

Over on the karma screen, there should be the following error
```bash
 # INFO [watcher]: Changed file "<ROOT>/harness/unit/test-myapp.js".
 # Chrome 28.0 (Mac)  One Plus One Equals FAILED
 #  Expression: (+ 1 1)
 #  	   Expected result: 11
 #  	   Actual result: 2
```

###### Going Green
Lets fix our test:
```clojure
(describe
  (it "One Plus One Equals" 
     (is (+ 1 1) 2)))
```
Now we will see that karma lets our test pass
```bash
 # INFO [watcher]: Changed file "<ROOT>/harness/unit/test-myapp.js".
 # Chrome 28.0 (Mac): Executed 1 of 1 SUCCESS (0.423 secs / 0.024 secs)
```

###### Adding More Cases
Lets add more test cases to our `describe` form:

```clojure
(describe
 {:doc "A better test description"
  :bindings [one-plus-one (+ 1 1)]}
 (it "One plus one is:" 
   (is one-plus-one 2)
   (is-not one-plus-one 11)
   (is one-plus-one even?)
   (is-not one-plus-one odd?)
   (is (js* "1+1") 2)
   (is (js* "'1'+'1'") "11")))
```

We have created our first suite of tests!

##### B.3 Your First Purnam Function
Once the workflow is operation, we can start writing our library functions

Create a new file at `src/myapp/core.cljs` with the following code:
```clojure
(ns myapp.core
  (:use [purnam.cljs :only [aset-in aget-in]])
  (:use-macros [purnam.js :only [def.n]]))

(def.n add-and-log [a b]
  (let [answer (+ a.value b.value)]
     (js/console.log answer)
     answer))
```

Save this file.

Now we add tests for `add-and-log` in `test/myapp/test_core.cljs`:

```clojure
;; Modify the ns declaration to include additional 
;; macros and functions
;;
(ns myapp.test_core
  (:use [purnam.cljs :only [aget-in aset-in]]
        [myapp.core :only  [add-and-log]]) ;; <--- Added
  (:use-macros [purnam.js :only [! def.n obj]] <-- Added 
               [purnam.test :only [init describe it is is-not]]))

;;
;;  ..... Old Test Code ....
;;

;; Tests for add-and-log
(describe
  {:doc "add-and-log will add inner values"
   :bindings [a1 (obj :value 1)
              a2 (obj :value 2)
              a3 (obj :value 3)]}
  (it "performs addition"
    (is (add-and-log a1 a2) 3)
    (is (add-and-log a1 a3) 4)
    (is (add-and-log a2 a3) 5)))
```
Save this files and we should get immediate feedback from karma.

```bash
 # INFO [watcher]: Changed file "<ROOT>/harness/unit/test-myapp.js".
 # Chrome 28.0 (Mac) LOG: 3
 # Chrome 28.0 (Mac) LOG: 4
 # Chrome 28.0 (Mac) LOG: 5
 # Chrome 28.0 (Mac): Executed 2 of 2 SUCCESS (0.421 secs / 0.028 secs)
```

Congratulations! You have written your first purnam function and test-suite. We are now ready to move on to [[Interactive Development]].

[[◄ Back (Home)|Home]] `      ` [[Next (Interactive Development) ►|Interactive Development]]