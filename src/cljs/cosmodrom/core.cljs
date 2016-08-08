(ns cosmodrom.core
  (:require [reagent.core :as r :refer [render]]
            [domina.core :refer [by-id]]
            [cljsjs.snapsvg]
            [clojure.string :as s])
  (:require-macros [cosmodrom.util :refer [html-to-hiccup]]))

(def app (r/atom {:width (.-innerWidth js/window)
                  :height (.-innerHeight js/window)}))







(defn flare-chat-click [])

(defn flare-video-click [])

(defn flare-calendar-click [el])



(defn center-flare [state]
  (let [paper (:svg @state)
        width (:width @state)
        height (:height @state)
        flare-width (js/parseInt (.-width (.attr paper)))
        flare-height (js/parseInt (.-height (.attr paper)))
        x0 (/ (- width flare-width) 2 )
        y0 (/ (- height flare-height) 2 )]

    (.attr paper (clj->js {:x x0
                           :y y0}))))

(defn init-svg [state]
  (let [width (:width @state)
        height (:height @state)
        paper (js/Snap "#flare")
        flare-calendar (js/Snap "#flare-calendar")
        flare-chat (js/Snap "#flare-chat")
        flare-video (js/Snap "#flare-video")]


    (swap! state assoc :svg paper)

    (center-flare state)
    
    (.click flare-calendar (fn [] (flare-calendar-click [flare-calendar])))
    (.click flare-chat flare-chat-click)
    (.click flare-video flare-video-click)))


(defn window-resize-handler [evt]
  (let [height (.-innerHeight js/window)
        width (.-innerWidth js/window)
        paper (:svg @app)]
    (do
      (swap! app assoc :height height)
      (swap! app assoc :width width)
      (center-flare app))))



(defn flare []
  (html-to-hiccup "resources/flares.svg"))


(defn page [state])



(defn ^:export init []
  (if (and js/document
           (aget js/document "getElementById"))
    (do
      ;; render page
;      (render [page app] (by-id "cosmodrom-app"))
      ;; init flares
      (init-svg app)
      (.addEventListener js/window "resize" window-resize-handler))))
