(ns nomisdraw.utils.quil-utils
  (:require [quil.core :as q :include-macros true]
            [quil.middleware :as m]
            [reagent.core :as r]
            [re-com.core :as re]))

;; How to make Quil work well with Reagent?
;; - This core idea comes from
;;   http://stackoverflow.com/questions/33345084/quil-sketch-on-a-reagent-canvas
;; - I've made it more functional.

(defn random-lowercase-string [length]
  (let [ascii-codes (range 97 123)]
    (apply str (repeatedly length #(char (rand-nth ascii-codes))))))

(defn random-canvas-id []
  (random-lowercase-string 30))

(defn ^:private boxify [elem]
  ;; Use this to prevent horizontal stretching
  #?(:clj  (throw (Exception. "Shouldn't be here"))
     :cljs [re/h-box
            :size "none" ; seems to be the default, but not documented AFAICS
            :children
            [elem]]))

(defn zzzz-sketch-in-reagent [sketch canvas-id w h]
  #?(:clj  (throw (Exception. "Shouldn't be here"))
     :cljs (let [component [r/create-class
                            {:reagent-render (fn []
                                               (let [element-wotsit (keyword (str "canvas#" canvas-id))]
                                                 [element-wotsit {:width  w
                                                                  :height h}]))
                             :component-did-mount (fn [] sketch)}]]
             (boxify component))))

;; #### This is kinda working on browser refresh, but not on Figwheel reload.

(defmacro sketch-in-reagent [w h & args]
  ;; FIXME:
  ;; I had `q/sketch` here, but it doesn't work on browser refresh.
  ;; - I think it doesn't do the animation -- a background gets drawn.
  ;; It's OK on a Figwheel reload, though.
  ;; Changing to `q/defsketch` makes things work on both browser refresh
  ;; and a Figwheel reload.

  (let [canvas-id-sym (gensym)] ; TODO: Can you use a xxxx# symbol? Or is this another example of symbols being broken?
    `(let [~canvas-id-sym (random-canvas-id)
           sketch#    (q/defsketch ~(symbol (random-lowercase-string 30)) ; fixme-!!!!-see-comment#
                        ~@(concat args
                                  [:middleware ['m/fun-mode]
                                   :host       canvas-id-sym
                                   :size       [w h]]))]
       (zzzz-sketch-in-reagent sketch# ~canvas-id-sym ~w ~h))))
