(ns cosmodrom.core
  (:require [reagent.core :as r :refer [render]]
            [domina.core :refer [by-id]]
            [cljsjs.svgjs]))

(def app (atom {:width (.-innerWidth js/window)
                :height (.-innerHeight js/window)
;                :canvas (js/fabric.Canvas. "display")
                }))


(defn page [state]
  )

(defn ^:export init []
  (if (and js/document
           (aget js/document "getElementById"))
    (render [page app] (by-id "app"))))


;(.attr (.rect svg 100 100) (clj->js {:fill "#f06"}))
