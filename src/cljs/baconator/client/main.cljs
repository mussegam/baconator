(ns baconator.client.main
  (:require
   [cljs.reader :as reader]
   [goog.dom :as dom]
   [goog.string :as gstring]))

;; Setting up the world map

(def world-map (js/L.map "map", (clj->js {:dragging false :touchZoom false :zoomControl false :boxZoom false})))
(def tiles (js/L.tileLayer "http://{s}.tile.cloudmade.com/a0ae0154488d43c281abff127d602c05/997/256/{z}/{x}/{y}.png", (clj->js {:maxZoom 2 :minZoom 2})))
(.setView world-map (clj->js [21.28 0]) 2)
(.addTo tiles world-map)

;; Bacons

(def vis-marker (atom 0))
(def bacon (js/L.icon (clj->js {:iconUrl "img/bacon.png" :iconSize [60 40]})))
(defn place-bacon [lat lon]
  (let [marker (js/L.marker (clj->js [lat lon]) (clj->js {:icon bacon}))]
    (.removeLayer world-map @vis-marker)
    (reset! vis-marker marker)
    (.addTo marker world-map)))

;; Utils

(defn log [& args]
  (.log js/console (apply pr-str args)))

(defn log-obj [obj]
  (.log js/console obj))

(defn get-element [id]
  (dom/getElement (name id)))

;; Twitter users

(defn create-li-tweet [name msg has-geo]
  (let [li (dom/createElement "li")]
    (when has-geo
      (dom/setProperties li (js-obj "class" "has-geo")))
    (doto li (dom/setTextContent (str name ": " (gstring/unescapeEntities msg))))))

(defn show-tweet [name msg has-geo]
  (let [node (get-element :tweetlist)
        item (create-li-tweet name msg has-geo)
        childs (prim-seq (dom/getChildren node))
        new-childs (doall (conj (take 4 childs) item))]
    (dom/removeChildren node)
    (doseq [child new-childs]
      (dom/appendChild node child))))

;; Get bacon lovers

(def ws-url (str "ws://" js/window.location.host js/window.location.pathname "checkins"))
(def ws (new js/WebSocket ws-url))
(set! (.-onmessage ws) (fn [msg]
                         (let [raw (.-data msg)
                               data (reader/read-string raw)
                               has-geo (not (nil? (:lat data)))]
                           (when has-geo
                             (place-bacon (:lat data) (:lon data)))
                           (show-tweet (:user data) (:text data) has-geo))))




