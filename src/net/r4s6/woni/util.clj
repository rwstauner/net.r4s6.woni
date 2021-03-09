(ns net.r4s6.woni.util)

(defn transform-fields
  "Takes a map of {field fn} and updates the fields in the record using the provided functions.

  (transform-fields {\"Name\" clojure.string/upper-case} {\"Name\" \"bob\"})
  => {\"Name\" \"BOB\")"
  [fns record]
  (let [field-set (set (keys record))]
    (reduce (fn [r [field fxn]]
              (update r field fxn))
            record
            (filter #(field-set (first %)) fns))))
