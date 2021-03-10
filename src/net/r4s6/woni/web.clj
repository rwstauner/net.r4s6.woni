(ns net.r4s6.woni.web
  (:require [compojure.core :refer [routes defroutes GET POST]]
            [compojure.route :refer [not-found]]
            [net.r4s6.woni.database :as db]
            [net.r4s6.woni.format :as fmt]
            [net.r4s6.woni.parse :as p]
            [org.httpkit.server :as server]
            [ring.logger :as rl]
            [ring.middleware.json :as rj]
            [ring.middleware.params :as rp]
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

(def sort-qs (comp p/parse-line str))

(defroutes base-api
  (routes
   (POST "/records" {:keys [body]}
     (let [saved? (some-> body
                          (p/parse-file)
                          (db/insert!)
                          (> 0))]
       (if saved?
         {:status 204}
         {:status 422})))

   (GET "/records/:sort-spec" [sort-spec :<< sort-action]
     (return-all sort-spec))

   (GET "/records" [sort :<< sort-qs]
     (return-all sort))

   (not-found {:status 404})))

(defn api
  [& {:keys [log-fn] :or {log-fn println}}]
  (-> base-api
      (rp/wrap-params)
      (rj/wrap-json-response)
      (rl/wrap-log-response {:log-fn log-fn})))

(defn serve
  [{:keys [port]}]
  (server/run-server (api) {:port port
                            :error-logger println})
  (println (format "listening on http://localhost:%d" port)))
