(ns purnam.js
  (:require [clojure.string :as s]))

(defn hash-set? [obj]
  (instance? clojure.lang.IPersistentSet obj))

(defn hash-map? [obj]
  (instance? clojure.lang.IPersistentMap obj))

(defmacro case-let [[var bound] & body]
  `(let [~var ~bound]
     (case ~var ~@body)))

(defn conj-if-str [arr s]
  (if (empty? s) arr
      (conj arr s)))

(declare split-dotted-pipe)

(defn reconstruct-dotted [output current ss]
  (str (s/join "." output) "." current (apply str ss)))

(defmacro split-error [msg]
  `(throw (Exception.
           (str ~msg ", input: " (reconstruct-dotted ~'output ~'current ~'ss)))))

(defn split-dotted
  ([ss]
     (split-dotted [] "" ss))
  ([output current ss]
     (case-let
      [ch (first ss)]
       nil (conj-if-str output current)
       \.  (case-let
            [nch (second ss)]
            nil (split-error "Cannot have '.' at the end of a dotted symbol")
            \/  (split-error "Cannot have '/' during split")
            \|  (trampoline split-dotted-pipe
                            (conj-if-str output current) "|" (nnext ss))
            (recur (conj-if-str output current) (str nch) (nnext ss)))
       \|  (split-error "Cannot have '|' during split")
       \/  (split-error "Cannot have '/' during split")
       (recur output (str current ch) (next ss)))))

(defn split-dotted-pipe
  ([output current ss] (split-dotted-pipe output current ss 0))
  ([output current ss level]
      (case-let
       [ch (first ss)]
       nil (split-error "Cannot have an unpaired pipe")
       \|  (case level
             0 (trampoline split-dotted
                           (conj output (str current "|"))
                           "" (next ss))
             (recur output (str current "|") (next ss) (dec level)))
       \.  (case-let
            [nch (second ss)]
            nil (split-error "Incomplete dotted symbol")
            \|  (recur output (str current ".|") (nnext ss) (inc level))
            (recur output (str current "." nch) (nnext ss) level))
       (recur output (str current ch) (next ss) level))))

(defn symbol-with-ns? [sym]
  (if-let [res (re-find #"[\.\w\_\-]+/[\w\_\-]+\.?(.*)" (str sym))]
    (-> res second empty?)))

(defn symbol-contains-dot? [sym]
  (if (re-find #"[^\.]+\.[^\.]" (str sym)) true))

(defn js-exp? [sym]
  (cond (not (symbol? sym)) false
        (.startsWith (str sym) ".") false
        (.endsWith (str sym) ".") false
        (symbol-with-ns? sym) false
        (or (= 'f.n sym) (= 'do.n sym)) false
        (symbol-contains-dot? sym) true
        :else false))

(defn js-split-first [sym]
  (let [ss  (str sym)
        res (or (re-find #"(^[^\|/]+/[^\.\|/]+)(\..*)" ss)
                (re-find #"(^[^\|\./]+)(\..*)" (str sym)))]
    (next res)))

(defn js-split-syms [sym]
  (if-let [[k ks] (js-split-first sym)]
    (split-dotted [k] "" ks)
    (throw (Exception. (str "js-exp: " sym " cannot be split")))))

(declare js-parse-sub-exp)

(defn js-parse-exp [sym]
 (let [[var & ks] (js-split-syms sym)]
   (list 'purnam.cljs/aget-in (symbol var)
         (vec (map js-parse-sub-exp ks)))))

(defn js-parse-sub-exp [ss]
  (if-let [res (re-find #"^\|(.*)\|$" ss)]
    (let [sym (symbol (second res))]
      (if (js-exp? sym)
        (js-parse-exp sym)
        sym))
    ss))

(declare js-expand)

(defn js-expand-sym [obj]
  (if (js-exp? obj)
    (js-parse-exp obj)
    obj))

(defn js-expand-fn [sym args]
  (let [[var & ks] (js-split-syms sym)
        sel  (vec (butlast ks))
        fnc  (last ks)]
    (list 'let ['obj# (list 'purnam.cljs/aget-in (symbol var)
                            (vec (map js-parse-sub-exp sel)))]
          (apply list (symbol (str "." fnc)) 'obj#
                 (js-expand args false)))))

(defn js-expand
  ([form] (js-expand form true))
  ([form pfn] (js-expand form pfn '#{! !> ? ?> obj f.n do.n}))
  ([form pfn ex]
     (cond (set? form) (apply set (map js-expand form))

           (hash-map? form)
           (into {}
                 (map (fn [en] (mapv js-expand en)) form))

           (vector? form) (mapv js-expand form)

           (seq? form)
           (cond (get ex (first form)) form

                 (and pfn (js-exp? (first form)))
                 (js-expand-fn (first form) (next form))

                 :else
                 (apply list (map js-expand form)))

           :else (js-expand-sym form))))

(defn js-apply-expand [f args]
  (apply list f (js-expand args)))

(defmacro ? [sym]
  (js-expand-sym sym))

(defmacro ?> [f & args]
  (js-apply-expand f args))

(defmacro ! [sym val]
 (let [[var & ks] (js-split-syms sym)]
   (list 'purnam.cljs/aset-in (symbol var)
         (vec (map js-parse-sub-exp ks))
         (js-expand val))))

(defmacro !> [sym & args]
  (js-expand-fn sym args))

(defmacro f.n [args & body]
  `(fn ~args ~@(js-expand body)))

(defmacro def.n [sym args & body]
  `(defn ~sym ~args
     ~@(js-expand body)))

(defmacro do.n [& body]
  `(do ~@(js-expand body)))

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

(declare make-var)

(defn make-js-object-aset [sym [k v]]
  (list 'aset sym (name k)
        (make-var
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
         (map make-var v)))

(defn make-var [v]
  (cond (hash-map? v)
        (make-js-object v)

        (vector? v)
        (make-js-array v)

        :else
        v))

(defmacro obj [& args]
    (let [m (apply hash-map args)]
      (js-expand (make-var m))))
