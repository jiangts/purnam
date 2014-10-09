(ns midje-doc.purnam-native-guide)

[[:chapter {:title "Introduction"}]]

"`purnam.native` offers methods for native javascript objects and arrays that are used in clojurescript. They are packaged into two namespaces:
  - [purnam.native](#purnam-native) - clojure protocols for native objects and arrays
  - [purnam.native.functions](#purnam-native-functions) - functions for native objects and arrays
"

[[:chapter {:title "Installation"}]]

"To use just the native functionality, add to `project.clj` dependencies:

  `[im.chit/purnam.native` \"`{{PROJECT.version}}`\"`]` 

The library is also included when `[im.chit/purnam` \"`{{PROJECT.version}}`\"`]` is added to `project.clj` dependencies."
    
[[:file {:src "test/midje_doc/api_purnam_native.cljs"}]]

[[:file {:src "test/midje_doc/api_purnam_native_functions.cljs"}]]
