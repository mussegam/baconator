(ns baconator.server.handler
  (:use compojure.core
        ring.util.response
        org.httpkit.server)
  (:require [compojure.handler :as handler]
            [compojure.route :as route]
            [ring.middleware.reload :as reload]
            [cheshire.core :refer :all]
            [org.httpkit.client :as http]))

(def checkins-clients (atom {}))
(defn checkins [req]
  (with-channel req con
    (swap! checkins-clients assoc con true)
    (on-close con (fn [status]
                     (swap! checkins-clients dissoc con)))))

(future (loop []
          (doseq [client @checkins-clients]
            (send! (key client) (pr-str {:lat (rand 40) :lon (rand 5)}) false))
          (Thread/sleep 2000)
          (recur)))

(defroutes app-routes
  (GET "/checkins" [] checkins)
  (GET "/venues" [] "Venues")
  (route/resources "/")
  (route/not-found "Not Found"))

(def application
  (->
   (handler/site app-routes)
   reload/wrap-reload))

(defn -main [& args]
  (let [port (Integer/parseInt (or (System/getenv "PORT") "8080"))]
        (run-server application {:port port :join? false})))


