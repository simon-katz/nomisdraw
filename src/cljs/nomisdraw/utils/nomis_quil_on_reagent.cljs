(ns nomisdraw.utils.nomis-quil-on-reagent
  (:require [cljs.core.async :as a]
            [quil.core :as q :include-macros true]
            [reagent.core :as r]
            [re-com.core :as re])
  (:require-macros [cljs.core.async.macros :as a]))

;;;; Making Quil work well with Reagent...
;;;; - I got the core idea from skrat's answer at
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
;;;;       TODO: This isn't working -- even though the draw function for an
;;;;             unmounted sketch returns immediately, things still get too slow
;;;;             just from changing what's being displayed.

(defn ^:private prevent-horizontal-stretching [elem]
  [re/h-box
   :size "none" ; seems to be the default, but not documented AFAICS
   :children
   [elem]])

(defn sketch
  "Wraps `quil.core/sketch` and plays nicely with Reagent.
  Below, C = the canvas that will host the sketch.
  Differs from `quil.core/sketch` as follows:
  - Creates C (rather than C having to be created separately), and the
   `:host` argument is the id of the canvas that will be created (rather
    than the id of an already-existing canvas).
  - Returns a component that wraps C.
  - The :size argument must be either `nil` or a [width height] vector."
  ;; Thoughts on the canvas id:
  ;; (1) You might think we could create our own unique canvas id.
  ;;     But no -- that would break re-rendering.
  ;; (2) You might think this could be done with a macro that creates the
  ;;     canvas id at compile time.
  ;;     But no -- the same call site can create multiple sketches.
  [& {:as sketch-args}]
  (assert (contains? sketch-args :host))
  (assert (contains? sketch-args :draw))
  (let [size            (:size sketch-args)
        _               (assert (or (nil? size)
                                    (and (vector? size)
                                         (= (count size) 2)))
                                (str ":size should be nil or a vector of size 2, but it is "
                                     size))
        [w h]           size
        canvas-id       (:host sketch-args)
        canvas-tag-&-id (keyword (str "canvas#" canvas-id))
        unmounted?-atom (atom false) ; see reusing-of-canvas-elements
        draw            (:draw sketch-args)
        draw'           (fn [& args]
                          (when-not @unmounted?-atom
                            (apply draw args)))
        sketch-args'    (merge sketch-args
                               {:draw draw'})]
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
