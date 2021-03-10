(ns net.r4s6.woni.parse-test
  (:require [clojure.test :refer [deftest testing is]]
            [net.r4s6.woni.parse :as p]))

(deftest parse-int
  (testing "int"
    (is (= 1
           (p/parse-int "1"))))
  (testing "zero-padded"
    (is (= 2
           (p/parse-int "02"))))
  (testing "invalid"
    (is (= nil
           (p/parse-int "x")))))

(defn local-date
  [y m d]
  (java.time.LocalDate/of y m d))

(deftest parse-date
  (testing "date"
    (is (= (local-date 2000 12 31)
           (p/parse-date "12/31/2000"))))
  (testing "zero-padded"
    (is (= (local-date 2010 1 2)
           (p/parse-date "01/02/2010"))))
  (testing "space padded"
    (is (= (local-date 2010 3 4)
           (p/parse-date "3/4/2010"))))
  (testing "not padded"
    (is (= (local-date 1999 5 6)
           (p/parse-date " 5/ 6/1999"))))
  (testing "invalid"
    (is (= "123"
           (p/parse-date "123")))))

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

  (testing "parse single line"
    (is (= []
           (p/parse-records ["FavoriteColor"])))
    (is (= [{"LastName" "FavoriteColor" "FirstName" "foo" "Email" "bar" "FavoriteColor" "baz" "DateOfBirth" "qux"}]
           (p/parse-records ["FavoriteColor, foo, bar, baz, qux"]))))

  (testing "drops rows that do not parse correctly"
    (is (= [{"LastName" "a" "FirstName" "b" "Email" "c" "FavoriteColor" "d" "DateOfBirth" "e"}]
           (p/parse-records ["a.b.c"
                             "a, b, c, d, e"])))
    (is (= [{"LastName" "a" "FirstName" "b"}]
           (p/parse-records ["LastName, FirstName"
                             "a.b.c"
                             "a, b, c, d, e"
                             "a, b"])))))
