(ns nomisdraw.quil-play
  (:require [quil.core :as q :include-macros true]
            [quil.middleware :as qm]
            [reagent.core :as r]))

;; TODO: Find out how to make Quil work well with Reagent.
;; This stuff comes from
;; http://stackoverflow.com/questions/33345084/quil-sketch-on-a-reagent-canvas

;; TODO:
;; I'm trying to make this functional (pass w and h around).
;; Changing from `q/defsketch` to `q/sketch` makes things not work on
;; browser refresh. OK on a Figwheel reload.

(defn draw [state w h]
  (q/background 255)
  (q/fill 0)
  (q/ellipse (rem (:time state) w)
             (rem (:time state) h)
             55
             55))

(defn foo [w h]
  (letfn [(initial-state []
            {:time 1})
          (update-state [state]
            (update state :time inc))]
    (q/defsketch fixme-!!!!-plop ; FIXME: can't make `q/sketch` work on browser refresh
      :setup  initial-state
      :update update-state
      :draw   (fn [state] (draw state w h))
      :host "foo"
      :middleware [qm/fun-mode]
      :size [w h])))

(defn hello-world []
  (let [w 200
        h 400]
   (r/create-class
    {:reagent-render (fn []
                       [:canvas#foo {:width w :height h}])
     :component-did-mount #(foo w h)})))
