# 0 - Quickstart

The guide will explain the following:

- [A. How to look at the demos](#A)
- [B. How this project is arranged](#B)
- [C. How to contribute](#C)

<a id="A"></a>
## A. Building the Demos

The project depends on:

- [lein cljsbuild](https://github.com/emezeske/lein-cljsbuild)
- [nodejs](http://nodejs.org/) and [npm](https://npmjs.org/) (for running .cljs tests)
- [yeoman](http://yeoman.io/)

If you do not have these, please follow the links and install them first.

##### Step 1. Clone the project

Open a terminal, go to your development directory and grab this project

    > git clone https://github.com/zcaudate/purnam.git

##### Step 2. Build the .js files

In the same window, type:

    > lein sub install         # This installs the .jar files in your sub directory
    > lein cljsbuild :auto     # This will output the *.js demo files

All demo `*.cljs` files are located in the `samples` folder. 

The .html files are build in the `harness/app/samples` directory. However, to look at the angularjs demos, we have to run the server

##### Step 3. Running a server

Open up a new terminal window. Purnam uses the grunt project scaffolding tool to spin up a webserver:

    > cd harness               # Go into the harness directory
    > npm install              # Install all project dependencies
    > grunt server             # Starts the Server

It should open up a browser to http://localhost:9000

There is a list of samples that you can navigate to and play with.

    - angularjs
    - craftyjs
    - More Coming!

<a id="B"></a>
## B. Project Structure

The project is setup in this way so that testing and compilation can be fairly streamlined. Because this is a clojurescript project, there are two types of unit tests that we have to run

   1. CLJ tests for our macros and macro helper functions
   2. CLJS tests to verify that the compiled *.js code works

##### Code Organisation

Purnam consists of:

    purnam                # root directory
    |
    |-- project.clj       # definition of sub-projects and cljs-compilation targets
    |-- karma.conf.js     # configuration for the karma test runner (for CLJS Tests)
    |-- purnam-js         
    |   |-- src           # core js macros, testing macros
    |   |-- test          # CLJ tests for core libary 
    |
    |-- purnam-angular     
    |   |-- src           # angular and angular testing macros
    |   |-- test          # CLJ tests for angular library
    
    |-- harness           # external libraries and files for demos and testing
    |   |-- Grunt.js      # configuration for the grunt server
    |   |-- app
    |       |-- demos     # static .html resources for demos
    |       |-- scripts   # compiled output
    |
    |-- demos             # demos and sample programs
    |
    |-- test              # CLJS tests for both purnam-js and purnam-angular projects.
    |                     # All *.cljs files are compiled to /harness/unit/purnam-js-unit.js and
                            then run are run using karma 

######  CLJ Tests
CLJ tests are written using `midje` and can be run in either the purnam root directory, or in each of the sub directories (purnam-js and purnam-angular). In your root directory, run:

    > lein midje :autotest

This will run all the CLJ unit tests in both `purnam-js` and `purnam-angular` projects. You can also type `lein midje :autotest` in any of the sub-folders to run only those specific tests

###### CLJS Tests

Running `lein cljs-build auto` in the purnam root directory not only build our samples, but it also built our unit-tests in the test folder and compiled it into one file located at `harness/unit/purnam-js-unit.js`. Karma is used to run our unit test and it is specifically set up to watch any changes so that new builds are tested incrementally.
  
    > cd ..                    # Go back into the purnam root directory

    > karma start              # Starts up the Karma Test Runner
                               # Opens up Chrome and Firefox and runs tests 

*.cljs unit tests for purnam-js and purnam-angular can be found in the `test` directory

<a id="C"></a>
## C. Extending Purnam

You can help extend purnam in three ways:

##### 1. Demos, demos and more demos!!

Please contribute your toys and experiments using purnam. Just make sure that you follow these conventions:

   - *.cljs code should go into the <root>/demo/<yourdemo> folder
   - *.html and all user resources go in the <root>/harness/app/demos/<yourdemo> directory
   - Third party libraries go into <root>/harness/app/libs directory

You should also add a seperate cljsbuild target in `project.clj` for example:
   
    {:source-paths ["purnam-js/src" "demo/<yourapp>"],
     :id "<yourapp>-demo",
     :compiler
     {:pretty-print true,
      :output-to "harness/app/scripts/<yourapp>-demo.js",
      :optimizations :whitespace}}

##### 2. Macros for JS Frameworks

I wrote purnam-angular because I really liked angularjs but I could not follow any of the javascript, especially after functions have been nested 5 closures deep. I'm sure that there are other libraries that can be simplified with the use of clojure macros. If you wish to write macros for additional js-frameworks, create a project, eg `purnam-ember` in the root directory and go crazy! 

##### 3. Functionality, Documentation, Tests and Fixes

Feel free to contribute in these aspects. I can be contacted via email: z (at) caudate (dot) me for any feedback/suggestions

### Other Pages


