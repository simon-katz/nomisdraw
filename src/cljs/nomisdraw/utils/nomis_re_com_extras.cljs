(ns nomisdraw.utils.nomis-re-com-extras
  (:require [reagent.core :as r]
            [re-com.core :as re]))

;;; TODO: Doc.
;;;       Including:
;;;       - The `:fun`s need to be #'-d for interactive development.
;;;         (Hmmm, so don't use anonymous functions. Painful.)

;;; TODO: Use a schema (or a clojure.spec spec) for options.

(defonce ^:private options-s-atom
  (atom {}))

(defn ^:private options-&-uniquifier>selected-id-atom [options
                                                       dropdown-uniquifier]
  (let [k [options dropdown-uniquifier]]
    (or (get @options-s-atom k)
        (let [a (r/atom (-> options
                            first
                            :id))]
          (swap! options-s-atom assoc k a)
          a))))

(defn dropdown-and-chosen-item [{:keys [options
                                        dropdown-uniquifier
                                        outer-style
                                        inner-style]
                                 :or {:dropdown-uniquifier ::default}}]
  (let [selected-id& (options-&-uniquifier>selected-id-atom options
                                                            dropdown-uniquifier)]
    [re/v-box
     :style outer-style
     :width     "700px"
     :gap       "10px"
     :children  [[re/h-box
                  :gap      "10px"
                  :align    :center
                  :children [[re/label :label "Select a demo"]
                             [re/single-dropdown
                              :choices   options
                              :model     selected-id&
                              :width     "300px"
                              :on-change #(reset! selected-id& %)]]]
                 (re/box
                  :style inner-style
                  :child
                  (let [fun (->> options
                                 (filter #(= @selected-id&
                                             (:id %)))
                                 first
                                 :fun)]
                    [fun]))]]))
