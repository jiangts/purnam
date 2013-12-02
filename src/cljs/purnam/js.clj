(ns purnam.js
  (:require [clojure.string :as s]))

(defn hash-set? [obj]
  (instance? clojure.lang.APersistentSet obj))

(defn hash-map? [obj]
  (instance? clojure.lang.APersistentMap obj))

(defn lazy-seq?
  "Returns `true` if `x` is of type `clojure.lang.LazySeq`."
  [x] (instance? clojure.lang.LazySeq x))

(defmacro suppress
  "Suppresses any errors thrown.

    (suppress (error \"Error\")) ;=> nil

    (suppress (error \"Error\") :error) ;=> :error
  "
  ([body]
     `(try ~body (catch Throwable ~'t)))
  ([body catch-val]
     `(try ~body (catch Throwable ~'t
                   (cond (fn? ~catch-val)
                         (~catch-val ~'t)
                         :else ~catch-val)))))

(defmacro case-let [[var bound] & body]
  `(let [~var ~bound]
     (case ~var ~@body)))

(defn conj-if-str [arr s]
  (if (empty? s) arr
      (conj arr s)))

(defn reconstruct-dotted [output current ss]
  (str (s/join "." output) "." current (apply str ss)))\

(defmacro split-error [msg]
  `(throw (Exception.
           (str ~msg ", input: " (reconstruct-dotted ~'output ~'current ~'ss)))))

(declare split-dotted-pipe)

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

(defmacro this* [] (list 'js* "this"))

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

(defn js-parse-var [var-str]
  (if (= var-str "this") '(js* "this")
      (symbol var-str)))

(defn js-parse-exp [sym]
 (let [[var & ks] (js-split-syms sym)]
   (list 'purnam.native/aget-in (js-parse-var var)
         (vec (map js-parse-sub-exp ks)))))

(defn js-parse-sub-exp [ss]
  (if-let [res (re-find #"^\|(.*)\|$" ss)]
    (let [sym (symbol (second res))]
      (if (js-exp? sym)
        (js-parse-exp sym)
        sym))
    ss))

(defn js-expand-sym [obj]
  (cond (js-exp? obj)
        (js-parse-exp obj)

        (= 'this obj)
        '(js* "this")

        :else obj))

(declare js-expand)

(defn js-expand-fn [sym args]
  (let [[var & ks] (js-split-syms sym)
        sel  (vec (butlast ks))
        fnc  (last ks)]
    (list 'let ['obj# (list 'purnam.native/aget-in (js-parse-var var)
                            (vec (map js-parse-sub-exp sel)))
                'fn#  (list 'aget 'obj# (js-parse-sub-exp fnc))]
          (apply list '.call 'fn# 'obj#
                 (js-expand args false)))))

(defn js-expand
  ([form] (js-expand form true))
  ([form pfn]
     (js-expand form pfn '#{! !> ? ?> obj arr
                            f.n def.n do.n
                            f*n def*n do*n def* property}))
  ([form pfn ex]
     (cond (set? form) (set (map js-expand form))

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

(comment
    (defmacro ? [sym]
      (js-expand-sym sym))

    (defmacro ?> [& args]
      (apply list (map js-expand args)))

    (defmacro ! [sym & [val]]
       (let [[var & ks] (js-split-syms sym)]
         (list 'purnam.native/aset-in (js-parse-var var)
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

    (defmacro property [sym & [readonly]]
      `(fn ([] (? ~sym))
           ([~'v]
            ~(if readonly
               `(throw (js/Error ~(str sym " is readonly")))
               `(cond (= "object" 
                      (js/goog.typeOf (? ~sym))
                      (js/goog.typeOf ~'v))
                   (purnam.native/js-replace (? ~sym) ~'v)
                  :else
                  (! ~sym ~'v))))))
)
;; Macro to create objects

(defn get-sym-root [sym]
  (let [syms  (s/split (name sym) #"\.")]
    (symbol (first syms))))

(defn has-sym-root? [sym root]
  (let [syms  (s/split (name sym) #"\.")]
    (cond (or (hash-map? root) (hash-set? root))
          (root (symbol (first syms)))
          :else
          (= (str root) (first syms)))))

(defn cons-sym-root [sym root]
  (symbol (str root "." sym)))

(defn change-sym-root
  ([sym new]
     (let [syms  (s/split (str sym) #"\.")
           nsyms (cons (str new) (rest syms))]
       (symbol (s/join "." nsyms))))
  ([sym old new]
     (if (has-sym-root? sym old)
       (change-sym-root sym new)
       sym)))

(defn walk-and-transform
  [form pred? ex-pred? transform]
  (let [r-fn #(walk-and-transform % pred? ex-pred? transform)]
    (cond (suppress (pred? form))     (transform form)
          (suppress (ex-pred? form))  form
          (vector? form)   (mapv r-fn form)
          (hash-set? form) (set (map r-fn form))
          (hash-map? form) (into {} (map (fn [[k x]] [k (r-fn x)]) form))
          (seq? form) (apply list (map r-fn form))
          :else form)))

(defn has-first-element [obj f]
  (and (or (list? obj) (lazy-seq? obj))
       (f (first obj))))

(defn change-roots
  ([form old new] (change-roots form old new #{}))
  ([form old new exclude]
     (walk-and-transform
      form
      #(has-sym-root? % old)
      #(has-first-element % exclude)
      #(change-sym-root % new))))

(defn change-roots-map
  ([form m] (change-roots-map form m #{}))
  ([form m exclude]
     (walk-and-transform
      form
      #(has-sym-root? % m)
      #(has-first-element % exclude)
      #(change-sym-root % (m (get-sym-root %))))))

(declare make-var)

(defn make-js-object-resolve [sym]
  (cond (symbol? sym) sym
        (keyword? sym) (.substring (str sym) 1)
        :else (str sym)))

(defn make-js-object-aset [sym [k v]]
  (list 'aset sym (make-js-object-resolve k)
        (make-var
         (change-roots-map v {'self sym} #{'obj}))))

(defn make-js-object
  ([m] (make-js-object nil m))
  ([sym m]
     (let [sym  (or sym (gensym))
           context (gensym)
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

(comment
  (defmacro obj [& args]
      (let [m (apply hash-map args)]
        (js-expand (make-var m))))

  (defmacro arr [& args]
    (let []
       (js-expand (make-js-array args)))))

(def default-binding-forms '#{let loop for doseq if-let when-let})

(declare walk-js-raw)

(defn walk-binding-form [[f bindings & body]]
  (let [b (partition 2 bindings)
        res (-> (mapcat (fn [[k v]] [k (walk-js-raw v)]) b)
                vec)]
    (apply list f res (walk-js-raw body))))

(defn walk-lambda-form [[f bindings & body]]
  (apply list f bindings (walk-js-raw body)))

(defn walk-js-raw [form]
  (cond (vector? form)
        (apply list 'array (map walk-js-raw form))

        (hash-map? form)
        (apply list 'obj
               (mapcat (fn [[k x]] [k (walk-js-raw x)]) form))

        (seq? form)
        (cond (default-binding-forms (first form))
              (walk-binding-form form)

              (= 'fn (first form))
              (walk-lambda-form form)

              :else
              (apply list (map walk-js-raw form)))
        :else form))

(comment
  (defmacro def* [name form]
    `(def ~name
          ~(js-expand (walk-js-raw form))))

  (defmacro def*n [name args & body]
    `(defn ~name ~args
           ~@(js-expand (walk-js-raw body))))

  (defmacro f*n [args & body]
    `(fn ~args ~@(js-expand (walk-js-raw body))))

  (defmacro do*n [& body]
    `(do ~@(js-expand (walk-js-raw body)))))