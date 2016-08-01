(ns cosmodrom.core
  (:require [reagent.core :as r :refer [render]]
            [domina.core :refer [by-id]]
            [cljsjs.snapsvg]
            [hickory.core :as hick])
  (:require-macros [cosmodrom.embed :refer [embed-svg]]))

(def app (r/atom {:width (.-innerWidth js/window)
                  :height (.-innerHeight js/window)
                  :flares-file "svg/flares.svg"
                  :svg-dom-id "#canvas"}))



;;
;; el is a group node
;;
(defn create-transform-group [el]
  (if (.-node el)
    (let [child-nodes (.selectAll el "*")]
      (-> el
          (.g)
          (.attr "class" "translate")
          (.g)
          (.attr "class" "rotate")
          (.g)
          (.attr "class" "scale")
          (.append child-nodes)))))



(defn get-origin-x [bbox direction]
  (case direction
    :left (.x bbox)
    :center (.cx bbox)
    :right (.x2 bbox)))



(defn get-origin-y [bbox direction]
  (case direction
    :top (.y bbox)
    :center (.cy bbox)
    :bottom (.y2 bbox)))



(defn flare []
  (embed-svg "html/svg/flares.svg"))


(defn page [state]
  [flare])


(defn window-resize-handler [evt]
  (let [height (.-innerHeight js/window)
        width (.-innerWidth js/window)]
    (do
      (swap! app assoc :height height)
      (swap! app assoc :width width))))


(defn load-svg [ctx path]
  (.load js/Snap path (fn [data] (.append ctx data))))


(defn init-svg [state]
  (comment (let [svg (js/Snap (:svg-dom-id @state))]
             (swap! state assoc :svg svg)
             (load-svg svg (:flares-file @state))
    
             (swap! state assoc :flare-calendar (.select svg "#flare-calendar"))
             (swap! state assoc :flare-chat (.select svg "#flare-chat"))
             (swap! state assoc :flare-video (.select svg "#flare-videos")))))


(defn ^:export init []
  (if (and js/document
           (aget js/document "getElementById"))
    (do
      ;; init flares
      (init-svg app)
      ;; render page
      (render [page app] (by-id "app"))
      (.addEventListener js/window "resize" window-resize-handler))))


;(.attr (.rect svg 100 100) (clj->js {:fill "#f06"}))
