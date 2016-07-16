(ns nomisdraw.play.examples-from-quil-intro
  (:require [nomisdraw.utils.dropdown-and-underling :as dau]
            [nomisdraw.utils.quil-on-reagent :as qor]
            [quil.core :as q :include-macros true]
            [quil.middleware :as m]
            [reagent.core :as r]
            [re-com.core :as re]))

;;;; Working through https://nbeloglazov.com/2014/05/29/quil-intro.html

;;;; ---------------------------------------------------------------------------
;;;; Static images

(defn example-001 []
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
                :host "quil-intro-example-001")))

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

(defn example-002 []
  (make-example "quil-intro-example-002"
                (fn [t]
                  [(* t (q/sin t))
                   (* t (q/cos t))])))

(defn example-003 []
  (make-example "quil-intro-example-003"
                (fn [t]
                  (let [r (* 200 (q/sin t) (q/cos t))]
                    [(* r (q/sin (* t 0.2)))
                     (* r (q/cos (* t 0.2)))]))))

;;;; TODO: Can you have the draw function called only once?
;;;;       The above examples waste CPU.

;;;; ---------------------------------------------------------------------------
;;;; Animation

(defn example-004 []
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
                :host "quil-intro-example-004")))

;;;; ---------------------------------------------------------------------------

(def ^:private choices [{:id :example-1
                         :label "Example 1"
                         :fun example-001}
                        {:id :example-2
                         :label "Example 2"
                         :fun example-002}
                        {:id :example-3
                         :label "Example 3"
                         :fun example-003}
                        {:id :example-4
                         :label "Example 4"
                         :fun example-004}])

(defonce ^:private selected-demo-id-atom (r/atom (-> choices
                                                     first
                                                     :id)))

(defn render []
  [dau/render-choices choices selected-demo-id-atom])
