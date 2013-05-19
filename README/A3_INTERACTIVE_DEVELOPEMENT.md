### Objectives ###

This is a step by step guide on how to configure your project for interactive developement with Light Table. 

**A - Project Setup**
  1. [[Configuring Cljsbuild |Interactive Development |Interactive-Development#a1-adding-another-build-to-your-cljsbuild]]
  2. [[Scaffolding with Yeoman |Interactive-Development#a2-creating-a-testing-harness-using-yo]]
  3. [[Viewing your Clojurescript Application |Interactive-Development#a3-viewing-your-clojurescript-application]]

**B - Connecting to Light Table**
  1. [[How Light Table Works |]]
  2. [[The Instarepl |]]

### Prerequisites ###

You will require:

- [leiningen](https://github.com/technomancy/leiningen)
- [yeoman](http://yeoman.io/)

If you do not have these already installed, please follow the links and install/create them first.

###Section A - Project Setup

##### A.1 Adding another build to your cljsbuild

We need to add another cljsbuild entry to our project.clj file:

```clojure
(defproject myapp "0.1.0-SNAPSHOT"
  :description "FIXME: write description"
  :url "http://example.com/FIXME"
  :license {:name "Eclipse Public License"
            :url "http://www.eclipse.org/legal/epl-v10.html"}
  :dependencies [[org.clojure/clojure "1.5.1"]
                 [purnam "0.1.0-alpha"]
  :plugins [[lein-cljsbuild "0.3.0"]]
  :cljsbuild    
  {:builds
   [{:id "tests"
     :source-paths ["src" "test"]
     :compiler {:output-to "harness/unit/test-myapp.js"}}
    ;;
    ;; -- This is the definition for our application build
    ;;
    {:id "app"
     :source-paths ["src"]
     :compiler {:output-to "harness/app/scripts/myapp.js"}}]})
```

This new build only looks at files in the `src` directory. Now cljsbuild will generate two *.js outputs. Save this file and start the `lein cljsbuild auto` in your terminal (or restart if it is already running).

##### A.2 Creating a Testing Harness using Yo

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
yeoman or `yo` pretty much handles everything. It will generate a project scaffold as well as install all project dependencies. After it is finished, We can start-up a server.
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
the `grunt server` command will start a server, open up a browser and watch file changes so that the page will automatically reload on any asset changes in the `harness/app` directory. The page it brings up is a default page.

##### A.3 Viewing your Clojurescript Application

Open `src/myapp/core.cljs` and add a new function:
```clojure
(ns myapp.core
  (:use [purnam.cljs :only [aset-in aget-in]])
  (:use-macros [purnam.js :only [def.n]]))

(def.n add-and-log [a b]
  (let [answer (+ a.value b.value)]
     (js/console.log answer)
     answer))

;; New alert function added
(defn hello-alert []
  (js/alert "Hello Clojurescript!"))
```

Edit `harness/app/index.html` and place the following code

```html
        <script src="scripts/main.js"></script>
        // New code here:
        <script src="scripts/myapp.js"></script>
        <script>
          myapp.core.hello_alert();
        </script>
```

Now go back to your browser, which would have already reloaded. http://localhost:9000, An alert should pop up - "Hello Clojurescript!"

###Section B - Connecting to Light Table

TBD.