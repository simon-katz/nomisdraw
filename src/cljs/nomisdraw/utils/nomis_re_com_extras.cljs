(ns nomisdraw.utils.nomis-re-com-extras
  (:require [reagent.core :as r]
            [re-com.core :as re]))

;;;; TODO Make a library that does this (this is the same as
;;;;      `com.nomistech.music-theory.library-candidates.nomis-re-com-extras`).

(defonce ^:private choices-s-atom
  (atom {}))

(defn ^:private choices-&-uniquifier>selected-id-atom [dropdown-choices
                                                       dropdown-uniquifier]
  (or (get @choices-s-atom dropdown-uniquifier)
      (let [a (r/atom (-> dropdown-choices
                          first
                          :id))]
        (swap! choices-s-atom assoc dropdown-uniquifier a)
        a)))

(defn ^:private apply-component [f opts]
  (vec (cons f (apply concat opts))))

(defn dropdown-and-chosen-item [{:keys [label-options
                                        dropdown-choices
                                        dropdown-uniquifier
                                        dropdown-options
                                        outer-style
                                        outer-options
                                        inner-style]}]
  (assert (not (nil? dropdown-uniquifier)))
  (let [selected-id& (choices-&-uniquifier>selected-id-atom dropdown-choices
                                                            dropdown-uniquifier)]
    (apply-component
     re/v-box
     (merge
      {:style    outer-style
       :children [[re/h-box
                   :gap      "10px"
                   :align    :center
                   :children [(apply-component re/label label-options)
                              (apply-component
                               re/single-dropdown
                               (merge {:choices   dropdown-choices
                                       :model     selected-id&
                                       :on-change #(reset! selected-id& %)}
                                      dropdown-options))]]
                  (re/box
                   :style inner-style
                   :child (let [fun (->> dropdown-choices
                                         (filter #(= @selected-id&
                                                     (:id %)))
                                         first
                                         :fun)]
                            [fun]))]}
      outer-options))))
