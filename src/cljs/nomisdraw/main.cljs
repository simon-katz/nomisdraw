(ns nomisdraw.main
  (:require [nomisdraw.top-level-render :as top-level-render]
            [reagent.dom :as dom]))

(enable-console-print!)

(println "Edits to this text should show up in your developer console.")


(dom/render [top-level-render/render]
            (. js/document (getElementById "app")))

(defn on-js-reload []
  ;; optionally touch your app-state to force rerendering depending on
  ;; your application
  ;; (swap! app-state update-in [:__figwheel_counter] inc)
  )
