(ns net.r4s6.woni.database-test
  (:require [clojure.test :refer [deftest testing is]]
            [net.r4s6.woni.database :as db]))

(deftest sort-by-fields
  (testing "single field"
    (is (= [{"L" "a"} {"L" "b"}]
           (db/sort-by-fields ["L"] [{"L" "b"}
                                     {"L" "a"}]))))
  (testing "multi field"
    (is (= [{"L" "c" "F" "b"} {"L" "c" "F" "z"}]
           (db/sort-by-fields ["L" "F"] [{"L" "c" "F" "z"}
                                         {"L" "c" "F" "b"}]))))
  (testing "multi field with descending"
    (is (= [{"L" "c" "F" "z"} {"L" "c" "F" "b"}]
           (db/sort-by-fields ["L" "-F"] [{"L" "c" "F" "z"}
                                          {"L" "c" "F" "b"}])))))
