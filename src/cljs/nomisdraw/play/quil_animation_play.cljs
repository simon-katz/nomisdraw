(ns nomisdraw.play.quil-animation-play
  (:require [nomisdraw.utils.quil-utils :as qu]
            [quil.core :as q :include-macros true]
            [quil.middleware :as m]
            [re-com.core :as re]))

(defn ^:private my-sketch [canvas-id w h]
  (letfn [(initial-state []
            {:time 1})
          (update-state [state]
            (update state :time inc))
          (draw [state]
            (let [t (:time state)]
              (q/background 255)
              (q/fill 255)
              (q/ellipse (rem t w)
                         (rem t h)
                         55
                         55)
              (q/fill 0)
              (let [tt (* t 10)
                    tt (* (rem tt 10000) 0.01)]
                (q/with-translation [(/ w 2) (/ h 2)]
                  (q/ellipse (* tt (q/sin tt))
                             (* tt (q/cos tt))
                             10
                             10)))))]
    (qu/sketch-in-reagent canvas-id
                          :setup      initial-state
                          :update     update-state
                          :draw       draw
                          :middleware [m/fun-mode]
                          :size       [w h])))

(defn render []
  [re/v-box
   :children
   (for [i (range 2)]
     ^{:key i}
     (my-sketch (str "my-sketch-" i)
                200 400))])
