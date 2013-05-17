(ns purnam.test-jasmin-angular
  (:use [midje.sweet :exclude [contains]]
        purnam.checks
        purnam.test.angular))

(fact "describe.controller"
  (macroexpand-1
   '(describe.controller
     "<DESC>"
     {:module <MODULE-NAME>
      :controller <CONTROLLER-NAME>}
     <BODY>))
  =>
  '(describe
     "<DESC>"
     [spec (js-obj)]
     (js/beforeEach (js/module "<MODULE-NAME>"))
     (js/beforeEach
      (js/inject
       (array "$rootScope" "$controller"
              (fn [$rootScope $controller]
                (! spec.$scope ($rootScope.$new))
                ($controller "<CONTROLLER-NAME>" spec)))))
     <BODY>)

  (comment (defmacro describe.controller [desc options & body]
             (let [{:keys [module controller inject]} options]
               (apply
                l 'describe desc
                ['spec '(js-obj)]
                (l 'js/beforeEach
                   (l 'js/module (str module)))
                (l 'js/beforeEach
                   (l 'js/inject
                      (l 'array "$rootScope" "$controller"
                         (l 'fn ['$rootScope '$controller]
                            '(! spec.$scope ($rootScope.$new))
                            (l '$controller (str controller) 'spec)))))
                body)))))

(fact "describe.controller"
  (macroexpand-1
   '(describe.controller
     "<DESC>"
     {:module <MODULE-NAME>
      :controller <CONTROLLER-NAME>}
     (<FUNC> $scope.<VAR>)
     (<FUNC> $ctrl.<VAR>)))
  =>
  '(describe
     "<DESC>"
     [spec (js-obj)]
     (js/beforeEach (js/module "<MODULE-NAME>"))
     (js/beforeEach
      (js/inject
       (array "$rootScope" "$controller"
              (fn [$rootScope $controller]
                (! spec.$scope ($rootScope.$new))
                ($controller "<CONTROLLER-NAME>" spec)))))
     (<FUNC> spec.$scope.<VAR>)
     (<FUNC> $ctrl.<VAR>)))



(fact "describe.controller will generate this type of template:"
  (macroexpand-1
   '(describe.controller
     "<DESC>"
     {:module <MODULE-NAME>
      :controller <CONTROLLER-NAME>
      :inject {:<V1> <V1-FORM>
               :<V2> <V2-FORM>}}
     (<FUNC> $scope.<VAR>)
     (<FUNC> <V1>.<VAR>)
     (<FUNC> <V2>.<VAR>)))
  =>
  '(describe
    "<DESC>"
    [spec (js-obj)]
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
    (<FUNC> spec.<V2>.<VAR>)))

(fact "describe.ng"
  (macroexpand-1
   '(describe.ng
     "<DESC>"
     {:module <MODULE-NAME>
      :bindings [<B1> <V1>]}
     <BODY>))
  => '(describe
       "<DESC>"
       [spec (js-obj)
        <B1> <V1>]
       (js/beforeEach (js/module "<MODULE-NAME>"))
       <BODY>))

(fact "service"
  (macroexpand-1
   '(service
    "<DESC>"
    [<SERVICE>]
    <BODY>))
  => '(js/inject (array "<SERVICE>" (fn [<SERVICE>] <BODY>))))
