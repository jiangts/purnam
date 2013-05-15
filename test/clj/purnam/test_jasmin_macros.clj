(ns purnam.test-jasmin-macros
  (:use [midje.sweet :exclude [contains]]
        purnam.checks
        purnam.jasmin))

(fact "describe"
  '(describe
    "test"
    [a1 (obj :array [1 2 3 4])]
    (it "descripition"
        (is o1.array.0 odd?)))
  =>
  (expands-into
   '(let [a1 (obj :array [1 2 3 4])]
      (js/describe
       "test"
       (clojure.core/fn []
         (it "descripition"
             (is (purnam.cljs/aget-in o1 ["array" "0"]) odd?)) nil)))))

(fact "beforeEach"
  '(beforeEach <BODY>)
  =>
  (expands-into
   '(js/beforeEach
     (clojure.core/fn []
       <BODY>))))

(fact "it"
  '(it <DESC> <BODY>)
  =>
  (expands-into
   '(js/it <DESC>
     (clojure.core/fn []
       <BODY>))))

(fact "is"
  '(is <FORM> <EXPECTED>)
  =>
  (expands-into
   '(.toSatisfy (js/expect <FORM>) <EXPECTED> "<FORM>" "<EXPECTED>")))

(fact "is-not"
  '(is-not <FORM> <EXPECTED>)
  =>
  (expands-into
   '(.toSatisfy (.-not (js/expect <FORM>))
                <EXPECTED> "<FORM>" "<EXPECTED>")))


(fact "describe.controller"
  (macroexpand-1
   '(describe.controller
     <DESC>
     {:module <MODULE-NAME>
      :controller <CONTROLLER-NAME>}
     <BODY>))
  =>
  '(describe
     <DESC>
     [spec (js-obj)]
     (js/beforeEach (js/module "<MODULE-NAME>"))
     (js/beforeEach
      (js/inject
       (array "$rootScope" "$controller"
              (fn [$rootScope $controller]
                (! spec.$scope ($rootScope.$new))
                (! spec.$ctrl ($controller "<CONTROLLER-NAME>" spec))))))
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
                            (l '! 'spec.$ctrl (l '$controller
                                                 (str controller) 'spec))))))
                body)))))

(fact "describe.controller"
  (macroexpand-1
   '(describe.controller
     <DESC>
     {:module <MODULE-NAME>
      :controller <CONTROLLER-NAME>}
     (<FUNC> $scope.<VAR>)
     (<FUNC> $ctrl.<VAR>)))
  =>
  '(describe
     <DESC>
     [spec (js-obj)]
     (js/beforeEach (js/module "<MODULE-NAME>"))
     (js/beforeEach
      (js/inject
       (array "$rootScope" "$controller"
              (fn [$rootScope $controller]
                (! spec.$scope ($rootScope.$new))
                (! spec.$ctrl ($controller "<CONTROLLER-NAME>" spec))))))
     (<FUNC> spec.$scope.<VAR>)
     (<FUNC> spec.$ctrl.<VAR>)))



(fact "describe.controller"
  (macroexpand-1
   '(describe.controller
     <DESC>
     {:module <MODULE-NAME>
      :controller <CONTROLLER-NAME>
      :inject {:<V1> <V1-FORM>
               :<V2> <V2-FORM>}}
     (<FUNC> $scope.<VAR>)
     (<FUNC> <V1>.<VAR>)
     (<FUNC> <V2>.<VAR>)
     (<FUNC> $scope.<VAR>)))
  =>
  '(describe
    <DESC>
    [spec (js-obj)]
    (js/beforeEach (js/module "<MODULE-NAME>"))
    (js/beforeEach
     (js/inject
      (array "$rootScope" "$controller" "<V1>" "<V2>"
             (fn [$rootScope $controller <V1> <V2>]
               (! spec.$scope ($rootScope.$new))
               (! spec.<V1> <V1-FORM>)
               (! spec.<V2> <V2-FORM>)
               (! spec.$ctrl ($controller "<CONTROLLER-NAME>" spec))))))
    (<FUNC> spec.$scope.<VAR>)
    (<FUNC> spec.<V1>.<VAR>)
    (<FUNC> spec.<V2>.<VAR>)
    (<FUNC> spec.$scope.<VAR>)))
