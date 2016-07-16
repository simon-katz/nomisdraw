(ns nomisdraw.utils.quil-utils
  (:require [cljs.core.async :as a]
            [quil.core :as q :include-macros true]
            [reagent.core :as r]
            [re-com.core :as re])
  (:require-macros [cljs.core.async.macros :as a]))

;;;; Making Quil work well with Reagent...
;;;; - This core idea comes from
;;;;   http://stackoverflow.com/questions/33345084/quil-sketch-on-a-reagent-canvas
;;;; - I've made it more functional.

;;;; TODO: reusing-of-canvas-elements
;;;;       Investigate and maybe report bug.
;;;;       When a canvas element is reused by a sketch (as will happen when
;;;;       re-rendering happens (because of change of state or a Figwheel
;;;;       reload)), Quil 2.4.0 is supposed to destroy the previous processing
;;;;       object.
;;;;       See commit https://github.com/quil/quil/commit/be03cdeb61e78ecf4d3e09a5fb8e8d3658631816.
;;;;       (Is that supposed to completely get rid of the sketch?)
;;;        But I find the browser gets slower and slower and CPU usage
;;;;       gets higher and higher. That's because the previous sketches' draw
;;;;       functions continue to be called.
;;;;       Hacky fix: wrap draw function in a check for being unmounted.

(defn ^:private prevent-horizontal-stretching [elem]
  [re/h-box
   :size "none" ; seems to be the default, but not documented AFAICS
   :children
   [elem]])

(defn sketch-in-reagent [canvas-id & {:as sketch-args}]
  ;; TODO: Add doc.
  (assert (not (contains? sketch-args :host))
          ":host arg not permitted (because host is being created here)")
  (assert (contains? sketch-args :draw))
  (let [size            (:size sketch-args)
        _               (assert (or (nil? size)
                                    (and (vector? size)
                                         (= (count size) 2)))
                                (str ":size should be nil or a vector of size 2, but it is "
                                     size))
        [w h]           size
        canvas-tag-&-id (keyword (str "canvas#" canvas-id))
        unmounted?-atom (atom false) ; see reusing-of-canvas-elements
        draw            (:draw sketch-args)
        draw'           (fn [& args]
                          (when-not @unmounted?-atom
                            (apply draw args)))
        sketch-args'    (merge sketch-args
                               {:draw draw'
                                :host canvas-id})]
    (-> [r/create-class
         {:reagent-render         (fn []
                                    [canvas-tag-&-id {:width  w
                                                      :height h}])
          :component-did-mount    (fn []
                                    ;; Use a go block so that the canvas exists
                                    ;; before we attach the sketch to it.
                                    ;; (Needed on initial render; not on
                                    ;; re-render.)
                                    (a/go
                                      (apply q/sketch
                                             (apply concat sketch-args'))))
          :component-will-unmount (fn []
                                    (reset! unmounted?-atom true))}]
        prevent-horizontal-stretching)))
