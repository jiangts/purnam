(ns purnam.types.macros
  (:require [clojure.walk :as w]))

(defn extend-single [proto ptmpls t funcs]
  (apply list `extend-type t proto
    (map (fn [tmpl f] (w/prewalk-replace {'?% f} tmpl))
       ptmpls funcs)))

(defn extend-entry [proto ptmpls [ts funcs]]
  (cond (vector? ts)
        (map #(extend-single proto ptmpls % funcs) ts)

        :else
        [(extend-single proto ptmpls ts funcs)]))

(defmacro extend-all [proto ptmpls & args]
  (let [types (partition 2 args)]
    `(do
       ~@(mapcat #(extend-entry proto ptmpls %) types))))

(defn generate-invoke [pfunc afunc args]
  (let [narg (count args)]
   `(~pfunc [_ ~@args]
      (if (> ~'n ~narg)
        (~afunc (- ~'n ~narg) (partial ~'f ~@args))
        (~pfunc ~'f ~@args))))))
       
(defn generate-invokes [pfunc afunc n]
  (let [args (map #(symbol (str "a" %)) (range n))]
    (map #(generate-invoke pfunc afunc (take % args)) (range n))))

(defmacro extend-invoke [type protocol pfunc afunc n]
  `(extend-type ~type
    ~protocol
    ~@(generate-invokes pfunc afunc n)))

(macroexpand-1 '(extend-type-invoke CFn IFn -invoke curry 2))

(defmacro with-context
 "Establishes the monadic context that can be accessed
  with the get-context function in the dynamic scope inside
  the body."
 [context & body]
 `(binding [purnam.types.monad/*pure-context* ~context]
    ~@body))