(ns cosmodrom.core
  (:require [reagent.core :as r :refer [render]]
;;            [domina.core :refer [by-id]]
            [cljsjs.snapsvg]
            [clojure.string :as s]
            [cosmodrom.svg :as svg :refer [animate-opacity
                                           animate-scale
                                           get-width
                                           get-height
                                           unhide
                                           hide
                                           center-svg
                                           center-svg-x]])
  (:require-macros [cosmodrom.util :refer [html-to-hiccup]]))

(def app (atom {:width (.-innerWidth js/window)
                :height (.-innerHeight js/window)
                :opacity-time 400
                :scale-time 550
                :flare-scale 43}))


;;(enable-console-print!)


(defn animate-flare-click [el callback]
  (let [svg (js/Snap "#flare")]
    (animate-scale svg (:flare-scale @app) (:scale-time @app) callback)
    (animate-opacity el 0 (:opacity-time @app))))

(defn animate-flare-unclick [el callback]
  (let [svg (js/Snap "#flare")]
    (animate-scale svg (/ 10000 (:flare-scale @app)) (:scale-time @app) callback)
    (animate-opacity el 1 (:opacity-time @app))))


(defn center-me [f]
  (swap! app update-in [:center-me] #(identity [f]))
  (f (:width @app) (:height @app)))

(defn reset-center-me []
  (swap! app dissoc :center-me))


(defn start-clicking [state el]
  (swap! state assoc :clicking el))


(defn end-clicking [state el]
  (swap! state dissoc :clicking)
  (if (:clicked @state)
    (swap! state dissoc :clicked)
    (swap! state assoc :clicked el)))


(defn flare-click [el back-el back-scale-fn]
  (when-not (:clicking @app)
    (if-not (:clicked @app)
      (do
        (start-clicking app el)
        (swap! app assoc :clicked-back-el back-el)
        (animate-flare-click el (fn []
                                  (center-me back-scale-fn)
                                  (unhide back-el (:opacity-time @app))
                                  (end-clicking app el))))
      (do
        (let [el (:clicked @app)]
          (start-clicking app el)
          (hide (:clicked-back-el @app) (:opacity-time @app))
          (animate-flare-unclick el (fn []
                                      (reset-center-me)
                                      (end-clicking app el))))))))



(defn init-svg [state]
  (let [width (:width @state)
        height (:height @state)
        svg (js/Snap "#flare")
        flare-calendar (js/Snap "#flare-calendar")
        flare-chat (js/Snap "#flare-chat")
        flare-cosmolab (js/Snap "#flare-cosmolab")
        logo (js/Snap "#logo")
        cosmolab (js/Snap "#cosmolab")
        calendar (js/Snap "#calendar")
        chat (js/Snap "#chat")]

    (center-svg svg width height)

    ;; precompute constants for background dimensions
    (let [factor (/ 100 (:flare-scale @state))
          scaled-flare-width (/ (get-width svg) factor)
          scaled-flare-height (/ (get-height svg) factor)
          bbox-chat (.getBBox flare-chat)
          bbox-calendar (.getBBox flare-calendar)
          bbox-cosmolab (.getBBox flare-cosmolab)
          scaled-x2-chat (-> bbox-chat (.-width) js/parseFloat (/ factor))
          scaled-x-chat (-> bbox-chat (.-x) js/parseFloat (/ factor))
          scaled-y-cosmolab (-> bbox-cosmolab (.-y) js/parseFloat (/ factor))]


      ;;calendar
      (.click flare-calendar
              #(flare-click
                flare-calendar
                calendar
                (fn [w _]
                  (.attr calendar
                         (clj->js {:width (+ 26
                                             (/ (- w
                                                   scaled-flare-width)
                                                2)
                                             scaled-x2-chat)})))))



      ;;cosmolab
      (.click flare-cosmolab
              #(flare-click
                flare-cosmolab
                cosmolab
                (fn [w _]
                  (.attr cosmolab
                         (clj->js {:x (-
                                       (+ (/ (- w
                                                scaled-flare-width)
                                             2)
                                          scaled-x-chat)
                                       2)})))))
      

      ;;chat
      (.click flare-chat
              #(flare-click
                flare-chat
                chat
                (fn [w h]
                  (center-svg-x chat w)
                  (.attr chat
                         (clj->js {:y (+ (/ (- h scaled-flare-height) 2)
                                         scaled-y-cosmolab)}))))))

        
    
        

    (animate-opacity flare-calendar 1 (:opacity-time @app))
    (animate-opacity flare-chat 1 (:opacity-time @app))
    (animate-opacity flare-cosmolab 1 (:opacity-time @app))
    (animate-opacity logo 1 (:opacity-time @app))))


(defn window-resize-handler [evt]
  (let [height (.-innerHeight js/window)
        width (.-innerWidth js/window)
        svg (js/Snap "#flare")]
    (do
      (swap! app assoc :height height)
      (swap! app assoc :width width)
      (center-svg svg width height)
      
      ;;run centering functions
      (doall (map #(% width height) (:center-me @app))))))



(comment (defn flare []
    (html-to-hiccup "resources/flare.svg")))


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


  
