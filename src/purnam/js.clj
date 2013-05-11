(ns purnam.js
  (:require [clojure.string :as s]))

(defn hash-set? [obj]
  (instance? clojure.lang.IPersistentSet obj))

(defn hash-map? [obj]
  (instance? clojure.lang.IPersistentMap obj))

(defn dotted-sym? [sym]
  (and (symbol? sym)
       (let [ss (str sym)
             arr (s/split ss #"\.")]
         (and (not (.endsWith ss "."))
              (> (.lastIndexOf ss ".") (.lastIndexOf ss "/"))
              (not= (first arr) "")
              (< 1 (count arr))))))

(defn split-syms
  ([sym]
     (let [st (str sym)]
       (if-let [usym (-> #"^(.+/[^\.]+)\.(.+)" (re-find st) second)]
         (split-syms [usym] (subs st (inc (count usym))))
         (split-syms [] st))))
  ([arr st]
     (->> (s/split st #"\.") (concat arr) vec)))

(defn aget-dotted [sym]
 (let [[var & ks] (split-syms sym)]
   (list 'purnam.cljs/aget-in (symbol var) (vec ks))))

(defn patch-dotted-sym [obj]
  (if (dotted-sym? obj)
    (aget-dotted obj)
    obj))

(declare patch-dotted-syms)

(defn expand-dotted-fn [sym args]
  (let [[var & ks] (split-syms sym)
        sel  (vec (butlast ks))
        fnc  (last ks)]
    (list 'let ['obj# (list 'purnam.cljs/aget-in (symbol var) sel)]
          (apply list (symbol (str "." fnc)) 'obj#
                 (patch-dotted-syms args false)))))

(defn patch-dotted-syms
  ([form] (patch-dotted-syms form true))
  ([form pfn] (patch-dotted-syms form pfn '#{! !> ? ?> obj}))
  ([form pfn ex]
     (cond (set? form) (apply set (map patch-dotted-syms form))

           (hash-map? form)
           (into {}
                 (map (fn [en] (mapv patch-dotted-syms en)) form))

           (vector? form) (mapv patch-dotted-syms form)

           (seq? form)
           (cond (get ex (first form)) form

                 (and pfn (dotted-sym? (first form)))
                 (expand-dotted-fn (first form) (next form))

                 :else
                 (apply list (map patch-dotted-syms form)))

           :else (patch-dotted-sym form))))

(defn expand-dotted [f args]
  (apply list f (patch-dotted-syms args)))

(defmacro ? [sym]
  (aget-dotted sym))

(defmacro ?> [f & args]
  (expand-dotted f args))

(defmacro ! [sym val]
 (let [[var & ks] (split-syms sym)]
   (list 'purnam.cljs/aset-in (symbol var) (vec ks)
          (patch-dotted-syms val))))

(defmacro !> [sym & args]
  (expand-dotted-fn sym args))

(defmacro f.n [args & body]
  `(fn ~args ~@(patch-dotted-syms body)))

(defmacro def.n [sym args & body]
  `(defn ~sym ~args
     ~@(patch-dotted-syms body)))


;; Macro to create objects

(defn has-root? [sym root]
  (let [syms  (s/split (name sym) #"\.")]
    (= (str root) (first syms))))

(defn change-root
  ([sym new]
     (let [syms  (s/split (str sym) #"\.")
           nsyms (cons (str new) (rest syms))]
       (symbol (s/join "." nsyms))))
  ([form root new] (change-root form root new #{}))
  ([form root new ex]
     (let [cr-fn #(change-root % root new ex)]
      (cond (symbol? form) (if (has-root? form root)
                            (change-root form new) form)
            (vector? form)   (mapv cr-fn form)
            (hash-set? form) (set (map cr-fn form))
            (hash-map? form) (into {} (map (fn [[k x]] [k (cr-fn x)]) form))
            (seq? form)
            (cond (get ex (first form)) form

                  :else
                  (apply list (map cr-fn form)))
            :else form))))

(defn transform-tree
  [form pred? ex-pred? transform]
  (let [r-fn #(transform-tree % pred? ex-pred? transform)]
    (cond (pred? form)     (transform form)
          (ex-pred? form)  form
          (vector? form)   (mapv r-fn form)
          (hash-set? form) (set (map r-fn form))
          (hash-map? form) (into {} (map (fn [[k x]] [k (r-fn x)]) form))
          (seq? form) (apply list (map r-fn form))
          :else form)))


(declare make-js)

(defn make-js-object-aset [sym [k v]]
  (list 'aset sym (name k)
        (make-js
         (change-root v 'this sym #{'obj}))))

(defn make-js-object
  ([m] (make-js-object nil m))
  ([sym m]
     (let [sym  (or sym (gensym))
           body (map #(make-js-object-aset sym %) m)]
         (concat ['let [sym '(js-obj)]]
                 body
                 [sym]))))

(defn make-js-array [v]
  (apply list 'array
         (map make-js v)))

(defn make-js [v]
  (cond (hash-map? v)
        (make-js-object v)

        (vector? v)
        (make-js-array v)

        :else
        v))

(defmacro obj [& args]
    (let [m (apply hash-map args)]
      (patch-dotted-syms (make-js m))))

(macroexpand-1
 '(obj :a {:b 1}))
