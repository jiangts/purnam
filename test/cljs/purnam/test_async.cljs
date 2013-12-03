(ns purnam.test-async
    (:use-macros [purnam.core :only [obj arr !]]
                 [purnam.test :only [describe it is fact]]
                 [purnam.test.async :only [runs waits-for]]))

(describe {:doc  "Testing Async macros"
           :vars [flag (atom false) 
                  value (atom 0)]}
  (it "Should support async execution of test preparation and exepectations"
    (runs (js/setTimeout (fn [] (reset! flag true)) 500))
    (waits-for "Flag should be true" 750 (swap! value inc) @flag)
    (runs (is @flag true)
          (is (> @value 0) true))))