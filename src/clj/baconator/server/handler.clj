(ns baconator.server.handler
  (:gen-class)
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
            [twitter.api.streaming :as api]
            [taoensso.timbre :as timbre
                      :refer (trace debug info warn error fatal spy with-log-level)])
  (:import (twitter.callbacks.protocols AsyncStreamingCallback)))

(declare send-checkin)

;; Twitter streaming api

(def twitter-config (edn/read-string (slurp "config/twitter.edn")))
(def twitter-creds (oauth/make-oauth-creds (:consumer-key twitter-config)
                                           (:consumer-secret twitter-config)
                                           (:token twitter-config)
                                           (:token-secret twitter-config)))

(defn send-tweet-info [tweet]
  (if-let [coordinates (:coordinates (:coordinates tweet))]
    (send-checkin {:user (:screen_name (:user tweet))
                   :text (:text tweet)
                   :lat (nth coordinates 1)
                   :lon (nth coordinates 0)})
    (send-checkin {:user (:screen_name (:user tweet))
                   :text (:text tweet)
                   :lat nil
                   :lon nil})))

(def ^:dynamic *callback*
  (AsyncStreamingCallback.
   (fn [_resp payload]
     (try
       (let [tweet (parse-string (str payload) true)]
         (send-tweet-info tweet))
       (catch com.fasterxml.jackson.core.JsonParseException ex false)))
   (fn [_resp]
      (println "Error connecting to twitter streaming API"))
   (fn [_resp excp]
      (.printStackTrace excp))))

(defn track-word [word]
  (api/statuses-filter :params {:track word}
                       :oauth-creds twitter-creds
                       :callbacks *callback*))

;; Handle the checkins websocket

(def checkins-clients (atom {}))

(defn checkins [req]
  (with-channel req con
    (info con "connected")
    (swap! checkins-clients assoc con true)
    (on-close con (fn [status]
                    (info con "disconnected with status" status)
                    (swap! checkins-clients dissoc con)))))

(defn send-checkin [msg]
  (doseq [client @checkins-clients]
    (send! (key client) (pr-str msg) false)))

;; Basic ring/compojure code

(defroutes app-routes
  (GET "/" [] (resource-response "index.html" {:root "public"}))
  (GET "/checkins" [] checkins)
  (route/resources "/")
  (route/not-found "Not Found"))

(defn wrap-logging [handler]
  (fn [req]
    (let [resp (handler req)]
      (info  (:remote-addr req) "==" (:request-method req) (:uri req) "=>" (:status resp))
      resp)))

(def application
  (->
   (handler/site app-routes)
   (wrap-logging)
   reload/wrap-reload))

(defn -main [& args]
  (let [port (Integer/parseInt (or (System/getenv "PORT") "8080"))]
    (track-word "bacon")
    (run-server application {:port port :join? false})))
