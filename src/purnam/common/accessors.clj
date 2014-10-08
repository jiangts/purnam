(ns purnam.common.accessors)

(defn aget-in-form
  [var arr]
  (if (empty? arr) var
      (let [bs (gensym)]
        (list 'if-let [bs (list 'aget var (first arr))]
              (aget-in-form bs (next arr))))))

(defn aget-in-form-raw
  [var arr]
  (apply list 'aget var arr))

(defn aget-in* [var arr safe?]
 (condp = (count arr)
  0 var
  1 (list 'aget var (first arr))
  (if safe?
    (aget-in-form var arr)
    (aget-in-form-raw var arr))))

(defn nested-val-form [[k & ks] val]
  (cond (nil? k) val
    
        :else
        (let [bs (gensym)]
          (list 'let [bs (list 'js* "{}")]
                (list 'aset bs k (nested-val-form ks val))
                bs))))

(defn aset-in-form* [var [k & ks] val]
  (cond (nil? k) nil
        (empty? ks)
        (if val
          (list 'aset var k val)
          (list 'js-delete var k))
        :else
        (let [bs (gensym)]
          (list 'if-let [bs (list 'aget var k)]
                (aset-in-form* bs ks val)
                (if val
                  (list 'aset var k (nested-val-form ks val)))))))

(defn aset-in-form [var ks val]
  (list 'do (aset-in-form* var ks val)
        var))
                
(defn aset-in* [var arr val]
  (condp = (count arr)
   0 (throw (Exception. (str "Cannot set the root element: " var arr)))
   1 (list 'aset var (first arr) val)
 (aset-in-form var arr val)))
 
(defn adelete-in* [var arr]
  (condp = (count arr)
   0 (throw (Exception. (str "Cannot delete the root element: " var)))
   1 (list 'js-delete var (first arr))
 #_(aset-in-form var arr val)))