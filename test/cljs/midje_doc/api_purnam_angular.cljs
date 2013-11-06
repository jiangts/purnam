(ns midje-doc.api-purnam-angular-test
  (:use [purnam.cljs :only [aget-in aset-in js-equals]])
  (:use-macros [purnam.js :only [f.n def.n obj arr]]
               [purnam.angular :only [def.module def.controller def.service]]
               [purnam.test :only [init describe is it]]
               [purnam.test.sweet :only [fact facts]]
               [purnam.test.angular :only [describe.ng describe.controller it-uses]]))

[[{:hide true}]]
(init)

[[:chapter {:title "purnam.angular" :tag "purnam-angular"}]]


[[:section {:title "def.module" :tag "def-module"}]]

"`def.module` provides an easy way to define angular modules. The following clojurescript code generates the equivalent javascript code below it:"

(comment
  (def.module my.app [ui ui.bootstrap]))

[[{:lang "js"}]]
[[:code "angular.module('my.app', ['ui', 'ui.bootstrap'])"]]

"Typically, the `def.module` is at the very top of the file, one module is defined for one clojure namespace."

[[:section {:title "def.config" :tag "def-config"}]]

"`def.config` is used to setup module providers. "

(comment
  (def.config <MODULE NAME> [... <PROVIDERS> ...]
     ... 
     <FUNCTION BODY>
     ...     ))
     
"It is most commonly used to setup the routing for an application."

(comment
  (def.config my.app [$locationProvider $routeProvider]
    (doto $locationProvider (.hashPrefix "!"))
    (doto $routeProvider
      (.when "" (obj :redirectTo "/home")))))

"The equivalent javascript code can be seen below."

[[{:lang "js"}]]
[[:code "angular.module('my.app')
         .config(['$locationProvider', '$routeProvider', 
               function($locationProvider, $routeProvider){
                 $locationProvider.hashPrefix('!');
                 $routeProvider.when('', {redirectTo: '/home'});
           }]);"]]
           
[[:section {:title "def.controller" :tag "def-controller"}]]

"`def.controller` defines a controller. The typical usage is like this:"
(comment
  (def.controller <MODULE NAME>.<CONTROLLER NAME> [... <INJECTIONS> ...]
     ... 
     <CONTROLLER BODY>
     ... ))

"A sample controller"

(comment       
  (def.controller my.app.SimpleCtrl [$scope]
     (! $scope.msg "Hello")
     (! $scope.setMessage (fn [msg] (! $scope.msg msg)))))

"Produces the equivalent javascript code:"

[[{:lang "js"}]]     
[[:code "angular.module('my.app')
          .controller('SimpleCtrl', ['$scope', function($scope){
                    $scope.msg = 'Hello'
                    $scope.setMessage = function (msg){
                      $scope.msg = msg;
                    }}])"]]

[[:section {:title "def.directive" :tag "def-directive"}]]
[[:section {:title "def.filter" :tag "def-filter"}]]
[[:section {:title "def.constant" :tag "def-constant"}]]
[[:section {:title "def.value" :tag "def-value"}]]
[[:section {:title "def.service" :tag "def-service"}]]
[[:section {:title "def.factory" :tag "def-factory"}]]
;;[[:section {:title "def.provider" :tag "def-provider"}]]
