(ns nomisdraw.play.examples-from-quil-intro
  (:require [nomisdraw.utils.nomis-re-com-utils :as reu]
            [nomisdraw.utils.nomis-quil-on-reagent :as qor]
            [quil.core :as q :include-macros true]
            [quil.middleware :as m]
            [reagent.core :as r]
            [re-com.core :as re]))

;;;; Working through https://nbeloglazov.com/2014/05/29/quil-intro.html

;;;; ---------------------------------------------------------------------------
;;;; Static images

(defn something-that-uses-no-ongoing-cpu []
  [:p "Quil sketches can use up a lot of CPU time. This text doesn't."])

(defn example-1 [canvas-id]
  (letfn [(draw []
            (q/frame-rate 0.1)
            
            ;; make background white
            (q/background 255)

            ;; move origin point to centre of the sketch
            ;; by default origin is in the left top corner
            (q/with-translation [(/ (q/width) 2) (/ (q/height) 2)]
              ;; parameter t goes 0, 0.01, 0.02, ..., 99.99, 100
              (doseq [t (range 0 100 0.01)]
                ;; draw a point with x = t * sin(t) and y = t * cos(t)
                (q/point (* t (q/sin t))
                         (* t (q/cos t))))))]
    (qor/sketch :size [300 300]
                :draw draw
                :host canvas-id)))

(defn make-example [canvas-id f]
  (letfn [(draw-plot [f from to step]
            (doseq [two-points (->> (range from to step)
                                    (map f)
                                    (partition 2 1))]
              ;; we could use 'point' function to draw a point
              ;; but let's rather draw a line which connects 2 points of the plot
              (apply q/line two-points)))
          (draw []
            (q/background 255)
            (q/with-translation [(/ (q/width) 2) (/ (q/height) 2)]
              (draw-plot f 0 100 0.01)))]
    (qor/sketch :size [300 300]
                :draw draw
                :host canvas-id)))

(defn example-2 [canvas-id]
  (make-example canvas-id
                (fn [t]
                  [(* t (q/sin t))
                   (* t (q/cos t))])))

(defn example-3 [canvas-id]
  (make-example canvas-id
                (fn [t]
                  (let [r (case 1
                            1 (* 200 (q/sin t) (q/cos t))
                            2 (* 100 (q/sin t))
                            3 (* 100 (q/cos t))
                            4 (* 100 (q/tan t))
                            5 100)]
                    [(* r (q/sin (* t 0.2)))
                     (* r (q/cos (* t 0.2)))]))))

;;;; TODO: Can you have the draw function called only once?
;;;;       The above examples waste CPU.

;;;; ---------------------------------------------------------------------------
;;;; Animation

(defn example-4 [canvas-id]
  (letfn [(f [t]
            (let [r (* 200 (q/sin t) (q/cos t))]
              [(* r (q/sin (* t 0.2)))
               (* r (q/cos (* t 0.2)))]))
          (draw []
            (q/with-translation [(/ (q/width) 2) (/ (q/height) 2)]
              ;; note that we don't use draw-plot here as we need
              ;; to draw only small part of a plot on each iteration
              (let [t (/ (q/frame-count) 10)]
                (q/line (f t)
                        (f (+ t 0.1))))))
          ;; 'setup' is a cousin of 'draw' function
          ;; setup initialises sketch and it is called only once
          ;; before draw called for the first time
          (setup []
            ;; draw will be called 60 times per second
            (q/frame-rate 60)
            ;; set background to white colour only in the setup
            ;; otherwise each invocation of 'draw' would clear sketch completely
            (q/background 255))]
    (qor/sketch :size [300 300]
                :setup setup
                :draw draw
                :host canvas-id)))

;;;; ---------------------------------------------------------------------------


(defn example-1a [] (example-1 "quil-intro-example-1a"))
(defn example-2a [] (example-2 "quil-intro-example-2a"))
(defn example-3a [] (example-3 "quil-intro-example-3a"))
(defn example-4a [] (example-4 "quil-intro-example-4a"))

(defn example-1b [] (example-1 "quil-intro-example-1b"))
(defn example-2b [] (example-2 "quil-intro-example-2b"))
(defn example-3b [] (example-3 "quil-intro-example-3b"))
(defn example-4b [] (example-4 "quil-intro-example-4b"))

(defn render []
  [re/v-box
   :children
   [[reu/dropdown-and-chosen-item [{:id :something-that-uses-no-ongoing-cpu
                                    :label "Low CPU usage"
                                    :fun #'something-that-uses-no-ongoing-cpu}
                                   {:id :example-1a
                                    :label "Example 1a"
                                    :fun #'example-1a}
                                   {:id :example-2a
                                    :label "Example 2a"
                                    :fun #'example-2a}
                                   {:id :example-3a
                                    :label "Example 3a"
                                    :fun #'example-3a}
                                   {:id :example-4a
                                    :label "Example 4a"
                                    :fun #'example-4a}]]
    [reu/dropdown-and-chosen-item [{:id :something-that-uses-no-ongoing-cpu
                                    :label "Low CPU usage"
                                    :fun #'something-that-uses-no-ongoing-cpu}
                                   {:id :example-1b
                                    :label "Example 1b"
                                    :fun #'example-1b}
                                   {:id :example-2b
                                    :label "Example 2b"
                                    :fun #'example-2b}
                                   {:id :example-3b
                                    :label "Example 3b"
                                    :fun #'example-3b}
                                   {:id :example-4b
                                    :label "Example 4b"
                                    :fun #'example-4b}]]]])
