(ns nomisdraw.play.re-com-slowness-play
  (:require [re-com.core :as re]))

(defn render-n [approach nesting-level]
  (time
   (let [text "Rhubarb"]
     (letfn [(stuff [n]
               (let [elements (repeat 5 [:p (str n " " text)])]
                 (case approach
                   1 `[:div ~@elements]
                   2 [re/v-box :children elements])))]
       (reduce (fn [sofar next] [:div next sofar])
               nil
               (map stuff (range nesting-level)))))))

(defn render []
  (render-n 1
            (case 2
              1   3
              2 620
              3 100)))
