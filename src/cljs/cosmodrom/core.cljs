(ns cosmodrom.core
  (:require [reagent.core :as r :refer [render]]
            [domina.core :refer [by-id]]
            [cljsjs.snapsvg]
            [clojure.string :as s]
            [cljs.core.async :refer [<! timeout]])
  (:require-macros [cosmodrom.util :refer [html-to-hiccup]]
                   [cljs.core.async.macros :refer [go-loop]]))

(def app (r/atom {:width (.-innerWidth js/window)
                  :height (.-innerHeight js/window)}))






(defn center-flare [svg]
  (let [width (:width @app)
        height (:height @app)
        flare-width (js/parseInt (.-width (.attr svg)))
        flare-height (js/parseInt (.-height (.attr svg)))
        x0 (/ (- width flare-width) 2 )
        y0 (/ (- height flare-height) 2 )]

    (.attr svg (clj->js {:x x0
                           :y y0}))))

(defn scale [svg percent]
  (let [width (/ (* percent (js/parseInt (.-width (.attr svg)))) 100)
        height (/ (* percent (js/parseInt (.-height (.attr svg)))) 100)]
   (.attr svg (clj->js {:width width :height height}))))



(defn scale-animation [svg percent time]
  (let [steps 5
        attr (.attr svg)
        width (-> attr (.-width) js/parseInt)
        height (-> attr  (.-height) js/parseInt)
        end-width (-> width (* percent) (/ 100))
        end-height (-> height (* percent) (/ 100))
        x0 (/ (- (:width @app) end-width) 2 )
        y0 (/ (- (:height @app) end-height) 2 )]
    
    (.animate svg (clj->js {:width end-width
                            :height end-height
                            :x x0 :y y0})
              time
              js/mina.backout)))


(defn opacity-animation [svg value time]
  (.animate svg
            #js {:opacity value}
            time))


(defn flare-chat-click [el]
  (scale-animation (:svg @app) 50 1000)
  (opacity-animation el 0 1000))

(defn flare-cosmolab-click [el]
  (scale-animation (:svg @app) 50 1000)
  (opacity-animation el 0 1000))

(defn flare-calendar-click [el]
  (scale-animation (:svg @app) 50 1000)
  (opacity-animation el 0 1000))


(defn init-svg [state]
  (let [width (:width @state)
        height (:height @state)
        svg (js/Snap "#flare")
        flare-calendar (js/Snap "#flare-calendar")
        flare-chat (js/Snap "#flare-chat")
        flare-cosmolab (js/Snap "#flare-cosmolab")]


    (swap! state assoc :svg svg)

    (center-flare svg)
    
    (.click flare-calendar #(flare-calendar-click flare-calendar))
    (.click flare-chat #(flare-chat-click flare-chat))
    (.click flare-cosmolab #(flare-cosmolab-click flare-cosmolab))))


(defn window-resize-handler [evt]
  (let [height (.-innerHeight js/window)
        width (.-innerWidth js/window)
        svg (:svg @app)]
    (do
      (swap! app assoc :height height)
      (swap! app assoc :width width)
      (center-flare svg))))



(defn flare []
  (html-to-hiccup "resources/flare.svg"))


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
