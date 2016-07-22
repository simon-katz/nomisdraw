(ns nomisdraw.utils.nomis-quil-on-reagent
  (:require [cljs.core.async :as a]
            [goog.dom :as dom]
            [quil.core :as q :include-macros true]
            [quil.sketch :as qs-maybe-non-api]
            [reagent.core :as r])
  (:require-macros [cljs.core.async.macros :as a]))

;;;; Making Quil work well with Reagent...
;;;; - I got the core idea from skrat's answer at
;;;;   http://stackoverflow.com/questions/33345084/quil-sketch-on-a-reagent-canvas
;;;; - I've made it more functional.

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
  (let [size            (:size sketch-args)
        _               (assert (or (nil? size)
                                    (and (vector? size)
                                         (= (count size) 2)))
                                (str ":size should be nil or a vector of size 2, but it is "
                                     size))
        [w h]           size
        canvas-id       (do 
                          (assert (contains? sketch-args :host))
                          (:host sketch-args))
        canvas-tag-&-id (keyword (str "canvas#" canvas-id))]
    [r/create-class
     {:reagent-render
      (fn []
        [canvas-tag-&-id {:style {;; Prevent stretching when used in flex container (I don't really understand, but never mind).
                                  :max-width w}
                          :width  w
                          :height h}])
      ;;
      :component-did-mount
      (fn []
        ;; Use a go block so that the canvas exists
        ;; before we attach the sketch to it.
        ;; (Needed on initial render; not on
        ;; re-render.)
        (a/go
          (apply q/sketch
                 (apply concat sketch-args))))
      ;;
      :component-will-unmount
      (fn []
        (-> canvas-id
            dom/getElement
            qs-maybe-non-api/destroy-previous-sketch))}]))
