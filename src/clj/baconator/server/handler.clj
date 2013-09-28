(ns baconator.server.handler
  (:use compojure.core
        ring.util.response
        org.httpkit.server)
  (:require [compojure.handler :as handler]
            [compojure.route :as route]
            [ring.middleware.reload :as reload]
            [org.httpkit.client :as http]))

(defroutes app-routes
  (GET "/" [] "Hello world!")
  (route/not-found "Not Found"))

(def application
  (->
   (handler/site app-routes)
   reload/wrap-reload))

(defn -main [& args]
  (let [port (Integer/parseInt (or (System/getenv "PORT") "8080"))]
        (run-server application {:port port :join? false})))


