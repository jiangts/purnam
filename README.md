# purnam

Purnam - AngularJs Language Extensions for Clojurescript

### Installation

In your project file, add

```clojure
[purnam "0.1.0-beta"]
```

### What is Purnam?
- [`purnam.js`](https://github.com/zcaudate/purnam/wiki/API---purnam-js) Javascript-like semantics for Clojurescript 
- [`purnam.test`](https://github.com/zcaudate/purnam/wiki/API---purnam-test) - Jasmin Extensions for Test Driven Workflow
- [`purnam.angular`](https://github.com/zcaudate/purnam/wiki/API---purnam-angular) and [`purnam.test-angular`](https://github.com/zcaudate/purnam/wiki/API---purnam-test-angular) - Angularjs Extensions for Boilerplace Reduction and Code Accuracy

### Starting Points:

- [PURNAM WIKI](https://github.com/zcaudate/purnam/wiki)
- [QUICKSTART](https://github.com/zcaudate/purnam/wiki/Your-First-Project)
- [BUILDING DEMOS](https://github.com/zcaudate/purnam/wiki/Building-Demos)

### A Taste of Purnam

##### Functions
```javascript
// javascript
function square(x){
  return {value: x.value * x.value};
}
```
```clojure
;; clojurescript + purnam
(def.n square [x]
  (obj :value (* x.value x.value)))
```
```clojure
;; clojurescript
(defn square [x]
  (let [o (js-obj)
        v (aget x "value")]
    (aset o "value" (* v v)))
    o)
```

##### Objects
```javascript
// javascript
var user = {id: 0 
            account: {username: "user"
                      password: "pass"}}
```
```clojure
;; clojurescript + purnam
(def* user {:id 0 
            :account {:username "user"
                      :password "pass"})})
```
```clojure
;; clojurescript
(def user
  (let [acc (js-obj)
        user (js-obj)]
    (aset acc "username" "user")
    (aset acc "password" "pass")
    (aset user "account" acc)
    (aset user "id" 0)
    user)) 

;; clojurescript using clj->js (slower)
(def user 
  (clj->js {:id 0 
            :account {:username "user"
                      :password "pass"})})
```


##### Angular JS

```clojure
;; purnam.angular

(def.module my.app [])

(def.config my.app [$routeProvider]
  (-> $routeProvider
      (.when "/" (obj :templateUrl "views/main.html"))
      (.otherwise (obj :redirectTo "/"))))

(def.controller my.app.MainCtrl [$scope $http]
  (! $scope.msg "")
  (! $scope.setMessage (fn [msg] (! $scope.msg msg)))
  (! $scope.loginQuery
     (fn [user pass]
       (let [q (obj :user user
                    :pass pass)]
         (-> $http
             (.post "/login" q)
             (.success (fn [res]
                         (if (= res "true")
                           (! $scope.loginSuccess true)
                           (! $scope.loginSuccess false))))
             (.error (fn [] (js/console.log "error!!")))))))


##### AngularJS Testing
```clojure
;; purnam.test.angular

(describe.controller
 {:doc "A sample controller for testing purposes"
  :module my.app
  :controller MainCtrl}

 (it "should be able to change the message within the $scope"
  (is $scope.msg "Hello") 
  (do ($scope.setMessage "World!")
      (is $scope.msg "World!"))

  (do ($scope.setMessage "Angular Rocks!")
      (is $scope.msg "Angular Rocks!"))))
```


## License

Copyright © 2013 Chris Zheng

Distributed under the The MIT License.