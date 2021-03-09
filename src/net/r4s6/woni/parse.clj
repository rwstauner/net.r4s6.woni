(ns net.r4s6.woni.parse
  (:require [clojure.string :as string]))

(def default-columns
  ["LastName"
   "FirstName"
   "Email"
   "FavoriteColor"
   "DateOfBirth"])

(def separator-re #"[ ,|]+")

(defn make-record
  "Returns a map representing a record.
  Returns nil if the record isn't valid."
  [columns values]
  (when (= (count columns) (count values))
    (zipmap columns values)))

; NOTE: If we wanted to use clojure.data.csv:
; (let [[delim] (seq (or (re-find #"[|,]" line) " "))]
;   (-> line (csv/read-csv :separator delim) first (->> (map string/trim))))


(defn parse-line
  "Parse input string into fields."
  [line]
  (string/split line separator-re))

(defn header?
  "Determine if list appears to be column headings."
  [xs]
  (some (set default-columns) xs))

(defn parse-records
  "Parse list of strings into list of records."
  [lines]
  (let [head (some-> lines
                     first
                     parse-line)
        [columns lines] (if (and head (header? head))
                          [head (rest lines)]
                          [default-columns lines])]
    (->> lines
         (map #(make-record columns (parse-line %)))
         (filter some?))))

(defn parse-file
  "Parse file into list of records."
  [file]
  (-> file
      slurp
      string/split-lines
      parse-records))
