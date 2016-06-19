(ns nomisdraw.render
  (:require [re-com.core :as re]
            [nomisdraw.play.re-com-play :as re-com-play]))

(defn style-for-top-level-div []
  (let [m "20px"]
    {:margin-left  m
     :margin-right m}))

(defn top-level-render []
  [:div {:style (style-for-top-level-div)}
   [:h1 "nomisdraw"]
   (case 1
     1 nil
     2 (re-com-play/make-re-com-stuff))])
