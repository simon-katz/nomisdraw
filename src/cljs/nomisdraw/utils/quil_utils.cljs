(ns nomisdraw.utils.quil-utils
  (:require [cljs.core.async :as a]
            [quil.core :as q :include-macros true]
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

(defn sketch-in-reagent [& {:as sketch-args}]
  ;; TODO: Add doc.
  (assert (not (contains? sketch-args :host))
          ":host should not be provided to `sketch-in-reagent`")
  (assert (not (= (:size sketch-args)
                  :fullscreen))
          ":fullscreen not supported as a size for `sketch-in-reagent`")
  (let [[w h] (:size sketch-args)
        canvas-id (random-canvas-id)
        canvas-tag-&-id  (keyword (str "canvas#" canvas-id))]
    ;; Use a go block so that the canvas exists
    ;; before we attach the sketch to it.
    (a/go
      (apply q/sketch
             (concat (apply concat
                            (into [] sketch-args))
                     [:host canvas-id])))
    (-> [canvas-tag-&-id {:width  w
                          :height h}]
        prevent-horizontal-stretching)))

;;;; TODO: See note about CPU usage
