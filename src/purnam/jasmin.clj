(ns purnam.jasmin
  (require [clojure.string :as s]))

(defmacro describe [desc bindings & body]
  (list 'let bindings
        (list 'js/describe desc
              `(fn [] ~@body
                 nil))))

(defmacro it [desc & body]
  (list 'js/it desc
        `(fn [] ~@body)))

(defmacro is [v1 v2]
  (list '.toBe (list 'js/expect v1) v2))

(defmacro is-not [v1 v2]
  (list '.toBe (list '.-not (list 'js/expect v1)) v2))

(defmacro contains [v1 v2]
  (list '.toBe (list 'js/expect v1) v2))
