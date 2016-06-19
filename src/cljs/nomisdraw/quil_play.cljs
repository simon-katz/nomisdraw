(ns nomisdraw.quil-play
  (:require [quil.core :as q :include-macros true]
            [quil.middleware :as qm]
            [reagent.core :as r]))

;; TODO: Find out how to make Quil work well with Reagent.
;; This stuff comes from
;; http://stackoverflow.com/questions/33345084/quil-sketch-on-a-reagent-canvas

(def w 400)
(def h 400)

(defn setup []
  {:t 1})

(defn update-x [state]
  (update-in state [:t] inc))

(defn draw [state]
  (q/background 255)
  (q/fill 0)
  (q/ellipse (rem (:t state) w) 46 55 55))

(q/defsketch foo
  :setup  setup
  :update update-x
  :draw   draw
  :host "foo"
  :no-start true
  :middleware [qm/fun-mode]
  :size [w h])

(defn hello-world []
  (r/create-class
    {:reagent-render (fn [] [:canvas#foo {:width w :height h}])
     :component-did-mount foo}))
