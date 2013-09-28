(defproject baconator "0.1.0-SNAPSHOT"
  :description "See bacon lovers around the world"
  :url "http://baconator.clojurecup.com"
  :license {:name "Eclipse Public License"
            :url "http://www.eclipse.org/legal/epl-v10.html"}
  :dependencies [[org.clojure/clojure "1.5.1"]
                 [ring/ring-core "1.2.0"]
                 [ring/ring-devel "1.2.0"]
                 [ring/ring-json "0.2.0"]
                 [compojure "1.1.5"]
                 [http-kit "2.1.10"]
                 [twitter-api "0.7.4"]
                 [org.clojure/clojurescript "0.0-1859"]]
  :plugins [[lein-cljsbuild "0.3.3"]
            [lein-ring "0.8.7"]
            [lein-midje "3.1.2"]]
  :profiles {:dev {:dependencies [[midje "1.5.1"]
                                  [ring-mock "0.1.5"]]}}
  :hooks [leiningen.cljsbuild]
  :source-paths ["src/clj"]
  :cljsbuild {
              :builds {
                       :main {
                              :source-paths ["src/cljs"]
                              :compiler {:output-to "resources/public/js/main.js"
                                         :optimizations :simple
                                         :pretty-print true}
                              :jar true}}}
  :main baconator.server.handler)
