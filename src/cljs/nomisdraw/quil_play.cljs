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

(defn ^:private random-lowercase-string [length]
  (let [ascii-codes (range 97 123)]
    (apply str (repeatedly length #(char (rand-nth ascii-codes))))))

(defn ^:private random-canvas-id []
  (random-lowercase-string 30))

(defn ^:private my-sketch [canvas-name w h]
  (letfn [(initial-state []
            {:time 1})
          (update-state [state]
            (update state :time inc))
          (draw [state]
            (q/background 255)
            (q/fill 0)
            (q/ellipse (rem (:time state) w)
                       (rem (:time state) h)
                       55
                       55))]
    (q/defsketch fixme-!!!!-plop ; FIXME: can't make `q/sketch` work on browser refresh
      :setup      initial-state
      :update     update-state
      :draw       draw
      :host       canvas-name
      :middleware [qm/fun-mode]
      :size       [w h])))

(defn a-sketch-in-reagent [w h]
  (let [canvas-id (random-canvas-id)]
    (r/create-class
     {:reagent-render (fn []
                        (let [element-wotsit (keyword (str "canvas#" canvas-id))]
                          [element-wotsit {:width w :height h}]))
      :component-did-mount #(my-sketch canvas-id w h)})))
