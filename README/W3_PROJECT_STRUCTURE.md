The folder is set up so that testing and compilation can be fairly streamlined. As this is a clojurescript project, there are two types of unit tests that we have to run:

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

Running `lein cljs-build auto` in the purnam root directory not only builds our samples, it also built our unit-tests in the test folder and compiled it into one file located at `harness/unit/purnam-js-unit.js`. Karma is used to run our unit test and it is specifically set up to watch any changes so that new builds are tested incrementally.
  
    > cd ..                    # Go back into the purnam root directory

    > karma start              # Starts up the Karma Test Runner
                               # Opens up Chrome and Firefox and runs tests 

*.cljs unit tests for purnam-js and purnam-angular can be found in the `test` directory


[[◄ Back (Building Demos)|Building Demos]] `      ` [[Next (Extending Purnam) ►|Extending Purnam]]