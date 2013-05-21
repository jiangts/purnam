(ns angular-demos.sfp
  (:use [purnam.cljs :only [aget-in aset-in]])
  (:use-macros [purnam.js :only [obj arr ! !> def.n]]
               [purnam.angular :only
                [def.module def.config def.factory
                 def.provider def.service def.controller]]))

(def.module sfpDemo [])

(def.service sfpDemo.HelloWorldFromService []
  (! this.sayHello (fn [] "Hello World!")))

(def.factory sfpDemo.HelloWorldFromFactory []
  (obj :sayHello (fn [] "Hello World!")))

(def.provider sfpDemo.HelloWorld []
  (obj :name "Default"
       :$get (fn []
               (let [n self.name]
                 (obj :sayHello
                      (fn [] (str "Hello " n "!")))))
       :setName (fn [name]
                  (! self.name name))))

(def.config sfpDemo [HelloWorldProvider]
  (!> HelloWorldProvider.setName "World"))

(def.controller sfpDemo.sfpMainCtrl [$scope HelloWorldFromService HelloWorldFromFactory HelloWorld]
  (! $scope.hellos
     (arr [(HelloWorldFromService.sayHello) "From Service"]
          [(HelloWorldFromFactory.sayHello) "From Factory"]
          [(HelloWorld.sayHello) "From Provider"])))
