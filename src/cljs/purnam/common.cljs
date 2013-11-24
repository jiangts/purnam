(ns purnam.common)

(def ^:dynamic flags (js-obj))

(def ^:dynamic *pure-context*)

(defn get-context [] *pure-context*)