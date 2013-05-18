### Objectives ###

This is a step by step guide on how to quickly get a clojurescript development environment set up to enable your experience of writing clojurescript applications to be as smooth as possibly.

**Section A - Project Setup [[►|Your-First-Project#section-a---project-setup]]**
  1. Creating and Configuring your ClojureScript Project
  [[►|Your-First-Project#a1-creating-and-configuring-your-clojurescript-project]]
  2. Creating a Testing Harness using Yo, Grunt and Bower
  [[►|Your-First-Project#a2-creating-a-testing-harness-using-yo-grunt-and-bower]
  3. Creating a Karma Configuration for TDD
  [[►|Your-First-Project#a3-creating-a-karma-configuration-for-tdd]]

**Section B - Test Driven Development for Clojurescript [[►|Your-First-Project#section-b---test-driven-development-for-clojurescsript]]**
  1. Writing your First Function [[►|Your-First-Project#b1-writing_your_first_function]]
  2. Writing your First Test [[►|Your-First-Project#b2-writing_your_first_Test]]
  
**Section C - Light Table**
  1. Light Table Setup 
  2. Connecting to Your Project
  3. Interactive Developement

### Prerequisites ###

You will require:

- [leiningen](https://github.com/technomancy/leiningen)
- [nodejs](http://nodejs.org/) and [npm](https://npmjs.org/)
- [yeoman](http://yeoman.io/)
- [karma](http://karma-runner.github.io/0.8/index.html)


If you do not have these, please follow the links and install them first.

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
                 [purnam "0.1.0-alpha"]
                 
;; -- Add the `lein-cljsbuild` plugin
;;
  :plugins [[lein-cljsbuild "0.3.0"]]

;; -- Configure lein cljsbuild --
;; We will configure two builds: unit tests and application.
;; This project is assumed to be a strictly clojurescript
;; project for purpose of simplicity
;; 
  :cljsbuild    
  {:builds
   [;;
    ;; -- This is the definition for our unit tests build
    ;;
    {:id "tests"
     :source-paths ["src" "test"]
     :compiler {:output-to "harness/unit/test-myapp.js"}}
    ;;
    ;; -- This is the definition for our application build
    ;;
    {:id "app"
     :source-paths ["src"]
     :compiler {:output-to "harness/app/scripts/myapp.js"}}]})
```

###### Run cljsbuild
Save your `project.clj` file. Now in the original terminal, download dependencies and run `lein cljsbuild auto` to automatically watch and compile files under the `:source-paths` defined in your cljsbuild options in the previous section.

```bash
> cd myapp
> lein deps
  
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

```

Any changes to `*.clj` and `*.cljs` files under `src` and `test` will be compiled to *.js to `harness/unit/test-myapps.js` and `harness/app/scripts/myapp.js`.


##### A.2 Creating a Testing Harness using Yo, Grunt and Bower

Keep `lein cljsbuild` running and open up a new terminal window. Navigate to your project root and create a folder name `harness`. 

```bash
> mkdir harness
> cd harness
```
This will be where we install our harness scaffolding using yo, grunt and bower. We will create the most basic project scaffolding using the `yo webapp` command.
```bash
> yo webapp

 #     _-----_
 #   |       |
 #   |--(o)--|   .--------------------------.
 #   `---------´  |    Welcome to Yeoman,    |
 #    ( _´U`_ )   |   ladies and gentlemen!  |
 #    /___A___\   '__________________________'
 #     |  ~  |
 #   __'.___.'__
 # ´   `  |° ´ Y `

 # Out of the box I include HTML5 Boilerplate, jQuery and Modernizr.
```
It will ask you some questions about Sass and RequireJS. Answer `n` to both for now.
```bash
 # Would you like to include Twitter Bootstrap for Sass? (Y/n) 
 > n
 # Would you like to include RequireJS (for AMD support)? (Y/n) 
 > n
```
Let `yo` do its thing and generate a project scaffold for you as well as installs all project dependencies. After it is finished, We can start-up a server.
```bash
> grunt server
 # Running "livereload-start" task
 # ... Starting Livereload server on 35729 ...

 # Running "connect:livereload" (connect) task
 # Starting connect web server on localhost:9000.

 # Running "open:server" (open) task

 # Running "watch" task
 # Watching app/scripts/{,*/}*.coffee
 # Watching test/spec/{,*/}*.coffee
 # Watching app/styles/{,*/}*.{scss,sass}
 # Watching app/*.html,{.tmp,app}/styles/{,*/}*.css,{.tmp,app}/scripts/{,*/}*.js,app/images/{,*/}*.{png,jpg,jpeg,gif,webp,svg}
```
the `grunt server` command will start a server, open up a browser and watch file changes so that the page will automatically reload on any asset changes in the `harness/app` directory. The page it brings up is a default page. We will be replacing this with our own in Section C.

##### A.3 Creating a Karma configuration for TDD
So now there are two running processes: 
  - `lein cljsbuild auto` for compiling our clojurescript files.
  - `grunt server` for serving our clojurescript application.

Although we will not be using the application server until Section C, we can keep both of the running for now.

Open up a third terminal window and navigate to the `harness` directory. We will initialise a karma configuration file to watch all files in `harness/unit` and run tests on change.

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
 > unit/*.js  
 >

 # Any files you want to exclude ?
 # You can use glob patterns, eg. "**/*.swp".
 # Enter empty string to move to the next question.
 >

 # Do you want Testacular to watch all the files and run the tests on change ?
 # Press tab to list possible options.
 > yes

Config file generated at "<DEV-DIRECTORY>/myapp/harness/karma.conf.js". 
```  

Now that we have configured karma, lets start it up:
```bash
> karma start
 # INFO [karma]: Karma server started at http://localhost:9876/
 # INFO [launcher]: Starting browser Chrome
 # INFO [Chrome 28.0 (Mac)]: Connected on socket id cxMy4YG1CWlwJzt3yjST
 # Chrome 28.0 (Mac): Executed 0 of 0 SUCCESS (0.112 secs / 0 secs)
```

We have now setup our Clojurescript TDD pipeline. We are now ready to code!

### Section B - Test Driven Development for Clojurescript ###

Recap of what happened in Section A:

- The clojurescript project was configured to use `cljsbuild`. purnam was included as a dependency. The command `lein cljsbuild auto` watches `src` and `test` directories for changes; compiles unit tests to `harness/unit/test-myapp.js` and application code to `harness/app/scripts/myapp.js`. 
- The skeleton for a webserver was set up in the `harness` directory using the `yo webapp` command . The webserver was launch using `grunt server`.
- Karma Test Runner was configured and started

Essentially, we have created the following setup:

    <editor> --->  [src]  -> cljsbuild -> [harness/app/scripts/myapp.js] -> grunt server -> <BROWSER OUTPUT>
              l->  [test] -> cljsbuild -> [harness/unit/test-myapp.js] -> karma -> <UNIT TESTING OUTPUT> 

In your root folder, remove generated scaffold files
```bash
> rm src/myapp/core.clj
> rm test/myapp/core_test.clj
```

Create a new file at `test/myapp/test_core.cljs` with the following :

```clojure
(ns myapp.test-core)

(js/console.log "Hello World")
```

You will see on the `lein cljsbuild` terminal:
```bash
 # Compiling "harness/unit/test-myapp.js" from ["src" "test"]...
 # Successfully compiled "harness/unit/test-myapp.js" in 1.150932 seconds.
```

Then over on the `karma` terminal
```bash
 # INFO [watcher]: Changed file "/Users/Chris/dev/play/myapp/harness/unit/myapp-tests.js".
 # Chrome 28.0 (Mac) LOG: 'Hello World'
 # Chrome 28.0 (Mac): Executed 0 of 0 SUCCESS (0.396 secs / 0 secs)

We can define our very first function:


(ns myapp.test-app)

(js/console.log "Hello World")

INFO [watcher]: Changed file "/Users/Chris/dev/play/myapp/harness/unit/test-myapp.js".
Chrome 29.0 (Mac) LOG: 'Hello'
Chrome 29.0 (Mac): Executed 0 of 0 SUCCESS (0.428 secs / 0 secs)

