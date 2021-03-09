(ns net.r4s6.woni.cli
  (:require [clojure.string :as string]
            [clojure.tools.cli :as cli]
            [net.r4s6.woni.database :as db]
            [net.r4s6.woni.parse :as p])
  (:gen-class))

(defn format-record
  [record]
  (string/join ", " (vals record)))

(defn input-file
  [file]
  (case file
    "-" *in*
    file))

(def cli-options
  [["-?" "--help" "Show usage info"]
   ["-s" "--sort SPEC" "Columns to sort by (default is \"-Email,LastName\")"
    :default ["-Email" "LastName"]
    :parse-fn #(p/parse-line %)]])

(defn usage
  [parsed]
  (string/join "\n" ["clojure -M:run [options] [inputs]"
                     ""
                     "Input files will be parsed and merged into a single record set."
                     ""
                     "Options:"
                     (:summary parsed)]))

(defn -main [& args]
  (let [parsed (cli/parse-opts args cli-options)]
    (if (-> parsed :options :help)
      (println (usage parsed))
      (let [records (reduce (fn [rs file]
                              (concat rs (p/parse-file (input-file file))))
                            []
                            (:arguments parsed))
            sorted (db/sort-by-fields (:sort (:options parsed)) records)]
        (doseq [record sorted]
          (println (format-record record)))))))
