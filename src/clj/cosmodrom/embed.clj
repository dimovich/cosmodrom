(ns cosmodrom.embed
  (:require [pl.danieljanus.tagsoup :as ts]))


(defmacro embed-svg [svg-file]
  (let [hiccup (ts/parse-string (slurp svg-file))]
    `~hiccup))
