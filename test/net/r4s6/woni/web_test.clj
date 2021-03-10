(ns net.r4s6.woni.web-test
  (:require [clojure.test :refer [deftest testing is use-fixtures]]
            [cheshire.core :as json]
            [net.r4s6.woni.database :as db]
            [net.r4s6.woni.web :as web]
            [ring.mock.request :as mock]))

(defn with-clean-db
  [f]
  (db/truncate!)
  (f))

(use-fixtures :each with-clean-db)

; Helpers to make the code shorter.
(def request (web/api :log-fn identity))

(defn date
  [y m d]
  (java.time.LocalDate/of y m d))

(defn record
  [l f e d]
  {"LastName" l "FirstName" f "Email" e "DateOfBirth" d})

(deftest post-records
  (testing "POST /records"
    (let [response (request (-> (mock/request :post "/records")
                                (mock/body "LastName\nl\nz")))]
      (is (= 204 (:status response)))
      (is (= [{"LastName" "l"}
              {"LastName" "z"}]
             (db/get-all)))))

  (testing "POST single bare record"
    (let [response (request (-> (mock/request :post "/records")
                                (mock/body "last first eml fc 1/2/2002")))]
      (is (= 204 (:status response)))
      (is (= {"LastName" "last" "FirstName" "first" "Email" "eml" "FavoriteColor" "fc" "DateOfBirth" (date 2002 1 2)}
             (last (db/get-all))))))

  (testing "POST no valid records"
    (let [response (request (-> (mock/request :post "/records")
                                (mock/body "x")))]
      (is (= 422 (:status response)))))
  (testing "POST no body"
    (let [response (request (-> (mock/request :post "/records")))]
      (is (= 422 (:status response)))))
  (testing "POST unaccepted content-type"
    (let [response (request (-> (mock/request :post "/records")
                                (mock/body "LastName\nl\nz")
                                (mock/header "Content-Type" "application/x-www-form-urlencoded")))]
      (is (= 422 (:status response))))))

(deftest get-records
  (db/insert! [(record "l" "f" "e2" (date 2000 1 2))
               (record "a" "c" "e1" (date 2000 3 4))
               (record "a" "b" "e3" (date 2000 1 3))])

  (testing "GET /records/email"
    (let [response (request (-> (mock/request :get "/records/email")))
          body (json/parse-string (:body response))]
      (is (= 200 (:status response)))
      (is (= {"records" [(record "a" "c" "e1" "03/04/2000")
                         (record "l" "f" "e2" "01/02/2000")
                         (record "a" "b" "e3" "01/03/2000")]}
             body))))

  (testing "GET /records/birthdate"
    (let [response (request (-> (mock/request :get "/records/birthdate")))
          body (json/parse-string (:body response))]
      (is (= 200 (:status response)))
      (is (= {"records" [(record "l" "f" "e2" "01/02/2000")
                         (record "a" "b" "e3" "01/03/2000")
                         (record "a" "c" "e1" "03/04/2000")]}

             body))))

  (testing "GET /records/name"
    (let [response (request (-> (mock/request :get "/records/name")))
          body (json/parse-string (:body response))]
      (is (= 200 (:status response)))
      (is (= {"records" [(record "a" "b" "e3" "01/03/2000")
                         (record "a" "c" "e1" "03/04/2000")
                         (record "l" "f" "e2" "01/02/2000")]}

             body))))

  (testing "GET /records/unknown"
    (let [response (request (-> (mock/request :get "/records/unknown")))]
      (is (= 404 (:status response)))))

  (testing "GET /records no sort"
    (let [response (request (-> (mock/request :get "/records")))
          body (json/parse-string (:body response))]
      (is (= 200 (:status response)))
      (is (= {"records" [(record "l" "f" "e2" "01/02/2000")
                         (record "a" "c" "e1" "03/04/2000")
                         (record "a" "b" "e3" "01/03/2000")]}

             body))))

  (testing "GET /records?sort="
    (let [response (request (-> (mock/request :get "/records?sort=-LastName,-Email")))
          body (json/parse-string (:body response))]
      (is (= 200 (:status response)))
      (is (= {"records" [(record "l" "f" "e2" "01/02/2000")
                         (record "a" "b" "e3" "01/03/2000")
                         (record "a" "c" "e1" "03/04/2000")]}

             body)))))
