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
  [:div
   (embed-svg "html/svg/flares.svg")])


(defn page [state]
  [flare])


(defn window-resize-handler [evt]
  (let [height (.-innerHeight js/window)
        width (.-innerWidth js/window)]
    (do
      (swap! app assoc :height height)
      (swap! app assoc :width width))))


(defn flare-hover-in [effect]
  (let [child (-> effect (.-node) (.-firstChild))
        start (-> child (.-attributes) (aget 0) (.-value) js->clj)]
    (.animate js/Snap start 5 (fn [v] (-> child
                                      (.-attributes)
                                      (aget 0)
                                      (.-value)
                                      (set! v)))
              300)))

(defn flare-hover-out [effect]
  (let [child (-> effect (.-node) (.-firstChild))
        start (-> child (.-attributes) (aget 0) (.-value) js->clj)]
    (.animate js/Snap start 0 (fn [v] (-> child
                                      (.-attributes)
                                      (aget 0)
                                      (.-value)
                                      (set! v)))
              300)))

(defn flare-chat-click []
  (js/alert "chat"))

(defn flare-video-click []
  (js/alert "video"))

(defn flare-calendar-click [el])

(defn set-flare-hover [el effect]
  (.attr el #js {:filter effect})
  (.hover el #(flare-hover-in effect) #(flare-hover-out effect)))

(defn create-blur-effect [paper]
  (.filter paper (-> (.-filter js/Snap) (.blur 0))))

(defn init-svg [state]
  (let [paper (js/Snap "#cosmodrom")
        flare-calendar (js/Snap "#flare-calendar")
        flare-chat (js/Snap "#flare-chat")
        flare-video (js/Snap "#flare-videos")]
    
    (.click flare-calendar (fn [] (flare-calendar-click [flare-calendar])))
    (.click flare-chat flare-chat-click)
    (.click flare-video flare-video-click)

    (set-flare-hover flare-calendar (create-blur-effect paper))
    (set-flare-hover flare-chat (create-blur-effect paper))
    (set-flare-hover flare-video (create-blur-effect paper))))



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


;(.attr (.rect svg 100 100) (clj->js {:fill "#f06"}))
