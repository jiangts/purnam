# 0 - Quickstart

The guide will explain the following:

- A. How to look at the demos
- B. How this project is arranged
- C. How to fork and add to Purnam

## A. Building the Demos

The project depends on:

- lein cljsbuild
- nodejs and npm (for running .cljs tests)
- yeoman

If you do not have these, please follow the links and install them first.

##### Step 1. Clone the Project

Open a terminal, go to your development directory and grab this project

    > git clone https://github.com/zcaudate/purnam.git

##### Step 2. Build the Demos

In the same window, type:

    > lein sub install         # This installs the .jar files in your sub directory
    > lein cljsbuild :auto     # This will output the *.js demo files

All demo `*.cljs` files are located in the `samples` folder. 

The .html files are build in the `harness/app/samples` directory. However, to look at the angularjs demos, we have to run the server

##### Step 3. Running A Server

Open up a new terminal window. Purnam uses the grunt project scaffolding tool to spin up a webserver:

    > cd harness               # Go into the harness directory
    > npm install              # Install all project dependencies
    > grunt server             # Starts the Server

It should open up a browser to http://localhost:9000

There is a list of samples that you can navigate to and play with.

##### Step 4. Running Unit Tests

Open up another terminal window. `lein cljs-build` also built a unit-test file to `harness/unit/purnam-js-unit.js`. We can use the Karma to run our tests.
  
      
    > cd ..                    # Go back into the purnam root directory
    
    > karma start              # Starts up the Karma Test Runner
                               # Opens up Chrome and Firefox and runs tests 

*.cljs unit tests for purnam.js and purnam.angular can be found in the `test` directory


## B. Project Structure
