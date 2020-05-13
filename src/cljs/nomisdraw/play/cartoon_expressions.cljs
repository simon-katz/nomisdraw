(ns nomisdraw.play.cartoon-expressions
  (:require [nomisdraw.utils.nomis-quil-on-reagent :as qor]
            [quil.core :as q :include-macros true]
            [quil.middleware :as m]
            [re-com.core :as re]))

(def ^:private width  650)
(def ^:private height 400)

(def eyebrow-inner-delta-y -3)
(def eyebrow-fudge-factor 1.3)

(defn make-centre-x-a []
  (/ (q/width) 2))

(defn make-centre-y-a []
  (/ (q/height) 2))

(defn ^:private face-outline [centre-x centre-y]
  (q/stroke 0)
  (q/stroke-weight 10)
  (q/fill 255)
  (q/ellipse centre-x
             centre-y
             200
             200))

(defn ^:private eye-brows [centre-x centre-y]
  (q/stroke 0)
  (doseq [id [:left :right]]
    (q/fill 0)
    (let [dx            (case id
                          :left 30
                          :right -30)
          eye-centre-x  (+ centre-x dx)]
      (q/stroke-weight 5)
      (let [towards-out   (case id :left + :right -)
            towards-in    (case id :left - :right +)
            brow-centre-y (- centre-y 50)
            w-slash-2     (/ 30 2.7)
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

(defn ^:private eyes [centre-x centre-y]
  (q/stroke 0)
  (q/stroke-weight 5)
  (doseq [id [:left :right]]
    (q/fill 255)
    (let [dx (case id
               :left 30
               :right -30)
          eye-centre-x (+ centre-x dx)
          eye-centre-y (- centre-y 30)]
      (q/ellipse (+ centre-x dx)
                 (- centre-y 30)
                 30
                 20)
      (q/fill 0)
      (q/ellipse eye-centre-x
                 eye-centre-y
                 7
                 7))))

(defn ^:private nose [centre-x centre-y]
  (q/stroke 0)
  (q/stroke-weight 5)
  (q/fill 255)
  (q/ellipse centre-x
             (+ centre-y 5)
             15
             30))

(defn ^:private mouth [centre-x centre-y]
  (q/stroke 0)
  (q/stroke-weight 5)
  (q/fill 255 0 0)
  (q/ellipse centre-x
             (+ centre-y 50)
             30
             5))

(defn ^:private my-sketch [w h]
  (letfn [(initial-state []
            (q/no-loop))
          (draw [state]
            (println "==== draw" state)
            (let [centre-x (make-centre-x-a)
                  centre-y (make-centre-y-a)]
              (q/background 100 0 100)
              ;;
              (face-outline centre-x centre-y)
              (eyes centre-x centre-y)
              (eye-brows centre-x centre-y)
              (nose centre-x centre-y)
              (mouth centre-x centre-y)))]
    (qor/sketch :setup      initial-state
                :draw       draw
                :middleware [m/fun-mode]
                :size       [w h])))

(defn render []
  (my-sketch width height))
