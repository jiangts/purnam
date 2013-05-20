(ns purnam.test-angular
  (:use [purnam.cljs :only [aget-in aset-in]])
  (:require [goog.object :as o])
  (:require-macros [purnam.js :as j])
  (:use-macros [purnam.js :only [obj arr ! defv.ndef.n]]
               [purnam.test :only [init describe it is is-not is-equal is-not-equal]]
               [purnam.angular :only [def.module def.config 
                                      def.filter def.controller 
                                      def.service def.directive]]
               [purnam.test.angular :only [describe.controller describe.ng 
                                          ng ng-filter ng-compile]]))

(def.module sample.filters [])

(def.filter sample.filters.range []
  (fn [input total]
    (when input
      (doseq [i (range (js/parseInt total))]
        (input.push i))
      input)))

;;
;; Defining our Module
;;

(def.module sample [sample.filters])

(describe.ng
  {:doc  "Testing Filters"
   :module sample}
   
  (ng [$filter]
    (let [r (($filter "range") (arr) 5)]
      (is r.length 5)
      (is r.0 0)))
       
  (ng-filter [range]
    (is-not range nil)
    (let [r (range (arr) 5)]
      (is r.length 5)
      (is r.0 0))))


(def.directive sample.spWelcome []
  (fn [$scope element attrs]
    (let [html (element.html)]
      (element.html (str "Welome <strong>" html "</strong>")))))

(describe.ng
  {:doc  "Testing Directives"
   :module sample}

    
 (ng [$compile $rootScope]
  "Testing the Compilation"
  (let [ele (($compile "<div sp-welcome>User</div>")
                $rootScope)]
     (is (ele.html) "Welome <strong>User</strong>")))
       
 (ng-compile [ele "<div sp-welcome>User</div>"]
    "Testing the Compilation"
    (is (ele.html) "Welome <strong>User</strong>")))

;;
;; Defining a SimpleService
;;

(def.service sample.SimpleService []
  (obj :user {:login "login"
              :password "secret"
              :greeting "hello world"}
       :changeLogin (fn [login]
                      (! this.user.login login))))

;;
;; Angular Module Testing for Simple Service
;;

(describe.ng
 {:doc  "A sample Angular Test Suite"
  :module sample
  :bindings [compare (obj :login "login"
                          :password "secret"
                          :greeting "hello world")]}
 (ng [SimpleService]
  "SimpleService Basics"
  (is-not SimpleService.user compare)
  (is-equal SimpleService.user compare))

 (ng [SimpleService]
  "SimpleService Change Login"
  (is SimpleService.user.login "login")

  (do (SimpleService.changeLogin "newLogin")
      (is SimpleService.user.login "newLogin")))

 (ng [SimpleService] ;; The login will reset
  "SimpleService Change Login"
  (is SimpleService.user.login "login")))

;;
;; Angular Test Controller Example
;;

(def.controller sample.SimpleCtrl [$scope]
  (! $scope.msg "Hello")
  (! $scope.setMessage (fn [msg] (! $scope.msg msg))))

;;
;; Controller Testing
;;

(describe.controller
 {:doc "A sample controller for testing purposes"
  :module sample
  :controller SimpleCtrl}

 (it "should have an object called `spec`"
     (is-not spec js/undefined))

 (it "should set a message within the $scope"
     (is spec.$scope.msg "Hello")  ;; The $scope is automatically registered for us
     (is $scope.msg "Hello")      ;; We can also use spec.$scope
     )

 (it "should be able to change the message within the $scope"
  (do ($scope.setMessage "World!")
      (is $scope.msg "World!"))

  (do ($scope.setMessage "Angular Rocks!")
      (is $scope.msg "Angular Rocks!"))))
