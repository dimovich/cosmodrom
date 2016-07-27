(ns cosmodrom.fabric)

(comment (ns cosmodrom.fabric
           (:require [cljsjs.fabric]))

         (defn init-canvas []
           (let [canvas (:canvas @app)]
             (.setHeight canvas (- (:height @app) 20))
             (.setWidth canvas (- (:width @app) 20))
             (.renderAll canvas)
             (.calcOffset canvas)))


         (defn window-resize-handler [evt]
           (let [height (.-innerHeight js/window)
                 width (.-innerWidth js/window)]
             (do
               (swap! app assoc :height height)
               (swap! app assoc :width width)
               (init-canvas))))




         (comment
           (.addEventListener js/window "resize" window-resize-handler)
           (.addEventListener  (by-id "display") :click (fn [_] (.renderAll canvas)))
           (init-canvas))
         )
