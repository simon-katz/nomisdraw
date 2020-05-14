(ns nomisdraw.play.cartoon-expressions
  (:require [nomisdraw.utils.nomis-quil-on-reagent :as qor]
            [quil.core :as q :include-macros true]
            [quil.middleware :as m]
            [re-com.core :as re]))

(def ^:private width  650)
(def ^:private height 400)

(def weight 3)

(def eyes-w-to-side 25)

(def eyebrow-fudge-factor    1.3)

(def eyebrows-h-below-centre -30)
(def eyes-h-below-centre     -10)
(def nose-h-below-centre      15)
(def mouth-h-below-centre     45)

(def eyes-pupils-w-adjustment  0)

(defn make-centre-xs [rs]
  (for [r rs]
    (* (q/width) r)))

(defn make-centre-y []
  (* (q/height) 0.5))

(defn ^:private face-outline [centre-x centre-y]
  (q/stroke 0)
  (q/stroke-weight weight)
  (q/fill 255)
  (q/ellipse centre-x
             centre-y
             125
             160))

(defn ^:private eye-brows [centre-x centre-y eyebrow-inner-delta-y]
  (q/stroke 0)
  (doseq [id [:left :right]]
    (q/fill 0)
    (let [dx            (case id
                          :left eyes-w-to-side
                          :right (- eyes-w-to-side))
          eye-centre-x  (+ centre-x dx)]
      (q/stroke-weight weight)
      (let [towards-out   (case id :left + :right -)
            towards-in    (case id :left - :right +)
            brow-centre-y (+ centre-y eyebrows-h-below-centre)
            w-slash-2     (/ eyes-w-to-side 2.7)
            fudge*delta-y (* eyebrow-fudge-factor
                             eyebrow-inner-delta-y)
            x-inner       (towards-in eye-centre-x
                                      w-slash-2
                                      fudge*delta-y)
            y-inner       (+ brow-centre-y eyebrow-inner-delta-y)
            x-outer       (towards-out eye-centre-x
                                       w-slash-2
                                       (- fudge*delta-y))
            y-outer       (- brow-centre-y eyebrow-inner-delta-y)]
        (q/line x-inner y-inner x-outer y-outer)))))

(defn ^:private eyes [centre-x centre-y eyes-pupils-h-adjustment]
  (q/stroke 0)
  (q/stroke-weight weight)
  (doseq [id [:left :right]]
    (q/fill 255)
    (let [dx (case id
               :left eyes-w-to-side
               :right (- eyes-w-to-side))
          eye-centre-x (+ centre-x dx)
          eye-centre-y (+ centre-y eyes-h-below-centre)]
      (q/ellipse (+ centre-x dx)
                 (+ centre-y eyes-h-below-centre)
                 30
                 20)
      (q/fill 0)
      (q/ellipse (+ eye-centre-x eyes-pupils-w-adjustment)
                 (+ eye-centre-y eyes-pupils-h-adjustment)
                 7
                 7))))

(defn ^:private nose [centre-x centre-y]
  (q/stroke 0)
  (q/stroke-weight weight)
  (q/fill 255)
  (q/ellipse centre-x
             (+ centre-y nose-h-below-centre)
             15
             30))

(defn ^:private mouth [centre-x centre-y]
  (q/stroke 0)
  (q/stroke-weight weight)
  (q/fill 0)
  (q/ellipse centre-x
             (+ centre-y mouth-h-below-centre)
             30
             2))

(defn ^:private face [centre-x
                      centre-y
                      eyebrow-inner-delta-y
                      eyes-pupils-h-adjustment]
  (face-outline centre-x centre-y)
  (eyes centre-x centre-y eyes-pupils-h-adjustment)
  (eye-brows centre-x centre-y eyebrow-inner-delta-y)
  (nose centre-x centre-y)
  (mouth centre-x centre-y))

(defn ^:private my-sketch [w h]
  (letfn [(initial-state []
            (q/no-loop))
          (draw [state]
            (println "==== draw" state)
            (q/background 100 0 100)
            (doseq [[centre-x
                     eyebrow-inner-delta-y
                     eyes-pupils-h-adjustment] (map vector
                                                    (make-centre-xs [0.1 0.3 0.5 0.7 0.9])
                                                    [-6 -3 0 3 6]
                                                    [-5 -2.5 0 2.5 5])]
              (let [centre-y (make-centre-y)]
                (face centre-x
                      centre-y
                      eyebrow-inner-delta-y
                      eyes-pupils-h-adjustment))))]
    (qor/sketch :setup      initial-state
                :draw       draw
                :middleware [m/fun-mode]
                :size       [w h])))

(defn render []
  (my-sketch width height))
