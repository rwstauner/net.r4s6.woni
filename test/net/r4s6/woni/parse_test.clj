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
           (p/parse-line "a b c"))))
  (testing "unknown format"
    (is (= ["a.b.c"]
           (p/parse-line "a.b.c")))))

(deftest parse-records
  (testing "parse empty list"
    (is (= []
           (p/parse-records []))))

  (testing "parse with header row"
    (is (= [{"LastName" "l" "FirstName" "f"}
            {"LastName" "a" "FirstName" "b"}]
           (p/parse-records ["LastName, FirstName"
                             "l, f" "a, b"]))))

  (testing "parse with custom fields"
    (is (= [{"LastName" "l" "FirstName" "f" "Mood" "happy"}
            {"LastName" "a" "FirstName" "b" "Mood" "sad"}]
           (p/parse-records ["LastName, FirstName, Mood"
                             "l, f, happy"
                             "a, b, sad"]))))

  (testing "parse without header row"
    (is (= [{"LastName" "l" "FirstName" "f" "Email" "e" "FavoriteColor" "fc" "DateOfBirth" "dob"}
            {"LastName" "a" "FirstName" "b" "Email" "c" "FavoriteColor" "d" "DateOfBirth" "e"}]
           (p/parse-records ["l, f, e, fc, dob"
                             "a, b, c, d, e"]))))

  (testing "drops rows that do not parse correctly"
    (is (= [{"LastName" "a" "FirstName" "b" "Email" "c" "FavoriteColor" "d" "DateOfBirth" "e"}]
           (p/parse-records ["a.b.c"
                             "a, b, c, d, e"])))
    (is (= [{"LastName" "a" "FirstName" "b"}]
           (p/parse-records ["LastName, FirstName"
                             "a.b.c"
                             "a, b, c, d, e"
                             "a, b"])))))
