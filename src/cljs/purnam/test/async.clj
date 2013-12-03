(ns purnam.test.async)

(defmacro runs [& body]
    "Specs are written by defining a set of blocks with calls to runs, which usually finish with an asynchronous call.
    Once the asynchronous conditions have been met, another runs block defines final test behavior. This is usually expectations based on state after the asynch call returns."
    (list 'js/runs `(fn [] ~@body)))

(defmacro waits-for [fail-msg timeout & body]
    "The waitsFor block takes a latch function, a failure message, and a timeout."
    `(js/waitsFor (fn [] ~@body) ~fail-msg ~timeout)
    )

