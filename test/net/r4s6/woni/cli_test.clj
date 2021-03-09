(ns net.r4s6.woni.cli-test
  (:require [clojure.java.io :as io]
            [clojure.string :as string]
            [clojure.test :refer [deftest testing is]]
            [net.r4s6.woni.cli :as cli]))

(defn data-file
  [path]
  (str (io/resource (str "data/" path))))

(defn main-out
  [& args]
  (with-out-str
    (apply cli/-main args)))

(def main-out-lines (comp string/split-lines main-out))

(deftest main
  (testing "usage"
    (let [out (main-out "--help")]
      (is (re-find #"Input files" out))
      (is (re-find #"Options:\n\s+-" out))
      (is (re-find #"--sort SPEC" out))))
  (testing "sort"
    (let [lines (main-out-lines "--sort"
                                "-Email,LastName"
                                (data-file "visitors.psv")
                                (data-file "enlisted.ssv"))]
      (is (= ["Flagg, Samuel, wind@4077.mash, DarkSlateGray3, 04/15/1918"
              "Klinger, Maxwell, section8@4077.mash, SlateBlue4, 02/26/1920"
              "O'Reilly, Walter, radar@4077.mash, WhiteSmoke, 04/15/1926"
              "Straminsky, Igor, igor@4077.mash, DodgerBlue4, 01/18/1924"
              "Barker, Wilson, generals@4077.mash, BlanchedAlmond, 02/06/1905"
              "Clayton, Crandell, generals@4077.mash, PaleGreen3, 07/03/1900"
              "Mitchell, Maynard, generals@4077.mash, SlateBlue1, 06/27/1892"]
             lines)))
    (let [lines (main-out-lines "--sort"
                                "-LastName"
                                (data-file "enlisted.ssv"))]
      (is (= ["Straminsky, Igor, igor@4077.mash, DodgerBlue4, 01/18/1924"
              "O'Reilly, Walter, radar@4077.mash, WhiteSmoke, 04/15/1926"
              "Klinger, Maxwell, section8@4077.mash, SlateBlue4, 02/26/1920"]
             lines)))
    (let [lines (main-out-lines "--sort"
                                "unknown,fields,-ignored"
                                (data-file "enlisted.ssv"))]
      (is (= ["O'Reilly, Walter, radar@4077.mash, WhiteSmoke, 04/15/1926"
              "Klinger, Maxwell, section8@4077.mash, SlateBlue4, 02/26/1920"
              "Straminsky, Igor, igor@4077.mash, DodgerBlue4, 01/18/1924"]
             lines))))
  (testing "stdin"
    (let [input "LastName FirstName\nl f\na b\n"
          lines (binding [*in* (io/reader (.getBytes input))]
                  (main-out-lines "--sort" "FirstName" "-"))]
      (is (= ["a, b"
              "l, f"]
             lines)))))
