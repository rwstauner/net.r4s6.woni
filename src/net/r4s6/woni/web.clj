(ns net.r4s6.woni.web
  (:require [compojure.core :refer [routes defroutes GET POST]]
            [compojure.route :refer [not-found]]
            [net.r4s6.woni.database :as db]
            [net.r4s6.woni.format :as fmt]
            [net.r4s6.woni.parse :as p]
            [org.httpkit.server :as server]
            [ring.logger :as rl]
            [ring.middleware.json :as rj]
            [ring.util.response :refer [response]]))

(defn respond-with-records
  [records]
  (response {:records records}))

(defn return-all
  "Create response of all records sorted by specified fields."
  [sort-fields]
  (->> (db/get-all)
       (db/sort-by-fields sort-fields)
       (map fmt/format-fields)
       (respond-with-records)))

; Map route portion to column spec.
(def sort-action {"email" ["Email"]
                  "birthdate" ["DateOfBirth"]
                  "name" ["LastName" "FirstName"]})

(defroutes base-api
  (routes
   (POST "/records" request
     (if (> (db/insert! (p/parse-file (:body request))) 0)
       {:status 204}
       {:status 422}))

   (GET "/records/:sort-spec" [sort-spec :<< sort-action]
     (return-all sort-spec))

   (not-found {:status 404})))

(defn api
  [& {:keys [log-fn] :or {log-fn println}}]
  (-> base-api
      (rj/wrap-json-response)
      (rl/wrap-log-response {:log-fn log-fn})))

(defn serve
  [{:keys [port]}]
  (server/run-server (api) {:port port
                            :error-logger println})
  (println (format "listening on http://localhost:%d" port)))