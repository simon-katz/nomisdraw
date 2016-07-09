(ns nomisdraw.render
  (:require [nomisdraw.play.re-com-slowness-play :as slowness-play]
            [nomisdraw.play.quil-animation-play :as qap]
            [reagent.core :as r]
            [re-com.core :as re]))

(defn ^:private style-for-top-level-div []
  (let [m "20px"]
    {:margin-left  m
     :margin-right m}))

(def ^:private choices [{:id :nested-re-com-can-be-slow
                         :label "Nested re-com can be slow"
                         :fun slowness-play/make-re-com-stuff}
                        {:id :some-quil-stuff
                         :label "Some Quil Animation Stuff"
                         :fun qap/some-quil-animation-stuff}])

(def ^:private selected-demo-id (r/atom (-> choices
                                            first
                                            :id)))

(defn ^:private the-choices []
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

(defn top-level-render []
  [re/v-box
   :style (style-for-top-level-div)
   :children
   [[:h1 "Nomisdraw"]
    [the-choices]]])
