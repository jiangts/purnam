(ns purnam.common.parse
  (:require [clojure.string :as s]
            [purnam.common :refer :all]))

(defn conj-if-str [arr s]
  (if (empty? s) arr
      (conj arr s)))

(defn reconstruct-dotted [output current ss]
  (str (s/join "." output) "." current (apply str ss)))

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


(defn symbol-with-ns? [sym]
 (if-let [[_ nstr sstr rstr] (re-find #"([\.\w\_\-]+)/\.?([\w\_\-]+\.?(.*))" (str sym))]
   (cond (empty? rstr) true
         (if-let [ns (suppress (the-ns (symbol nstr)))]
           (contains? (ns-map ns) (symbol sstr))) true)))

(defn symbol-contains-dot? [sym]
 (if (re-find #"[^\.]+\.[^\.]" (str sym)) true))

(defn exp? [sym]
  (cond (not (symbol? sym)) false
        (suppress (resolve sym)) false
        (contains? (ns-map *ns*) sym) false
        (.startsWith (str sym) ".") false
        (.endsWith (str sym) ".") false
        (symbol-with-ns? sym) false
        (symbol-contains-dot? sym) true
        :else false))

(defn split-first [sym]
 (let [ss  (str sym)
       res (or (re-find #"(^[^\|/]+/\.?[^\.\|/]+)(\..*)" ss)
               (re-find #"(^[^\|\./]+)(\..*)" (str sym)))]
   (next res)))

(defn split-syms [sym]
 (if-let [[k ks] (split-first sym)]
   (split-dotted [k] "" ks)
   (throw (Exception. (str "js-exp: " sym " cannot be split")))))

(declare parse-sub-exp)

(defn parse-var [var-str]
 (if (= var-str "this") '(js* "this")
     (symbol var-str)))

(defn parse-exp [sym]
(let [[var & ks] (split-syms sym)]
  (list 'purnam.common/aget-in (parse-var var)
        (vec (map parse-sub-exp ks)))))

(defn parse-sub-exp [ss]
 (if-let [res (re-find #"^\|(.*)\|$" ss)]
   (let [sym (symbol (second res))]
     (if (symbol-contains-dot? sym)
       (parse-exp sym)
       sym))
   ss))
