(ns cosmodrom.templates.index
  (:require [net.cgrand.enlive-html :refer [deftemplate
                                            html-content]]))


(deftemplate index "index.html"
  []
  [:div#cosmodrom-app] (html-content (slurp "resources/flare.svg")))
