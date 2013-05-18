You will require:

- [leiningen](https://github.com/technomancy/leiningen)
- [nodejs](http://nodejs.org/) and [npm](https://npmjs.org/)
- [yeoman](http://yeoman.io/)

If you do not have these, please follow the links and install them first.

##### Step 1. Clone the project

Open a terminal, go to your development directory and grab this project

    > git clone https://github.com/zcaudate/purnam.git

##### Step 2. Build the .js files

In the same window, type:

    > lein sub install         # Command compiles the project and installs .jar files to your .m2 directory
    > lein cljsbuild :auto     # Command compiles .cljs src files and output *.js demo files.

All demo `*.cljs` sources files are located in the `demos` folder. The `lein cljsbuild` command compiles the source files to `harness/app/scripts`.

All demo resources such as `*.html` files and graphics are located in `harness/app/demos`. Some demos work without a browser but others do not (the angularjs demos). We have to run the server in order to view all working demos.

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

[[◄ Back (Home)|Home]] `      ` [[Next (Your First Project) ►|Your First Project]]