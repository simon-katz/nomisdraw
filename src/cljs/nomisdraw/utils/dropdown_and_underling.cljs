(ns nomisdraw.utils.dropdown-and-underling
  (:require [re-com.core :as re]))

;;; TODO: Use a schema (or a clojure.spec spec) for choices.

(defn render-choices [choices selected-demo-id-atom]
  [re/v-box
   :width     "700px"
   :gap       "10px"
   :children  [[re/h-box
                :gap      "10px"
                :align    :center
                :children [[re/label :label "Select a demo"]
                           [re/single-dropdown
                            :choices   choices
                            :model     selected-demo-id-atom
                            :width     "300px"
                            :on-change #(reset! selected-demo-id-atom %)]]]
               (let [fun (->> choices
                              (filter #(= @selected-demo-id-atom
                                          (:id %)))
                              first
                              :fun)]
                 [fun])]])
