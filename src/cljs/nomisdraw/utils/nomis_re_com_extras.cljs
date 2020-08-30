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

(defn ^:private choices-&-uniquifier>selected-id-atom [dropdown-choices
                                                       dropdown-uniquifier]
  (let [k [dropdown-choices dropdown-uniquifier]]
    (or (get @choices-s-atom k)
        (let [a (r/atom (-> dropdown-choices
                            first
                            :id))]
          (swap! choices-s-atom assoc k a)
          a))))

(defn ^:private apply-component [f opts]
  (vec (cons f (apply concat opts))))

(defn dropdown-and-chosen-item [{:keys [label-options
                                        dropdown-choices
                                        dropdown-uniquifier
                                        dropdown-options
                                        outer-style
                                        outer-options
                                        inner-style]
                                 :or {dropdown-uniquifier ::default}}]
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
