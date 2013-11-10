(defproject baconator "0.2.0-SNAPSHOT"
  :description "See bacon lovers around the world"
  :url "http://baconator.clojurecup.com"
  :license {:name "Eclipse Public License"
            :url "http://www.eclipse.org/legal/epl-v10.html"}
  :dependencies [[org.clojure/clojure "1.5.1"]
                 [org.clojure/clojurescript "0.0-2030"]
                 [com.taoensso/timbre "2.7.1"]
                 [ring/ring-core "1.2.1"]
                 [ring/ring-devel "1.2.1"]
                 [ring/ring-json "0.2.0"]
                 [compojure "1.1.6"]
                 [http-kit "2.1.11"]
                 [twitter-api "0.7.4"]
                 [cheshire "5.2.0"]
                 [javax.servlet/servlet-api "2.5"]
                 [org.clojure/tools.reader "0.7.10"]]
  :plugins [[lein-cljsbuild "1.0.0-alpha2"]
            [lein-ring "0.8.8"]]
  :hooks [leiningen.cljsbuild]
  :source-paths ["src/clj"]
  :cljsbuild {
              :builds {
                       :main {
                              :source-paths ["src/cljs"]
                              :compiler {:optimizations :simple
                                         :pretty-print false
                                         :output-to "resources/public/js/main.js"}
                              :jar true}}}
  :profiles {:uberjar {:main baconator.server.handler :aot :all}}
  :aot :all
  :main baconator.server.handler)
