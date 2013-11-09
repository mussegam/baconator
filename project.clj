(defproject baconator "0.2.0-SNAPSHOT"
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
                 [cheshire "5.2.0"]
                 [org.clojure/clojurescript "0.0-1859"]
                 [com.taoensso/timbre "2.6.3"]]
  :plugins [[lein-cljsbuild "0.3.3"]
            [lein-ring "0.8.7"]]
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
  :profiles {:uberjar {:main baconator.server.handler :aot :all}}
  :aot :all
  :main baconator.server.handler)
