(ns cosmodrom.core
  (:require [reagent.core :as r :refer [render]]
            [domina.core :refer [by-id]]
            [cljsjs.snapsvg]
            [hickory.core :as hick])
  (:require-macros [cosmodrom.embed :refer [embed-svg]]))

(def app (r/atom {:width (.-innerWidth js/window)
                  :height (.-innerHeight js/window)}))


(defn flare []
  [:div
   [:svg
    {:y "0px", :xml:space "preserve", :width "401.417px",
     :viewbox "0 0 401.417 377", :id "cosmodrom", :x "0px",
     :version "1.1", :enable-background "new 0 0 401.417 377", :height "377px"}
    (embed-svg "resources/flares.svg")]])


(defn page [state]
  [flare])


(defn window-resize-handler [evt]
  (let [height (.-innerHeight js/window)
        width (.-innerWidth js/window)]
    (do
      (swap! app assoc :height height)
      (swap! app assoc :width width))))



(defn flare-chat-click [])

(defn flare-video-click [])

(defn flare-calendar-click [el])


(defn init-svg [state]
  (let [paper (js/Snap "#cosmodrom")
        flare-calendar (js/Snap "#flare-calendar")
        flare-chat (js/Snap "#flare-chat")
        flare-video (js/Snap "#flare-video")]
    
    (.click flare-calendar (fn [] (flare-calendar-click [flare-calendar])))
    (.click flare-chat flare-chat-click)
    (.click flare-video flare-video-click)))



(defn ^:export init []
  (if (and js/document
           (aget js/document "getElementById"))
    (do
      ;; render page
      (render [page app] (by-id "app"))
      ;; init flares
      (init-svg app)
;;      (.addEventListener js/window "resize" window-resize-handler)
      )))
