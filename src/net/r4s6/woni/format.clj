(ns net.r4s6.woni.format
  (:require [net.r4s6.woni.util :as util]))

(defn format-date
  "Takes a java.time.LocalDate and formats it as M/D/YYYY.
  Returns a string for any input that is not a date."
  [d]
  (try
    (format "%d/%d/%4d"
            (.getMonthValue d)
            (.getDayOfMonth d)
            (.getYear d))
    (catch Throwable _
      (str d))))

(def formatters {"DateOfBirth" format-date})

(def format-fields (partial util/transform-fields formatters))
