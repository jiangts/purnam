You can help extend purnam in three ways:

##### 1. Demos, demos and more demos!!

Please contribute your toys and experiments using purnam. Just make sure that you follow these conventions:

   - *.cljs code should go into the <root>/demos/<yourdemo> folder
   - *.html and all user resources go in the <root>/harness/app/demos/<yourdemo> directory
   - Third party libraries go into <root>/harness/app/libs directory

You should also add a seperate cljsbuild target in `project.clj` for example:
   
    {:source-paths ["purnam-js/src" "demos/<yourapp>"],
     :id "<yourapp>-demo",
     :compiler
     {:pretty-print true,
      :output-to "harness/app/scripts/<yourapp>-demo.js",
      :optimizations :whitespace}}

##### 2. Macros for JS Frameworks

I wrote purnam-angular because I really liked angularjs but I could not follow any of the javascript, especially after functions have been nested 5 closures deep. I'm sure that there are other libraries that can be simplified with the use of clojure macros. If you wish to write macros for additional js-frameworks, create a project, eg `purnam-ember` in the root directory and go crazy! 

##### 3. Functionality, Documentation, Tests and Fixes

Feel free to contribute in these aspects. I can be contacted via email: z (at) caudate (dot) me for any feedback/suggestions


[[◄ Back (Project Structure)|Project Structure]] `      ` [[Next (Reference) ►|purnam.core]]

[[◄ Home (Home)|Home]] 