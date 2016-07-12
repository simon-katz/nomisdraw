(ns nomisdraw.utils.quil-utils
  (:require [cljs.core.async :as a]
            [quil.core :as q :include-macros true]
            [quil.middleware :as m]
            [reagent.core :as r]
            [re-com.core :as re])
  (:require-macros [cljs.core.async.macros :as a]))

;; Making Quil work well with Reagent...
;; - This core idea comes from
;;   http://stackoverflow.com/questions/33345084/quil-sketch-on-a-reagent-canvas
;; - I've made it more functional.

(defn ^:private random-lowercase-string [length]
  (let [ascii-codes (range 97 123)]
    (apply str (repeatedly length #(char (rand-nth ascii-codes))))))

(defn ^:private random-canvas-id []
  (random-lowercase-string 30))

(defn ^:private prevent-horizontal-stretching [elem]
  [re/h-box
   :size "none" ; seems to be the default, but not documented AFAICS
   :children
   [elem]])

(defn sketch-in-reagent [w h & sketch-args]
  (let [canvas-id (random-canvas-id)
        canvas-tag-&-id  (keyword (str "canvas#" canvas-id))]
    (-> [r/create-class
         {:reagent-render      (fn []
                                 [canvas-tag-&-id {:width  w
                                                   :height h}])
          :component-did-mount (fn []
                                 ;; use a go block so that the canvas exists
                                 ;; before we attach the sketch to it
                                 (a/go
                                   (apply q/sketch
                                          (concat sketch-args
                                                  [:host       canvas-id
                                                   :middleware [m/fun-mode]
                                                   :size       [w h]]))))}]
        prevent-horizontal-stretching)))
