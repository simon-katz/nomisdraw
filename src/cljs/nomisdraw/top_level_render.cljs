(ns nomisdraw.top-level-render
  (:require [nomisdraw.play.examples-from-quil-intro :as qi]
            [nomisdraw.play.quil-animation-play :as qap]
            [nomisdraw.play.cartoon-dynamic-face :as cartoon-dynamic-face]
            [nomisdraw.play.cartoon-expressions :as cartoon-expressions]
            [nomisdraw.play.re-com-slowness-play :as slowness-play]
            [nomisdraw.utils.nomis-re-com-extras :as rex]
            [reagent.core :as r]
            [re-com.core :as re]))

(defn ^:private style-for-top-level-div []
  (let [m "20px"]
    {:margin-left  m
     :margin-right m}))

(defn render []
  [re/v-box
   :style (style-for-top-level-div)
   :children
   [[:h1 "Nomisdraw"]
    [rex/dropdown-and-chosen-item
     {:outer-style {:border-style "solid"
                    :border-width "4px"
                    :border-color "grey"
                    :padding      "8px"}
      :inner-style {:border-style "solid"
                    :border-width "1px"
                    :border-color "grey"
                    :padding      "8px"}
      :options     [{:id :quil-basics
                     :label "Examples from Quil Intro"
                     :fun #'qi/render}
                    {:id :some-quil-animation-stuff
                     :label "Some Quil Animation Stuff"
                     :fun #'qap/render}
                    {:id :nested-re-com-can-be-slow
                     :label "Nested re-com can be slow"
                     :fun #'slowness-play/render}
                    {:id :cartoon-dynamic-face
                     :label "Cartoon Dynamic Face"
                     :fun #'cartoon-dynamic-face/render}
                    {:id :cartoon-expressions
                     :label "Cartoon Expressions"
                     :fun #'cartoon-expressions/render}]}]]])
