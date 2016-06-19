(ns nomisdraw.quil-play
  (:require [quil.core :as q :include-macros true]
            [quil.middleware :as qm]
            [reagent.core :as r]))

;; How to make Quil work well with Reagent?
;; - This core idea comes from
;;   http://stackoverflow.com/questions/33345084/quil-sketch-on-a-reagent-canvas
;; - I've made it more functional.

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
    ;; FIXME:
    ;; I had `q/sketch` here, but it doesn't work on browser refresh.
    ;; It's OK on a Figwheel reload, though.
    ;; Changing to `q/defsketch` makes things work on both browser refresh
    ;; and a Figwheel reload.
    (q/defsketch fixme-!!!!-see-comment
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
