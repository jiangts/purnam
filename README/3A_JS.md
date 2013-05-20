### Overview ###

Angularjs testing macros are defined in the `purnam.test.angular` namespace. Used to decrease boilerplate and code noise, increase developer accuracy as well as increase code legibility.

<a name="describeng"></a><a name="ng"></a>
### `describe.ng` and `ng` - automatic injection
```clojure
(def.service my.app.SimpleService []
  (obj :user {:login "login"
              :password "secret"
              :greeting "hello world"}
       :changeLogin (fn [login]
                      (! this.user.login login))))

(describe.ng
 {:doc  "Sample Angular Test"
  :module my.app
  :bindings [compare (obj :login "login"
                          :password "secret"
                          :greeting "hello world")]}
 (ng [SimpleService]
  "SimpleService Basics"
  (is-not SimpleService.user compare)     ;; Not the actual object
  (is-equal SimpleService.user compare))  ;; But have equal values

 (ng [SimpleService]
  "SimpleService Change Login"
  (is SimpleService.user.login "login")

  (do (SimpleService.changeLogin "newLogin")
      (is SimpleService.user.login "newLogin"))))
```
<a name="describecontroller"></a>
### `describe.controller` - for testing controllers
```clojure
(def.controller my.app.SimpleCtrl [$scope]
  (! $scope.msg "Hello")
  (! $scope.setMessage (fn [msg] (! $scope.msg msg))))

(describe.controller
 {:doc "A sample controller for testing purposes"
  :module my.app
  :controller SimpleCtrl}

 (it "should be able to change the message within the $scope"
  (is $scope.msg "Hello") 
  (do ($scope.setMessage "World!")
      (is $scope.msg "World!"))

  (do ($scope.setMessage "Angular Rocks!")
      (is $scope.msg "Angular Rocks!"))))
```
<a name="ng-filter"></a>
### `ng-filter` - for testing filters
```clojure
(def.filter my.app.range []
  (fn [input total]
    (when input
      (doseq [i (range (js/parseInt total))]
        (input.push i))
      input)))

(describe.ng
  {:doc  "Testing Filters"
   :module my.app}       
  (ng-filter [range]
    (let [r (range (arr) 5)]
      (is r.length 5)
      (is r.0 0))))
```
<a name="ng-compile"></a>
### `ng-compile` - for testing directives
```clojure
(def.directive my.app.spWelcome []
  (fn [$scope element attrs]
    (let [html (element.html)]
      (element.html (str "Welome <strong>" html "</strong>")))))

(describe.ng
  {:doc  "Testing Directives"
   :module my.app}
  (ng-compile [ele "<div sp-welcome>User</div>"]
     "Testing the Compilation"
     (is (ele.html) "Welome <strong>User</strong>")))
```


[[◄ Back (API purnam.angular)|API   purnam angular]] 