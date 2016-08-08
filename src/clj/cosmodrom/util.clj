(ns cosmodrom.util
  (:require [pl.danieljanus.tagsoup :as ts]))


(defmacro html-to-hiccup [file]
  (let [hiccup (ts/parse-string (slurp file))]
    `~hiccup))
