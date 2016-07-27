(ns cosmodrom.core
  (:require [reagent.core :as r :refer [render]]
            [domina.core :refer [by-id]]
            [cljsjs.svgjs]))

(def app (r/atom {:width (.-innerWidth js/window)
                :height (.-innerHeight js/window)
                                        ;                :canvas (js/fabric.Canvas. "display")
}))


(defn flare [state]
  (let [ox (/ (:width @state) 2)
        oy (/ (:height @state) 2)
        flare-width 200
        flare-height 150]
    [:svg
     {:width (:width @state)
      :height (:height @state)}
     [:ellipse
      {:cx ox
       :cy oy
       :rx flare-width
       :ry flare-height
       :style {:fill "purple" :fill-opacity 0.5}
       :transform (str "rotate(45 " ox " " oy") ")}]
     [:ellipse
      {:cx ox
       :cy oy
       :rx flare-width
       :ry flare-height
       :style {:fill "green" :fill-opacity 0.5}
       :transform (str "rotate(135 " ox " " oy ") ")}]
     [:ellipse
      {:cx ox
       :cy oy
       :rx flare-width
       :ry flare-height
       :style {:fill "cyan" :fill-opacity 0.5}
       :transform (str "rotate(-10 " ox " " oy ") ")}]
     [:circle
      {:cx ox                           ;(- ox (/ flare-width 2))
       :cy oy
       :r 150
       :style {:fill "black" :fill-opacity 0.6}}]
     [:g
      {:fill "none"
       :stroke "white"
       :stroke-width 1}
      [:path
       {:stroke-dasharray "5,5"
        :d (str "M" (- ox 100) " " oy "L" (+ ox 100) " " oy)}]]
     [:text
      {:x ox
       :y (+ oy 30)
       :style {:fill "white" :font-family "Verdana" :font-size "30" :text-anchor "middle"}}
      "COSMODROM"]
     [:g
      {:fill "none"
       :stroke "white"
       :stroke-width 1}
      [:path
       {:stroke-dasharray "5,5"
        :d (str "M" (- ox 100) " " (+ oy 40) "L" (+ ox 100) " " (+ oy 40))}]]]))



(defn page [state]
  [:div
   {:class "center"}
   [flare state]])


(defn window-resize-handler [evt]
  (let [height (.-innerHeight js/window)
        width (.-innerWidth js/window)]
    (do
      (swap! app assoc :height height)
      (swap! app assoc :width width))))


(defn ^:export init []
  (if (and js/document
           (aget js/document "getElementById"))
    (do
      (render [page app] (by-id "app"))
      (.addEventListener js/window "resize" window-resize-handler))))


;(.attr (.rect svg 100 100) (clj->js {:fill "#f06"}))
