(ns baconator.client.main)

;; Setting up the world map
(def map (js/L.map "map", (clj->js {:touchZoom false :zoomControl false :boxZoom false})))
(def tiles (js/L.tileLayer "http://{s}.tile.cloudmade.com/a0ae0154488d43c281abff127d602c05/997/256/{z}/{x}/{y}.png", (clj->js {:maxZoom 2})))
(.setView map (clj->js [21.28 0]) 2)
(.addTo tiles map)


