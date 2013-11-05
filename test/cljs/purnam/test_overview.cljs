(ns purnam.test-overview
  (:use [purnam.cljs :only [aget-in aset-in]])
  (:use-macros [purnam.js :only [obj arr ? ?> ! !> def.n
                                 f.n do.n f*n do*n def* def*n]]
               [purnam.test :only [init describe it is is-not beforeEach]]))

(init)

(describe
   {:doc "obj - primitive object constructor"}
   (it "1.1"
       (is
        (obj "key1" "val1" "key2" "val2")
        (js* "{key1: 'val1', key2: 'val2'}")))

   (it "1.2"
       (is
        (obj :key1 "val1" :key2 "val2")
        (js* "{key1: 'val1', key2: 'val2'}")))

   (it "1.3"
       (let [s1 "key1"
             s2 "key2"]
         (is (obj s1 "val1" s2 "val2")
                   (js* "{key1: 'val1', key2: 'val2'}")))
       (let [s1 :key1
             s2 :key2]
         (is-not (obj s1 "val1" s2 "val2")
                   (js* "{key1: 'val1', key2: 'val2'}"))))

   (it "1.4"
       (is
        (obj :data [{:id 1 :name "one"}
                    {:id 2 :name "two"}])
        (js* "{data: [{id:1,name:'one'},
                    {id:2,name:'two'}]}")))

   (it "1.5"
       (is
        (obj :name "l1" :data [1 2 3]
             :next {:name "l2" :data [4 5 6]
                    :next {:name "l3" :data [7 8 9]}})
        (js* "{name: 'l1',
              data: [1,2,3],
              next: {name: 'l2',
                     data: [4,5,6],
                     next: {name: 'l3',
                            data: [7,8,9]}}}"))))

(describe
 {:doc "arr - primitive array constructor"}

 (it "2.1"
     (is (arr 1 2 3 4 5)
               (js* "[1,2,3,4,5]")))

 (it "2.2"
     (is
      (arr {:data [1 2 3 4 5]}
           {:data [6 7 8 9 10]})
      (js* "[{data: [1,2,3,4,5]},
             {data: [6,7,8,9,10]}]"))))




(describe
 {:doc "? - object/array accessor"
  :globals [ka "a"
            kb "b"]
  :vars [o (obj :a 1 :b 2 :c 3)]}

 (it "3.1"
     (is 1 o.a)
     (is 6 (+ o.a o.b o.c)))
     
 (it "3.2"
     (is 1 (? o.|ka|))
     (is 3 (+ (? o.|ka|) (? o.|kb|))))

 (it "3.3"
     (! o (arr [1 2 3]
               [4 5 6]
               [7 8 9]))
     (is 8 (- (? o.2.2) (? o.0.0))) )

 (it "3.4"
     (! o (obj :a {:data 1}))
     (is (? o.a.data) 1)
     (is (? o.b.data) nil)
     (is (? o.any.nested.syntax) nil)))

(describe
 {:doc "?> - call with notation"
  :vars [o (obj :a 1 :b 2)]}

 (it "4.1"
     (is (?> + o.a o.b) 3)
     (is (?> - o.a o.b) -1))

 (it "4.2"
     (is
      (?> mapv (fn [x] (inc x.a))   ;; no need to write (inc (? x.a))
          [(obj :a 1) (obj :a 2) (obj :a 3)])
      [2 3 4])
     (is
      (?> .map (arr {:a 1} {:a 2} {:a 3})
          (fn [x] (inc x.a)))
      (js* "[2,3,4]"))))

(describe
 {:doc "! - setter"}

 (it "5.1"
     (let [o (obj)]
       (is (? o.a) nil)
       (! o.a 6)
       (is (? o.a) 6)))

 (it "5.2"
     (let [o (obj)
           k "a"]
       (is (? o.|k|) nil)
       (! o.|k| 6)
       (is (? o.|k|) 6)
       (is (? o.a) 6)))

 (it "5.3"
     #_(let [o (obj)]
         (! o (obj :a 1)))) ;; cannot compile

 (it "5.4"
     (let [o (obj)]
       (is (? o.a.b.c.d) nil)
       (is (? o.a) nil)
       (! o.a.b.c.d 6)
       (is (? o.a.b.c.d) 6)
       (is
        (? o.a)
        (obj :b {:c {:d 6}}))))

 (it "5.5"
     (let [o1 (obj :a 1)
           o2 (obj :a {})]
       (! o1.a.b.c 10)
       (! o2.a.b.c 10)

       (is (? o1.a.b.c) nil)
       (is (? o2.a.b.c) 10))))


(describe
 {:doc "!> - call on object"}
 (it "6.1"
     (let [a (arr)]
       (!> a.push 1)
       (!> a.push 2)
       (!> a.push 3)
       (is (? a.0) 1)
       (is (? a.1) 2)
       (is (? a.2) 3)))

 (it "6.2"
     (let [a (arr)
           k "push"]
       (!> a.|k| 1)
       (!> a.|k| 2)
       (is (? a.0) 1)
       (is (? a.1) 2))))

(describe
 {:doc "this - context"}
 (it "7.1"
     (let [o1 (obj :a 10
                   :func (fn [] this.a))
           o2 (obj :a 20
                   :func (? o1.func))]
       (is (!> o1.func) 10)
       (is (!> o2.func) 20)))

 (it "7.2"
     (let [o1 (obj :a 10
                   :b {:a 20
                       :func (fn [] this.a)})]
       (is (!> o1.b.func) 20))))

(describe
 {:doc "self - self reference"}
 (it "8.1"
     (let [o1 (obj :a 10
                   :func (fn [] self.a))
           o2 (obj :a 20
                   :func (? o1.func))]
       (is (!> o1.func) 10)
       (is (!> o2.func) 10)))

 (it "8.2"
     (let [o1 (obj :a 10
                   :b {:a 20
                       :func (fn [] self.a)})
           o2 (obj :a 10
                   :b (obj :a 20
                           :func (fn [] self.a)))]
       (is (!> o1.b.func) 10)
       (is (!> o2.b.func) 20))))


#_(defn add-inner [a b]
      (+ a.inner b.inner))

#_(defn print-this []
        (js/console.log this))


(def.n add-inner [a b]
  (+ a.inner b.inner))

(describe
 {:doc "f.n and def.n - function definitions"}
 (it "9.1"
     (let [a (obj :inner 1)
           b (obj :inner 2)]
       (is 3 (add-inner a b))))

 (it "9.2"
     (let [a (obj :inner 1)
           b (obj :inner 2)
           add2 (f.n [a b] (+ a.inner b.inner))]
       (is 3 (add2 a b)))))

(describe
 {:doc "do.n - do block"}
 (it "10.1"
     (let [o1 (obj :inner 1)
           o2 (obj :inner 2)
           o3 (obj :inner 3)
           o (obj)]
       (is
        (do.n
         (! o.val.a (+ o1.inner o2.inner))
         (! o.val.b (* o2.inner o3.inner))
         o.val)
        (obj :a 3 :b 6)))))

(def* f1 (fn [o] (! o.val [1 2 3 4 5])))
(def*n f2 [o] (! o.val [1 2 3 4 5]))
(describe
 {:doc "def* def*n and f*n - function"
  :globals [a (obj)]}

 (beforeEach (! a.val nil))

 (it "def*"
     (is a.val nil)
     (f1 a)
     (is a.val
               (arr 1 2 3 4 5)))

 (it "def*n"
     (is a.val nil)
     (f2 a)
     (is a.val
               (arr 1 2 3 4 5)))
 (it "f*n"
     (is a.val nil)
     ((f*n [o] (! o.val [1 2 3 4 5])) a)
     (is a.val
               (arr 1 2 3 4 5)))
 (it "do*n"
     (is a.val nil)
     (do*n
      ((fn [o] (! o.val [1 2 3 4 5])) a))
     (is a.val
               (arr 1 2 3 4 5))))

(def* o1 {:a 10
          :b {:a 20
              :func (fn [] self.a)}})
(def* o2 (obj :a 10
              :b {:a 20
                  :func (fn [] self.a)}))

(describe
 {:doc "def*"}
 (it ""
     (is (o1.b.func) 20)
     (is (o2.b.func) 20)))
