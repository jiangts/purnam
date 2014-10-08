(ns purnam.test.clojure)

(defmacro statement [v expected]
  (list '.toSatisfy (list 'js/expect v) expected (str v) (str expected)))

(defmacro is [info v expected]
  (list 'js/describe info 
     (list 'fn []
       (list 'js/it ""
         `(fn [] 
             (statement ~v ~expected))))))                          
                                   
(defmacro deftest [info & args]
  `(do
    ~@(map (fn [[f & rst :as form]] 
             (if (= f 'is)
               (concat [f info] rst)
               form)) 
           args)))
