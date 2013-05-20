(ns purnam.cljs)

(defn obj-in
 ([ks val] (obj-in (js-obj) ks val))
 ([obj ks val]
    (if-let [k (first ks)]
      (do (aset obj k (obj-in (next ks) val))
          obj)
      val)))

(defn aset-in [var arr val]
  (let [[k & ks] arr]
    (cond (nil? k) nil
          (empty? ks) (aset var k val)
          :else
          (if-let [svar (aget var k)]
            (aset-in svar ks val)
            (aset var k (obj-in ks val))))
    var))

(defn aget-in
  ([var] var)
  ([var arr]
     (cond  (= var js/undefined) nil
            (empty? arr) var
            (nil? var) nil
            :else (aget-in (aget var (first arr))
                           (next arr)))))

(defn js<- [obj]
  (clj->js obj))

(defn log
  ([x] (if (coll? x)
         (.log js/console (str x) x)
         (.log js/console (str x))) x)
  ([x y] (if (coll? x)
           (.log js/console (str x ":") (str y) y)
           (.log js/console (str x ":") (str y))) y))
