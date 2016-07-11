(ns nomisdraw.utils.quil-utils
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

(defn ^:private boxify [elem]
  ;; Use this to prevent horizontal stretching
  [re/h-box
   :size "none" ; seems to be the default, but not documented AFAICS
   :children
   [elem]])

(defn ^:private install-sketch-fun [tag-&-id w h sketch-fun]
  (-> [r/create-class
       {:reagent-render (fn []
                          [tag-&-id {:width  w
                                     :height h}])
        :component-did-mount sketch-fun}]
      boxify))

(defn sketch-in-reagent [w h & {:keys [setup update draw]}]
  ;; FIXME:
  ;; I had `q/sketch` here, but it doesn't work on browser refresh.
  ;; - I think it doesn't do the animation -- a background gets drawn.
  ;; It's OK on a Figwheel reload, though.
  ;; Changing to `q/defsketch` makes things work on both browser refresh
  ;; and a Figwheel reload.
  (let [canvas-id (random-canvas-id)
        tag-&-id  (keyword (str "canvas#" canvas-id))]
    (install-sketch-fun tag-&-id
                        w
                        h
                        (fn [] (q/defsketch fixme-!!!!-see-comment
                                 :setup      setup
                                 :update     update
                                 :draw       draw
                                 :host       canvas-id
                                 :middleware [m/fun-mode]
                                 :size       [w h])))))
