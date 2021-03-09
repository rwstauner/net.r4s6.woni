(ns net.r4s6.woni.database
  (:require [clojure.string :as string]))

(defn sort-by-fields
  "Sort records by list of fields.
  Field specs are column names optionally prefixed with a \"-\" to indicate descending order.

  (sort-by-fields [\"-Email\", \"LastName\"] records)"
  [specs records]
  (let [dir-field (map #(if (string/starts-with? % "-")
                          [-1 (subs % 1)]
                          [1 %])
                       specs)
        dirs (map first dir-field)
        fields (map second dir-field)
        srt (fn [record]
              (map (partial get record) fields))
        cmp (fn [as bs]
              (or (first (drop-while zero?
                                     (map (fn [a b d]
                                            (* d (compare a b)))
                                          as bs dirs)))
                  0))]
    (sort-by srt cmp records)))
