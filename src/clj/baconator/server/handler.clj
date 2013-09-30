(ns baconator.server.handler
  (:use compojure.core
        ring.util.response
        org.httpkit.server
        twitter.callbacks
        twitter.callbacks.handlers)
  (:require [compojure.handler :as handler]
            [compojure.route :as route]
            [ring.middleware.reload :as reload]
            [cheshire.core :refer :all]
            [clojure.edn :as edn]
            [org.httpkit.client :as http]
            [twitter.oauth :as oauth]
            [twitter.api.streaming :as api])
  (:import (twitter.callbacks.protocols AsyncStreamingCallback)))

(declare send-checkin)

;; Twitter streaming api

(def twitter-config (edn/read-string (slurp "config/twitter.edn")))
(def twitter-creds (oauth/make-oauth-creds (:consumer-key twitter-config)
                                           (:consumer-secret twitter-config)
                                           (:token twitter-config)
                                           (:token-secret twitter-config)))

(def ^:dynamic *callback*
  (AsyncStreamingCallback.
   (fn [_resp payload]
      (let [tweet (parse-string (str payload) true)
            lower (.toLowerCase (:text tweet))]
        (when (:coordinates tweet)
          (println (:text tweet))
          (let [coordinates (:coordinates (:coordinates tweet))
                user (:screen_name (:user tweet))]
            (send-checkin {:user user
                           :text (:text tweet)
                           :lat (nth coordinates 1)
                           :lon (nth coordinates 0)})))))
   (fn [_resp]
      (println "Error connecting to twitter streaming API"))
   (fn [_resp excp]
      (.printStackTrace excp))))

(api/statuses-filter :params {:track "bacon"}
                     :oauth-creds twitter-creds
                     :callbacks *callback*)

;; Handle the checkins websocket

(def checkins-clients (atom {}))

(defn checkins [req]
  (with-channel req con
    (swap! checkins-clients assoc con true)
    (on-close con (fn [status]
                     (swap! checkins-clients dissoc con)))))

(defn send-checkin [msg]
  (doseq [client @checkins-clients]
    (send! (key client) (pr-str msg) false)))

;; Basic ring/compojure code

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


