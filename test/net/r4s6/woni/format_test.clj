(ns net.r4s6.woni.format-test
  (:require [clojure.test :refer [deftest testing is]]
            [net.r4s6.woni.format :as fmt]))

(deftest format-fields
  (testing "date"
    (is (= {"DateOfBirth" "05/06/2001" "l" "l"}
           (fmt/format-fields {"DateOfBirth" (java.time.LocalDate/of 2001 5 6)
                               "l" "l"})))
    (is (= {"DateOfBirth" "not-a-date" "l" "l"}
           (fmt/format-fields {"DateOfBirth" "not-a-date"
                               "l" "l"})))))
