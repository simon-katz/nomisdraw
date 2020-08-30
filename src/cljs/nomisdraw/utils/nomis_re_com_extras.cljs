(ns nomisdraw.utils.nomis-re-com-extras
  (:require [reagent.core :as r]
            [re-com.core :as re]))

;;; TODO: Doc.
;;;       Including:
;;;       - The `:fun`s need to be #'-d for interactive development.
;;;         (Hmmm, so don't use anonymous functions. Painful.)

;;; TODO: Use a schema (or a clojure.spec spec) for choices.

(defonce ^:private choices-s-atom
  (atom {}))

(defn ^:private choices-&-uniquifier>selected-id-atom [choices
                                                       dropdown-uniquifier]
  (let [k [choices dropdown-uniquifier]]
    (or (get @choices-s-atom k)
        (let [a (r/atom (-> choices
                            first
                            :id))]
          (swap! choices-s-atom assoc k a)
          a))))

(defn dropdown-and-chosen-item [{:keys [choices
                                        dropdown-uniquifier
                                        outer-style
                                        inner-style]
                                 :or {dropdown-uniquifier ::default}}]
  (let [selected-id& (choices-&-uniquifier>selected-id-atom choices
                                                            dropdown-uniquifier)]
    [re/v-box
     :style outer-style
     :gap       "10px"
     :children  [[re/h-box
                  :gap      "10px"
                  :align    :center
                  :children [[re/label :label "Select a demo"]
                             [re/single-dropdown
                              :choices   choices
                              :model     selected-id&
                              :width     "auto"
                              :on-change #(reset! selected-id& %)]]]
                 (re/box
                  :style inner-style
                  :child
                  (let [fun (->> choices
                                 (filter #(= @selected-id&
                                             (:id %)))
                                 first
                                 :fun)]
                    [fun]))]]))
