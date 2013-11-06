(ns midje-doc.api-purnam-angular
  (:use [purnam.cljs :only [aget-in aset-in js-equals]])
  (:use-macros [purnam.js :only [f.n def.n obj arr]]
               [purnam.angular :only [def.module def.controller def.service]]
               [purnam.test :only [init describe is it]]
               [purnam.test.sweet :only [fact facts]]
               [purnam.test.angular :only [describe.ng describe.controller it-uses]]))

[[{:hide true}]]
(init)

[[:chapter {:title "purnam.test.angular" :tag "purnam-test-angular"}]]
[[:section {:title "init" :tag "init-angular"}]]
[[:section {:title "controllers" :tag "controllers"}]]
[[:section {:title "services" :tag "services"}]]
[[:section {:title "directives" :tag "directives"}]]
[[:section {:title "filters" :tag "filters"}]]
