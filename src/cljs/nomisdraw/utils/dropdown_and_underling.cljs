(ns nomisdraw.utils.dropdown-and-underling
  (:require [reagent.core :as r]
            [re-com.core :as re]))

;;; TODO: Currently trying to make this work nicely with interactive
;;;       development.
;;;       Pros and cons:
;;;       - Pros
;;;         - More modular
;;;       - Cons
;;;         - The def of the choices:
;;;           - must #' the `:fun` values

;;; TODO: Doc.
;;;       Including:
;;;       - Should be "options", not "choices", but I'm using re terminology.
;;;       - The `:fun`s need to be #'-d.

;;; TODO: Use a schema (or a clojure.spec spec) for choices.

(defonce ^:private choices-s-atom
  (atom {}))

(defn ^:private choices-&-uniquifier>selected-id-atom [choices uniquifier]
  (let [k [choices uniquifier]]
   (or (get @choices-s-atom
            k)
       (let [a (r/atom (-> choices
                           first
                           :id))]
         (swap! choices-s-atom
                assoc
                k
                a)
         a))))

(defn dropdown-and-chosen-item
  ([choices]
   (dropdown-and-chosen-item choices ::default))
  ([choices uniquifier]
   (let [selected-id-atom (choices-&-uniquifier>selected-id-atom choices
                                                                 uniquifier)]
     [re/v-box
      :width     "700px"
      :gap       "10px"
      :children  [[re/h-box
                   :gap      "10px"
                   :align    :center
                   :children [[re/label :label "Select a demo"]
                              [re/single-dropdown
                               :choices   choices
                               :model     selected-id-atom
                               :width     "300px"
                               :on-change #(reset! selected-id-atom %)]]]
                  (let [fun (->> choices
                                 (filter #(= @selected-id-atom
                                             (:id %)))
                                 first
                                 :fun)]
                    [fun])]])))
