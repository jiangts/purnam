### Overview ###

One of the major attractions to using angularjs is the philosophy of testing and automating everything. The `purnam.test.angular` library tries to follow along that philosophy. Angularjs testing macros are defined in the `purnam.test.angular` namespace. They are used to decrease boilerplate and code noise, increase developer accuracy as well as increase code legibility.

In reality, there are 4 main tests in angular
- Controllers
- Directives
- Filters
- Services (Providers and Factories Included)

Controllers provide most of the functionality and so have a dedicated macro. services can be injected using `it-uses`, directives using `it-compiles` and filters using `it-uses-filters`.

<a name="describeng"></a><a name="ng"></a>
### `describe.ng` and `it-uses` - automatic injection
Reduce clutter with `describe.ng` form. You can specify your module with the `:module` keyword and what to inject into your application before every `it` form with the `:inject` keyword. You can also inject on a one by one basis using `it-uses`.

Define Service

```clojure
(def.service sample.SimpleService []
  (obj :user {:login "login"}
       :changeLogin (fn [login]
                      (! this.user.login login))))
```

Construct service test

```clojure                      
(describe.ng
  {:doc  "Simple Services Test"
   :module sample
   :inject [SimpleService]
  (it "SimpleService Change Login"
    (is SimpleService.user.login "login")
    (do (SimpleService.changeLogin "newLogin")
        (is SimpleService.user.login "newLogin"))))
```

An alternative using `it-uses`

```clojure
(describe.ng
  {:doc  "Simple Services Test"
   :module sample
 
  (it-uses [SimpleService]
    "SimpleService Change Login"
    (is SimpleService.user.login "login")
    (do (SimpleService.changeLogin "newLogin")
        (is SimpleService.user.login "newLogin"))))
```

<a name="describecontroller"></a>
### `describe.controller` - for testing controllers
There is also a describe.controller for convience of testing controllers

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

<a name="it-uses-filter"></a>
### `it-uses-filter` - for testing filters
Filters can be tested with the `it-uses-filter` form.

```clojure
(def.filter sample.range []
  (fn [input total]
    (when input
      (doseq [i (range (js/parseInt total))]
        (input.push i))
      input)))

(describe.ng
  {:doc  "Testing Filters"
   :module sample}       
  (it-uses-filter [range]
    (let [r (range (arr) 5)]
      (is r.length 5)
      (is r.0 0))))
```
<a name="it-compiles"></a>
### `it-compiles` - for testing directives
Directives can be tested with the `it-compiles` form.

```clojure
(def.directive sample.spWelcome []
  (fn [$scope element attrs]
    (let [html (element.html)]
      (element.html (str "Welome <strong>" html "</strong>")))))

(describe.ng
  {:doc  "Testing Directives"
   :module sample}
  (it-compiles [ele "<div sp-welcome>User</div>"]
     "Testing the Compilation"
     (is (ele.html) "Welome <strong>User</strong>")))
```


[[◄ Back (API purnam.angular)|API   purnam angular]] 