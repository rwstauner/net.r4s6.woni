(ns net.r4s6.woni.parse
  (:require [clojure.string :as string]))


(def separator-re #"[ ,|]+")

; NOTE: If we wanted to use clojure.data.csv:
; (let [[delim] (seq (or (re-find #"[|,]" line) " "))]
;   (-> line (csv/read-csv :separator delim) first (->> (map string/trim))))

(defn parse-line
  "Parse input string into fields."
  [line]
  (string/split line separator-re))
