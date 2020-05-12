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
  (* (if (< v 0) -1 1)
     (min 10
          (/ (q/abs v) 2))))

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
      (let [vv-x    (- (q/pmouse-x) eye-centre-x)
            v-x     (case id :left vv-x :right (- vv-x))
            delta-x (v->n-x-pixels v-x)
            delta-x (case id :left delta-x :right (- delta-x))
            ratio-x (if (zero? vv-x) 999999 (q/abs (/ delta-x vv-x)))
            vv-y    (- (q/pmouse-y) eye-centre-y)
            v-y     vv-y
            delta-y (* 0.5 (v->n-x-pixels v-y))
            ratio-y (q/abs (if (zero? vv-y) 999999 (/ delta-y vv-y)))
            use-x?  (< ratio-x ratio-y)
            delta-x (if (and (not use-x?)
                             (not (zero? ratio-y)))
                      (* ratio-y vv-x)
                      delta-x)
            delta-y (if (and use-x?
                             (not (zero? ratio-x)))
                      (* ratio-x vv-y)
                      delta-y)]
        (q/ellipse (+ eye-centre-x delta-x)
                   (+ eye-centre-y delta-y)
                   7
                   7)))))

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
  (q/ellipse centre-x centre-y 20 20)
  (q/fill 0 255 0)
  (q/ellipse centre-x centre-y 14 14)
  (q/fill 255 0 0)
  (q/ellipse centre-x centre-y  8  8))

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
