(ns purnam.common)

(defn >nested-val [[k & ks] val]
  (if (nil? k)
    val
    (let [o (js-obj)]
      (aset o k (>nested-val ks val))
      o)))

(defn aset-in [var arr val]
  (let [[k & ks] arr]
    (cond (nil? k) nil
          (empty? ks) (aset var k val)
          :else
          (if-let [svar (aget var k)]
            (aset-in svar ks val)
            (aset var k (>nested-val ks val))))
    var))

(defn aget-in
  ([var] var)
  ([var arr]
     (cond  (= var js/undefined) nil
            (empty? arr) var
            (nil? var) nil
            :else (aget-in (aget var (first arr))
                           (next arr)))))

;; -----------

;; -----------

(defn >strkey [x]
  (cond
    (string? x) x
    (keyword? x) (name x)
    :else (str x)))

(defn >obj-name [this]
  (if-let [[_ n] (re-find #"^function (\w+)" (str this))]
    n
    "Object"))

(defn >type [o]
  (js* "typeof ~{}" o))

(defn >lookup
  ([o k]
     (aget o (>strkey k)))
  ([o k not-found]
     (let [s (>strkey k)]
       (if-let [res (aget o s)]
         res
         not-found))))

(defn >clone
  ([o] ))

(defn >assoc!
  ([o k v]
     (do (aset o (>strkey k) v)
         o))
  ([o k v & more]
      (apply >assoc! (>assoc! o k v) more)))

(defn >assoc
  ([o k v]
     (-> o >clone (>assoc! k v)))
  ([o k v & more]
     (apply >assoc! (>clone o) k v more)))

(defn >assoc-in! [o ks v]
  (let [[k & more] ks]
    (cond (nil? k) nil
          (empty? more) (>assoc! o k v)
          :else
          (if-let [o-sub (aget o k)]
            (>assoc-in! o-sub ks val)
            (>assoc! o k (>nested-val ks val))))
    o))

(defn >dissoc!
  [o & ks]
  (doseq [k ks]
    (js-delete o (>strkey k)))
  o)
