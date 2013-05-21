(ns angular-demo.login
  (:use [purnam.cljs :only [aset-in aget-in]])
  (:use-macros
   [purnam.js :only [! def.n obj]]
   [purnam.angular :only [def.module def.config
                           def.controller def.service]]))

(def.module loginDemo [purnam])

(def.service loginDemo.Db []
  (atom {:users   {"skywalker" "luke"
                   "trump" "donald"
                   "dylon" "bob"
                   "white" "snow"
                   "oprah" "oprah"}}))

(def.service loginDemo.App []
  (obj
   :helperMsg {:login "Please Enter Your Login"
               :password "Please Enter Your Password"}
   :statusMsg {:initial "Please Log In:"
               :loggedIn "Logged In"
               :loggingIn "Logging In"
               :loginFailed "Login Failed"}
   :current {:status "initial"
             :focus  "blank"}))


(def.controller loginDemo.LoginMainCtrl [$scope App Db]
  (! $scope.app App)

  (! $scope.loginText "")

  (! $scope.passwordText "")

  (! $scope.alert
     (fn [msg] (js/alert msg)))

  (! $scope.statusMsg
     (fn [] App.statusMsg.|App.current.status|))

  (! $scope.helperMsg
     (fn [] App.statusMsg.|App.current.status|))

  (! $scope.setStatus
     (fn [status] (! App.current.status status)))

  (! $scope.setFocus
     (fn [focus]
       (js/console.log focus)
       (! App.current.focus focus)))

  (! $scope.noInput
     (fn [text] (or (nil? text)
                   (= text.length 0))))

  (! $scope.login
     (fn [login pass]
       ;;(js/console.log login pass $scope)
       (let [rpass (get-in @Db [:users login])]
         (if (= pass rpass)
           (js/alert (str "You are Logging in as " login))
           (js/alert (str "You have failed to login as " login)))))))
