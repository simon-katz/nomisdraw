(ns nomisdraw.render
  (:require [re-com.core :as re]
            [nomisdraw.play.re-com-slowness-play :as slowness-play]
            [nomisdraw.quil-play :as qp]))

(defn ^:private style-for-top-level-div []
  (let [m "20px"]
    {:margin-left  m
     :margin-right m}))

(defn top-level-render []
  [re/v-box
   :style (style-for-top-level-div)
   :children
   [[:h1 "Nomisdraw"]
    [qp/some-quil-stuff]
    (case 1
      1 nil
      2 (slowness-play/make-re-com-stuff))]])
