(ns cosmodrom.core
  (:require [reagent.core :as r :refer [render]]
            [domina.core :refer [by-id]]
            [cljsjs.snapsvg]
            [clojure.string :as s]
            [cljs.core.async :refer [<! timeout]])
  (:require-macros [cosmodrom.util :refer [html-to-hiccup]]
                   [cljs.core.async.macros :refer [go-loop]]))

(def app (atom {:width (.-innerWidth js/window)
                :height (.-innerHeight js/window)
                :opacity-time 600
                :scale-time 600
                :flare-scale 50}))


(defn get-width [el]
  (-> el (.attr) (.-width) js/parseInt))

(defn get-height [el]
  (-> el (.attr) (.-height) js/parseInt))


(defn center-flare-x [svg]
  (let [width (:width @app)
        flare-width (get-width svg)
        x0 (/ (- width flare-width) 2 )]
    (.attr svg (clj->js {:x x0}))))

(defn center-flare [svg]
  (let [width (:width @app)
        height (:height @app)
        flare-width (get-width svg)
        flare-height (get-height svg)
        x0 (/ (- width flare-width) 2 )
        y0 (/ (- height flare-height) 2 )]

    (.attr svg (clj->js {:x x0
                         :y y0}))))


(defn scale [svg percent]
  (let [width (/ (* percent (get-width svg)) 100)
        height (/ (* percent (get-height svg)) 100)]
   (.attr svg (clj->js {:width width :height height}))))


(defn animate-scale [svg percent time callback]
  (let [steps 5
        attr (.attr svg)
        width (get-width svg)
        height (get-height svg)
        end-width (-> width (* percent) (/ 100))
        end-height (-> height (* percent) (/ 100))
        x0 (/ (- (:width @app) end-width) 2 )
        y0 (/ (- (:height @app) end-height) 2 )]
    
    (.animate svg (clj->js {:width end-width
                            :height end-height
                            :x x0 :y y0})
              time
              js/mina.backout
              callback)))


(defn animate-opacity [svg value time]
  (.animate svg
            #js {:opacity value}
            time))


(defn animate-flare-click [el callback]
  (let [svg (js/Snap "#flare")]
    (animate-scale svg (:flare-scale @app) (:scale-time @app) callback)
    (animate-opacity el 0 (:opacity-time @app))))

(defn animate-flare-unclick [el]
  (let [svg (js/Snap "#flare")]
    (animate-scale svg (/ 10000 (:flare-scale @app)) (:scale-time @app) nil)
    (animate-opacity el 1 (:opacity-time @app))))


(defn unhide [el]
  (.removeClass el "hidden"))

(defn hide [el]
  (.addClass el "hidden"))


(defn setup-calendar-svg []
  (let [el (js/Snap "#calendar")]
    (unhide el)
    (.attr el
           (clj->js {:width (+ 10 (-> (js/Snap "#flare")
                                      (.attr)
                                      (.-x)
                                      js/parseInt)
                               (/ (-> (js/Snap "#flare-chat")
                                      (.getBBox)
                                      (.-x2)
                                      js/parseInt) 2))}))))



(defn setup-cosmolab-svg []
  (let [el (js/Snap "#cosmolab")]
    (unhide el)
    (.attr el
           (clj->js {:x (- (+ (-> (js/Snap "#flare")
                                (.attr)
                                (.-x)
                                js/parseInt)
                            (-> (js/Snap "#flare-chat")
                                (.getBBox)
                                (.-x)
                                js/parseInt)) 24)}))))


(defn setup-chat-svg []
  (let [el (js/Snap "#chat")]
    (center-flare-x el)
    (unhide el)
    (.attr el (clj->js {:y (+ (-> (js/Snap "#flare")
                                  (.attr)
                                  (.-y)
                                  js/parseInt)
                              (/ (-> (js/Snap "#flare-cosmolab")
                                     (.getBBox)
                                     (.-y)
                                     js/parseInt) 2))}))))



(defn flare-click [el callback]
  (if-not (:clicked-flare @app)
    (do
      (swap! app assoc :clicked-flare el)
      (animate-flare-click el callback))
    (do
      (let [el (:clicked-flare @app)
            back-id (-> el (.attr) (.-id) (s/split #"-") second)
            back-el (js/Snap (str "#" back-id))]
        (animate-flare-unclick el)
        ;;hide background-flare
        (hide (js/Snap (str "#" back-id))))
      (swap! app dissoc :clicked-flare))))


(defn center-me [f]
  (swap! app update-in [:center-me] #(identity [f])))

(defn flare-cosmolab-click [el]
  (flare-click el setup-cosmolab-svg)
  (center-me setup-cosmolab-svg))


(defn flare-calendar-click [el]
  (flare-click el setup-calendar-svg)
  (center-me setup-calendar-svg))


(defn flare-chat-click [el]
  (flare-click el setup-chat-svg)
  (center-me setup-chat-svg))


(defn init-svg [state]
  (let [width (:width @state)
        height (:height @state)
        svg (js/Snap "#flare")
        flare-calendar (js/Snap "#flare-calendar")
        flare-chat (js/Snap "#flare-chat")
        flare-cosmolab (js/Snap "#flare-cosmolab")]

    (center-flare svg)
    
    (.click flare-calendar #(flare-calendar-click flare-calendar))
    (.click flare-chat #(flare-chat-click flare-chat))
    (.click flare-cosmolab #(flare-cosmolab-click flare-cosmolab))))


(defn window-resize-handler [evt]
  (let [height (.-innerHeight js/window)
        width (.-innerWidth js/window)
        svg (js/Snap "#flare")]
    (do
      (swap! app assoc :height height)
      (swap! app assoc :width width)
      (center-flare svg)
      ;;run centering functions
      (doall (map #(%) (:center-me @app))))))



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


  
