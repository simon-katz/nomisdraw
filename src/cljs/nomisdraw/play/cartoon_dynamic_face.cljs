(ns nomisdraw.play.cartoon-dynamic-face
  (:require [nomisdraw.utils.nomis-quil-on-reagent :as qor]
            [quil.core :as q :include-macros true]
            [quil.middleware :as m]
            [re-com.core :as re]))

(def ^:private width  650)
(def ^:private height 400)
(def ^:private the-frame-rate 60)

(defn ^:private t-dash [t]
  (* t (/ 60 ; default frame rate
          the-frame-rate)))

(defn make-centre-x-a [t]
  (+ (* 180 (q/sin (/ t 200)))
     320))

(defn make-centre-y-a [t]
  (+ (* 70 (q/sin (/ t 300)))
     200))

(defn make-centre-x-b [t]
  (q/pmouse-x))

(defn make-centre-y-b [t]
  (q/pmouse-y))

(defn ^:private face-outline [t centre-x centre-y]
  (q/stroke 0)
  (q/stroke-weight 10)
  (q/fill 255)
  (q/ellipse centre-x
             centre-y
             (+ 200 (* 20 (q/sin (/ t 40))))
             (+ 200 (* 40 (q/sin (/ t 30))))))

(defn ^:private v->n-x-pixels [v]
  (cond
    (<= v -30) -10
    (<= v -25)  -9
    (<= v -20)  -8
    (<= v -15)  -6
    (<= v -10)  -4
    (<= v -5)   -2
    (<= v 0)     0
    (<= v 5)     2
    (<= v 10)    4
    (<= v 20)    6
    (<= v 30)    8
    (<= v 40)    9
    :else       10))

(defn ^:private eyes [t centre-x centre-y]
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
      (q/ellipse (+ eye-centre-x
                    (let [vv    (- (q/pmouse-x) eye-centre-x)
                          v     (case id :left vv :right (- vv))
                          delta  (v->n-x-pixels v)]
                      (case id :left delta :right (- delta))))
                 (+ eye-centre-y
                    (let [v     (- (q/pmouse-y) eye-centre-y)
                          delta  (* 0.5 (v->n-x-pixels v))]
                      delta))
                 7
                 7))))

(defn ^:private nose [t centre-x centre-y]
  (q/stroke 0)
  (q/stroke-weight 5)
  (q/fill 255)
  (q/ellipse centre-x
             (+ centre-y 5)
             15
             30))

(defn ^:private mouth [t centre-x centre-y]
  (q/stroke 0)
  (q/stroke-weight 5)
  (q/fill (* 255 (q/abs (q/sin (/ t 100)))) 0 0)
  (let [sin-val (q/abs (q/sin (/ t 80)))]
    (q/ellipse centre-x
               (+ centre-y 50)
               (max (* 60 sin-val)
                    30)
               (max (* 20 sin-val)
                    5))))

(defn ^:private extras [t centre-x centre-y]
  (q/stroke 0)
  (q/stroke-weight 1)
  (q/fill 0 0 255)
  (q/ellipse centre-x centre-y 50 50)
  (q/fill 0 255 0)
  (q/ellipse centre-x centre-y 35 35)
  (q/fill 255 0 0)
  (q/ellipse centre-x centre-y 20 20))

(defn ^:private my-sketch [w h]
  (letfn [(initial-state []
            (q/frame-rate the-frame-rate)
            {:time 0})
          (update-state [state]
            (update state :time inc))
          (draw [state]
            ;; (println "==== (q/current-frame-rate) =" (q/current-frame-rate))
            (let [t        (t-dash (:time state))
                  centre-x (make-centre-x-a t)
                  centre-y (make-centre-y-a t)]
              (q/background 100 0 100)
              ;;
              (face-outline t centre-x centre-y)
              (eyes t centre-x centre-y)
              (nose t centre-x centre-y)
              (mouth t centre-x centre-y)
              ;;
              (extras t (make-centre-x-b t) (make-centre-y-b t))))]
    (qor/sketch :setup      initial-state
                :update     update-state
                :draw       draw
                :middleware [m/fun-mode]
                :size       [w h])))

(defn render []
  (my-sketch width height))
