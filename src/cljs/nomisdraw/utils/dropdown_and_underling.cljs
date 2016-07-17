(ns nomisdraw.utils.dropdown-and-underling
  (:require [reagent.core :as r]
            [re-com.core :as re]))

;;; TODO: Currently trying to make this work nicely with interactive
;;;       development.
;;;       Pros and cons:
;;;       - Pros
;;;         - More modular
;;;       - Cons
;;;         - The def of the options:
;;;           - must #' the `:fun` values

;;; TODO: Doc.
;;;       Including:
;;;       - The `:fun`s need to be #'-d for interactive development.

;;; TODO: Use a schema (or a clojure.spec spec) for options.

(defonce ^:private options-s-atom
  (atom {}))

(defn ^:private options-&-uniquifier>selected-id-atom [options uniquifier]
  (let [k [options uniquifier]]
   (or (get @options-s-atom
            k)
       (let [a (r/atom (-> options
                           first
                           :id))]
         (swap! options-s-atom
                assoc
                k
                a)
         a))))

(defn dropdown-and-chosen-item
  ([options]
   (dropdown-and-chosen-item options ::default))
  ([options uniquifier]
   (let [selected-id-atom (options-&-uniquifier>selected-id-atom options
                                                                 uniquifier)]
     [re/v-box
      :width     "700px"
      :gap       "10px"
      :children  [[re/h-box
                   :gap      "10px"
                   :align    :center
                   :children [[re/label :label "Select a demo"]
                              [re/single-dropdown
                               :choices   options
                               :model     selected-id-atom
                               :width     "300px"
                               :on-change #(reset! selected-id-atom %)]]]
                  (let [fun (->> options
                                 (filter #(= @selected-id-atom
                                             (:id %)))
                                 first
                                 :fun)]
                    [fun])]])))
