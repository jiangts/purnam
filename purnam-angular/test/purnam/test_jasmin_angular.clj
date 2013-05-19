(ns purnam.test-jasmin-angular
  (:use [midje.sweet :exclude [contains]]
        purnam.checks
        purnam.test.angular))


(fact "describe.ng"
  (macroexpand-1
   '(describe.ng
     {:doc "<DESC>"
      :module <MODULE-NAME>
      :bindings [<B1> <V1>]}
     <BODY>))
  =>
  '(let [spec (js-obj)]
     (describe
      {:doc "<DESC>"
       :module <MODULE-NAME>
       :bindings [<B1> <V1>]}
      (js/beforeEach (js/module "<MODULE-NAME>"))
      <BODY>)))

(fact "ng"
  (macroexpand-1
   '(ng [<SERVICE>]
        "<DESC>"
        <BODY>))
  => '(js/it "<DESC>" (js/inject (array "<SERVICE>" (fn [<SERVICE>] <BODY>)))))

(fact "describe.controller"
  (macroexpand-1
   '(describe.controller
     {:doc "<DESC>"
      :module <MODULE-NAME>
      :controller <CONTROLLER-NAME>}
     <BODY>))
  =>
  '(let [spec (js-obj)]
     (describe {:doc "<DESC>"
                :module <MODULE-NAME>
                :controller <CONTROLLER-NAME>}
               (js/beforeEach (js/module "<MODULE-NAME>"))
               (js/beforeEach
                (js/inject
                 (array "$rootScope" "$controller"
                        (fn [$rootScope $controller]
                          (! spec.$scope ($rootScope.$new))
                          ($controller "<CONTROLLER-NAME>" spec)))))
               <BODY>)))


(fact "describe.controller"
  (macroexpand-1
   '(describe.controller
     {:doc "<DESC>"
      :module <MODULE-NAME>
      :controller <CONTROLLER-NAME>}
     (<FUNC> $scope.<VAR>)
     (<FUNC> $ctrl.<VAR>)))
  =>
  '(let [spec (js-obj)]
     (describe
      {:doc "<DESC>"
       :module <MODULE-NAME>
       :controller <CONTROLLER-NAME>}
      (js/beforeEach (js/module "<MODULE-NAME>"))
      (js/beforeEach
       (js/inject
        (array "$rootScope" "$controller"
               (fn [$rootScope $controller]
                 (! spec.$scope ($rootScope.$new))
                 ($controller "<CONTROLLER-NAME>" spec)))))
      (<FUNC> spec.$scope.<VAR>)
      (<FUNC> $ctrl.<VAR>))))



(fact "describe.controller will generate this type of template:"
  (macroexpand-1
   '(describe.controller
     {:doc "<DESC>"
      :module <MODULE-NAME>
      :controller <CONTROLLER-NAME>
      :inject {:<V1> <V1-FORM>
               :<V2> <V2-FORM>}}
     (<FUNC> $scope.<VAR>)
     (<FUNC> <V1>.<VAR>)
     (<FUNC> <V2>.<VAR>)))
  =>
  '(let [spec (js-obj)]
     (describe
      {:doc "<DESC>"
       :module <MODULE-NAME>
       :controller <CONTROLLER-NAME>
       :inject {:<V1> <V1-FORM>
                :<V2> <V2-FORM>}}
      (js/beforeEach (js/module "<MODULE-NAME>"))
      (js/beforeEach
       (js/inject
        (array "$rootScope" "$controller" "<V1>" "<V2>"
               (fn [$rootScope $controller <V1> <V2>]
                 (! spec.$scope ($rootScope.$new))
                 (! spec.<V1> <V1-FORM>)
                 (! spec.<V2> <V2-FORM>)
                 ($controller "<CONTROLLER-NAME>" spec)))))
      (<FUNC> spec.$scope.<VAR>)
      (<FUNC> spec.<V1>.<VAR>)
      (<FUNC> spec.<V2>.<VAR>))))
