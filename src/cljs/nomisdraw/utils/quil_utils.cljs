(ns nomisdraw.utils.quil-utils
  (:require ;; [quil.core :as q :include-macros true]
            ;; [quil.middleware :as m]
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

(defn sketch-in-reagent [sketch-fun w h]
  (-> (let [canvas-id (random-canvas-id)]
        [r/create-class
         {:reagent-render (fn []
                            (let [element-wotsit (keyword (str "canvas#" canvas-id))]
                              [element-wotsit {:width  w
                                               :height h}]))
          :component-did-mount #(sketch-fun canvas-id w h)}])
      boxify))
