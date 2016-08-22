(ns cosmodrom.svg)


(defn get-width [el]
  (-> el (.attr) (.-width) js/parseFloat))

(defn get-height [el]
  (-> el (.attr) (.-height) js/parseFloat))


(defn center-svg-x [svg width]
  (let [flare-width (get-width svg)
        x0 (/ (- width flare-width) 2 )]
    (.attr svg (clj->js {:x x0}))))

(defn center-svg [svg width height]
  (let [flare-width (get-width svg)
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
  (let [attr (.attr svg)
        width (get-width svg)
        height (get-height svg)
        end-width (-> width (* percent) (/ 100))
        end-height (-> height (* percent) (/ 100))
        x0 (-> attr (.-x) js/parseFloat)
        y0 (-> attr (.-y) js/parseFloat)
        x (+ x0 (/ (- width end-width) 2))
        y (+ y0 (/ (- height end-height) 2))]
    
    (.animate svg (clj->js {:width end-width
                            :height end-height
                            :x x :y y})
              time
              (aget (aget js/window "mina") "backout")
              callback)))


(defn animate-opacity [svg value time]
  (.animate svg
            #js {:opacity value}
            time))


(defn unhide [el time]
  ;;  (.removeClass el "hidden")
  (animate-opacity el 1 time))

(defn hide [el time]
  ;;  (.addClass el "hidden")
  (animate-opacity el 0 time))
