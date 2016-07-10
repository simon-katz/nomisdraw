(ns nomisdraw.top-level-render
  (:require [nomisdraw.play.quil-animation-play :as qap]
            [nomisdraw.play.quil-basics :as qb]
            [nomisdraw.play.re-com-slowness-play :as slowness-play]
            [reagent.core :as r]
            [re-com.core :as re]))

(defn ^:private style-for-top-level-div []
  (let [m "20px"]
    {:margin-left  m
     :margin-right m}))

(def ^:private choices [{:id :some-quil-animation-stuff
                         :label "Some Quil Animation Stuff"
                         :fun qap/render}
                        {:id :quil-basics
                         :label "Quil Basics"
                         :fun qb/render}
                        {:id :nested-re-com-can-be-slow
                         :label "Nested re-com can be slow"
                         :fun slowness-play/render}])

(def ^:private selected-demo-id (r/atom (-> choices
                                            first
                                            :id)))

(defn ^:private render-choices []
  [re/v-box
   :width     "700px"
   :gap       "10px"
   :children  [[re/h-box
                :gap      "10px"
                :align    :center
                :children [[re/label :label "Select a demo"]
                           [re/single-dropdown
                            :choices   choices
                            :model     selected-demo-id
                            :width     "300px"
                            :on-change #(reset! selected-demo-id %)]]]
               [re/gap :size "0px"] ;; Force a bit more space here
               (let [fun (->> choices
                              (filter #(= @selected-demo-id
                                          (:id %)))
                              first
                              :fun)]
                 [fun])]])

(defn render []
  [re/v-box
   :style (style-for-top-level-div)
   :children
   [[:h1 "Nomisdraw"]
    [render-choices]]])
