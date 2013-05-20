### Overview ###

Angularjs convenience macros are defined in the `purnam.angular` namespace. To decrease boilerplate and code noise as well as increase code legibility. It is assumed that the developer is quite familiar with the ins and outs of angularjs.

### `def.module` ###
Usage:
```clojure
(def.module <MODULE NAME> [... <DEPENDENCIES> ...])
```
Sample:
```clojure
;; clojure
(def.module my.app [ui ui.bootstrap]))
```
```javascript
// javascript
angular.module('my.app', ['ui', 'ui.bootstrap'])
```

### `def.config` ###
Usage:
```clojure
(def.config <MODULE NAME> [... <PROVIDERS> ...]
   ... 
   <FUNCTION BODY>
   ...
   )
```
Sample:
```clojure
;; clojure
(def.config my.app [$locationProvider $routeProvider]
  (doto $locationProvider (.hashPrefix "!"))
  (doto $routeProvider
    (.when "" (obj :redirectTo "/home"))))
```
```javascript
// javascript
angular.module('my.app')
       .config(['$locationProvider', '$routeProvider', 
         function($locationProvider, $routeProvider){
           $locationProvider.hashPrefix("!");
           $routeProvider.when("", {redirectTo: "/home"});
       }]);
```

### `def.controller` ###
Usage:
```clojure
(def.controller <MODULE NAME>.<CONTROLLER NAME> [... <INJECTIONS> ...]
   ... 
   <CONTROLLER BODY>
   ...
   )
```
Sample:
```clojure
;; clojure
(def.controller my.app.SimpleCtrl [$scope]
  (! $scope.msg "Hello")
  (! $scope.setMessage (fn [msg] (! $scope.msg msg))))
```
```javascript
// javascript
angular.module('my.app')
       .controller('SimpleCtrl', ['$scope', function($scope){
         $scope.msg = "Hello"
         $scope.setMessage = function (msg){
           $scope.msg = msg;
         }}])
```


### `def.constant` ###
** DISCLAIMER ** I have never needed `constant` in my angularjs applications.

Usage:
```clojure
(def.constant <MODULE NAME>.<CONSTANT NAME>
    <CONSTANT>
   )
```
Sample:
```clojure
;; clojure
(def.constant my.app.MeaningOfLife 42)
```
```javascript
// javascript
angular.module('my.app')
       .constant('MeaningOfLife', 42);
```

### `def.directive` ###
Usage:
```clojure
(def.directive <MODULE NAME>.<DIRECTIVE NAME> [... <INJECTIONS> ...]
   
   ;; Initialisation code to return a function:
   
   (fn [$scope element attrs]
      .... <FUNCTION> ....
   )
```
Sample:
```clojure
;;clojure
(def.directive my.app.appWelcome []
  (fn [$scope element attrs]
    (let [html (element.html)]
      (element.html (str "Welcome <strong>" html "</strong>")))))
```
```javascript
// javascript
angular.module('my.app')
  .directive('appWelcome', [function() {
    return function($scope, element, attrs) {
       var html = element.html();
       element.html('Welcome: <strong>' + html + '</strong>');
    };}]);
```

### `def.filter` ###
```clojure
(def.filter <MODULE NAME>.<FILTER NAME> [... <INJECTIONS> ...]
   
   ;; Initialisation code to return a function:
   
   (fn [input & args]
      .... <FUNCTION> ....
   )
```
Sample:
```clojure
;; clojure
(def.filter my.app.range []
  (fn [input total]
    (when input
      (doseq [i (range (js/parseInt total))]
        (input.push i))
      input)))
```
```javascript
// javascript
angular.module('my.app').
  filter('range', [function() {
    return function(input, total) {
      if(!input) return null;
      total = parseInt(total);
      for (var i=0; i <total; i++)
        input.push(i);
      return input;
    };
  }]);
```
  
### `def.service` ###
Usage:
```clojure
(def.service <MODULE NAME>.<SERVICE NAME> [... <INJECTIONS> ...]
    <RETURN OBJECT>
   )
```
Sample:
```clojure
;; clojure
(def.service my.app.LoginService []
  (obj :user {:login "login"
              :password "secret"
              :greeting "hello world"}
       :changeLogin (fn [login]
                      (! this.user.login login))))
```
```javascript
// javascript
angular.module('my.app')
       .service('LoginService', [function(){
         return {user: {:login "login",
                        :password "secret",
                        :greeting "hello world"},
                 changeLogin: function (login){
                                  this.user.login = login;}}}]);
```

### `def.value` ###
** DISCLAIMER ** I have never need `value` before in my angularjs applications

Usage:
```clojure
(def.value <MODULE NAME>.<VALUE NAME>
    <VALUE>
   )
```
Sample:
```clojure
;; clojure
(def.value my.app.MeaningOfLife 42)
```
```javascript
// javascript
angular.module('my.app')
       .value('MeaningOfLife', 42);
```

[[◄ Back (API purnam.test)|API   purnam test]] `      ` [[Next (API purnam.test.angular) ►|API   purnam test angular]]
