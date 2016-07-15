(ns nomisdraw.utils.quil-utils
  (:require [cljs.core.async :as a]
            [quil.core :as q :include-macros true]
            [reagent.core :as r]
            [re-com.core :as re])
  (:require-macros [cljs.core.async.macros :as a]))

;; Making Quil work well with Reagent...
;; - This core idea comes from
;;   http://stackoverflow.com/questions/33345084/quil-sketch-on-a-reagent-canvas
;; - I've made it more functional.

(defn ^:private prevent-horizontal-stretching [elem]
  [re/h-box
   :size "none" ; seems to be the default, but not documented AFAICS
   :children
   [elem]])

(defn sketch-in-reagent [canvas-id & {:as sketch-args}]
  ;; TODO: Add doc.
  (assert (not (contains? sketch-args :host))
          ":host arg not permitted (because host is being created here)")
  (let [size            (:size sketch-args)
        _               (assert (or (nil? size)
                                    (and (vector? size)
                                         (= (count size) 2)))
                                (str ":size should be nil or a vector of size 2, but it is "
                                     size))
        [w h]           size
        canvas-tag-&-id (keyword (str "canvas#" canvas-id))
        unmounted?-atom (atom false)
        draw            (:draw sketch-args)
        draw'           (fn [& args]
                          (when-not @unmounted?-atom
                            (apply draw args)))
        sketch-args'    (assoc sketch-args :draw draw')]
    (-> [r/create-class
         {:reagent-render      (fn []
                                 [canvas-tag-&-id {:width  w
                                                   :height h}])
          :component-did-mount (fn []
                                 ;; Use a go block so that the canvas exists
                                 ;; before we attach the sketch to it.
                                 (a/go
                                   (apply q/sketch
                                          (concat (apply concat
                                                         (into [] sketch-args'))
                                                  [:host canvas-id]))))
          :component-will-unmount (fn []
                                    (reset! unmounted?-atom true))}]
        prevent-horizontal-stretching)))
