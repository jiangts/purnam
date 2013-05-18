(ns angular-demo.combined
  (:use [purnam.cljs :only [aset-in aget-in]])
  (:use-macros
   [purnam.js :only [! def.n obj]]
   [purnam.angular :only [def.module def.config def.controller def.service]]))

(def.module combinedDemo [loginDemo todoDemo recipesDemo])