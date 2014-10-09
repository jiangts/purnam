(ns purnam.test_test
  (:use [midje.sweet :exclude [contains]]
        [purnam.test :exclude [fact facts]]))

(fact "describe without options"
  (macroexpand-1
  '(describe
    <BODY>))
  =>
  '(let [spec (js-obj)]
     (js/describe
      ""
      (clojure.core/fn []
        <BODY> nil))))

(fact "describe with options"
  (macroexpand-1
  '(describe
    {:doc "<DESCRIPTION>"
     :spec <SPEC>
     :vars [<VAR1> <FORM1>
            <VAR2> <FORM1>]}
    <BODY>))
  => '(let [<SPEC> (js-obj)]
        (aset <SPEC> "<VAR1>" <FORM1>)
        (aset <SPEC> "<VAR2>" <FORM1>)
        (js/describe "<DESCRIPTION>"
                     (clojure.core/fn [] <BODY> nil))))

(fact "describe FULL"
  (macroexpand-1
  '(describe
    {:doc "<DESC>"
     :vars [<V> (obj :array [1 2 3 4])]}
    (it "<IT IS>"
        (is <V>.array.<INDEX> <FN>))))
  => 
  '(let [spec (js-obj)] 
    (aset spec "<V>" (obj :array [1 2 3 4])) 
    (js/describe "<DESC>" 
      (clojure.core/fn [] (it "<IT IS>" 
          (purnam.test/is (purnam.common/aget-in spec ["<V>" "array" "<INDEX>"])
            <FN> 
            "'<V>.array.<INDEX>'" 
            "'<FN>'")) nil))))

(fact "beforeEach"
  (macroexpand-1
   '(beforeEach <BODY>))
  =>
  '(js/beforeEach
    (clojure.core/fn []
      <BODY>)))

(fact "it without description"
  (macroexpand-1
   '(it <BODY>))
  =>
  '(js/it ""
          (clojure.core/fn []
            <BODY>)))

(fact "it with description"
  (macroexpand-1
   '(it "<DESC>" <BODY>))
  =>
  '(js/it "<DESC>"
          (clojure.core/fn []
            <BODY>)))

(fact "is"
  (macroexpand-1
    '(is <FORM> <EXPECTED>))
  =>
  '(.toSatisfy (js/expect <FORM>) <EXPECTED> "<FORM>" "<EXPECTED>"))

(fact "is-not"

  (macroexpand-1 '(is-not <FORM> <EXPECTED>))
  =>
  '(.toSatisfy (.-not (js/expect <FORM>))
                <EXPECTED> "<FORM>" "<EXPECTED>"))
