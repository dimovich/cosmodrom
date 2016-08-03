(comment
  (defn hover-effect [effect, endv, time]
    (let [child (-> effect (.-node) (.-firstChild))
          start (-> child (.-attributes) (aget 0) (.-value) js->clj)]
      (.animate js/Snap start endv
                (fn [v] (-> child
                            (.-attributes)
                            (aget 0)
                            (.-value)
                            (set! v)))
                time)))

  (defn set-flare-hover [el effect]
    (.attr el #js {:filter effect})
    (.hover el #(hover-effect effect 5 300) #(hover-effect effect 0 300)))

  (defn create-blur-effect [paper]
    (.filter paper (-> (.-filter js/Snap) (.blur 0))))


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


  )

