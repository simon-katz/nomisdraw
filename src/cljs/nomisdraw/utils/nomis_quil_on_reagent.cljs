(ns nomisdraw.utils.nomis-quil-on-reagent
  (:require [cljs.core.async :as a]
            [goog.dom :as dom]
            [quil.core :as q :include-macros true]
            [reagent.core :as r])
  (:require-macros [cljs.core.async.macros :as a]))

;;;; Making Quil work well with Reagent...
;;;; - I got the core idea from skrat's answer at
;;;;   http://stackoverflow.com/questions/33345084/quil-sketch-on-a-reagent-canvas
;;;; - I've made it more functional.

(defn ^:private random-lowercase-string [length]
  (let [ascii-codes (range 97 123)]
    (apply str (repeatedly length #(char (rand-nth ascii-codes))))))

(defn ^:private random-canvas-id []
  (random-lowercase-string 40))

(defn sketch
  "Wraps `quil.core/sketch` and plays nicely with Reagent.
  Below, C = the canvas that will host the sketch.
  Differs from `quil.core/sketch` as follows:
  - Creates C (rather than C having to be created separately).
  - The `:host` argument must not be provided. (Instead, a unique canvas id is
    created.)
  - Returns a component that wraps C.
  - The :size argument must be either `nil` or a [width height] vector."
  [& {:as sketch-args}]
  (assert (not (contains? sketch-args :host))
          ":host should not be provided, because a unique canvas id will be created")
  (let [size            (:size sketch-args)
        _               (assert (or (nil? size)
                                    (and (vector? size)
                                         (= (count size) 2)))
                                (str ":size should be nil or a vector of size 2, but it is "
                                     size))
        [w h]           size
        canvas-id       (random-canvas-id)
        canvas-tag-&-id (keyword (str "canvas#" canvas-id))
        sketch-args*    (merge sketch-args 
                               {:host canvas-id})]
    [r/create-class
     {:reagent-render
      (fn []
        [canvas-tag-&-id {:style {;; Prevent stretching when used in
                                  ;; flex container. (I don't really
                                  ;; understand, but never mind).
                                  :max-width w}
                          :width  w
                          :height h}])
      ;;
      :component-did-mount
      (fn []
        ;; Use a go block so that the canvas exists before we attach the sketch
        ;; to it. (Needed on initial render; not on re-render.)
        (a/go
          (apply q/sketch
                 (apply concat sketch-args*))))
      ;;
      :component-will-unmount
      (fn []
        (q/with-sketch (q/get-sketch-by-id canvas-id)
          (q/exit)))}]))
