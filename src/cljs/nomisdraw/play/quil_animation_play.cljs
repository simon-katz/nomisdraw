(ns nomisdraw.play.quil-animation-play
  (:require [quil.core :as q :include-macros true]
            [quil.middleware :as m]
            [reagent.core :as r]
            [re-com.core :as re]))

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
            (let [t (:time state)]
              (q/background 255)
              (q/fill 255)
              (q/ellipse (rem t w)
                         (rem t h)
                         55
                         55)
              (q/fill 0)
              (let [tt (* t 100)
                    tt (* (rem tt 10000) 0.01)]
                (q/with-translation [(/ w 2) (/ h 2)]
                  (q/ellipse (* tt (q/sin tt))
                             (* tt (q/cos tt))
                             10
                             10)))
              (q/with-translation [(/ w 2) (/ h 2)]
                (doseq [t (range 0 100 0.01)]
                  (q/point (* t (q/sin t))
                           (* t (q/cos t)))))))]
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
      :middleware [m/fun-mode]
      :size       [w h])))

(defn ^:private boxify [elem]
  ;; Use this tp prevent horizontal stretching
  [re/h-box
   :size "none" ; seems to be the default, but not documented AFAICS
   :children
   [elem]])

(defn ^:private a-sketch-in-reagent [w h]
  (-> (let [canvas-id (random-canvas-id)]
        [r/create-class
         {:reagent-render (fn []
                            (let [element-wotsit (keyword (str "canvas#" canvas-id))]
                              [element-wotsit {:width  w
                                               :height h}]))
          :component-did-mount #(my-sketch canvas-id w h)}])
      boxify))

(defn some-quil-animation-stuff []
  [re/v-box
   :children
   [(for [i (range 2)]
      ^{:key i}
      [a-sketch-in-reagent 200 400])]])
