(ns net.r4s6.woni.parse-test
  (:require [clojure.test :refer [deftest testing is]]
            [net.r4s6.woni.parse :as p]))

(deftest parse-line
  (testing "pipe delimited"
    (is (= ["a" "b" "c"]
           (p/parse-line "a | b | c"))))
  (testing "comma delimited"
    (is (= ["a" "b" "c"]
           (p/parse-line "a, b, c"))))
  (testing "space delimited"
    (is (= ["a" "b" "c"]
           (p/parse-line "a b c")))))
