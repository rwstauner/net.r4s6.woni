(ns net.r4s6.woni.util-test
  (:require [clojure.test :refer [deftest testing is]]
            [clojure.string :as string]
            [net.r4s6.woni.util :as util]))

(deftest transform-fields
  (testing "transform"
    (is (= {"L" "LAST" "F" "First"}
           (util/transform-fields {"L" string/upper-case}
                                  {"L" "Last" "F" "First"})))))
