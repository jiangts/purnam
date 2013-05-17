(ns crafty-game.core
  (:use [purnam.cljs :only [aget-in aset-in]])
  (:require [goog.object :as o])
  (:use-macros [purnam.js :only [obj arr ! def.n def.n* def* do*]]))

(def* Game
  {:grid {:width 24
           :height 16
           :tile {:width 16 :height 16}}
   :width
   (fn [] (* this.grid.width this.grid.tile.width))

   :height
   (fn [] (* this.grid.height this.grid.tile.height))

   :start
   (fn []
     (let [G self]
       (js/Crafty.init (G.width) (G.height))
       (js/Crafty.background "rgb(249, 223, 125)")
       (-> js/Crafty (.e "PlayerCharacter") (.at 5 5))

       (doseq [x (range G.grid.width)
               y (range G.grid.height)]
         (let [edge? (or (zero? x)
                         (zero? y)
                         (= x (dec G.grid.width))
                         (= y (dec G.grid.height)))]
           (cond edge?
                 (-> js/Crafty
                     (.e "Tree") (.at x y)))))
       (let [v   {:count 0}
             max 10]
         (doseq [x (range 1 (dec G.grid.width))
                 y (range 1 (dec G.grid.height))]

           (cond (< (js/Math.random) 0.05)
                 (-> js/Crafty
                     (.e "Bush") (.at x y))

                 (>= v.count max) nil

                 (< (js/Math.random) 0.04)
                 (do (! v.count (inc v.count))
                     (-> js/Crafty
                         (.e "Village") (.at x y))))))))})

;; Components

(do*
 (js/Crafty.c
  "Grid"
  {:init
   (fn []
     (this.attr {:w Game.grid.tile.width
                 :h Game.grid.tile.height}))
   :at
   (fn [x y]
     (cond (and (= x js/undefined)
                (= y js/undefined))
           {:x (/ this.x Game.grid.tile.width)
            :y (/ this.y Game.grid.tile.height)}
           :else
           (do
             (this.attr {:x (* x Game.grid.tile.width)
                         :y (* y Game.grid.tile.height)})
             this)))})

 (js/Crafty.c
  "Actor"
  {:init
   (fn [] (this.requires "2D, Canvas, Grid"))
   })

 (js/Crafty.c
  "PlayerCharacter"
  {:init
   (fn []
     (-> this
         (.requires "Actor, Fourway, Color, Collision")
         (.fourway 4)
         (.color "rgb(20, 75, 40)")
         (.stopOnSolids)
         (.onHit "Village" this.visitVillage)))

   :stopOnSolids
   (fn []
     (-> this
         (.onHit "Solid" this.stopMovement))
     this)

   :stopMovement
   (fn []
     (! this._speed 0)
     (when this._movement
       (! this.x (- this.x this._movement.x))
       (! this.y (- this.y this._movement.y))))

   :visitVillage
   (fn [data]
     (let [village data.0.obj]
       (village.collect)))
   })

 (js/Crafty.c
  "Village"
  {:init
   (fn []
     (-> this
         (.requires "Actor, Color")
         (.color "rgb(170, 125, 40)")))
   :collect
   (fn []
     ;;(js/alert "Hello")
     (this.destroy)
     (js/Crafty.trigger "VillageVisited" this))})

 (js/Crafty.c
  "Tree"
  {:init
   (fn []
     (-> this
         (.requires "Actor, Color, Solid")
         (.color "rgb(20, 125, 40)")))})

 (js/Crafty.c
  "Bush"
  {:init
   (fn []
     (-> this
         (.requires "Actor, Color, Solid")
         (.color "rgb(20, 185, 40)")))}))



(comment
  (cond edge?
                 (-> js/Crafty
                     (.e "2D, Canvas, Color")
                     (.attr {:x (* x G.grid.tile.width)
                             :y (* y G.grid.tile.height)
                             :w G.grid.tile.width
                             :h G.grid.tile.height})
                     (.color ""))
                 (< (js/Math.random) 0.06)
                 (-> js/Crafty
                     (.e "2D, Canvas, Color")
                     (.attr {:x (* x G.grid.tile.width)
                             :y (* y G.grid.tile.height)
                             :w G.grid.tile.width
                             :h G.grid.tile.height})
                     (.color "black"))))
