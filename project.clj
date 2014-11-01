(defproject tta "1.0.0-SNAPSHOT"
  :description "OPTIMAL PROJECT TOOL"
  :url ""
  :license {:name "Eclipse Public License v1.0"
            :url "http://www.eclipse.org/legal/epl-v10.html"}
  :dependencies [[org.clojure/clojure "1.6.0"]
                 [compojure "1.1.8"]
                 [ring/ring-jetty-adapter "1.2.2"]
                 [ring-middleware-format "0.4.0"]
                 [ring/ring-defaults "0.1.2"]
                 [environ "0.5.0"]
                 [jayq "2.5.2"]
                 [hiccups "0.3.0"]
                 [org.clojure/clojurescript "0.0-2371"]
                 [org.clojure/algo.monads "0.1.5"]]
  :min-lein-version "2.0.0"
  :plugins [[environ/environ.lein "0.2.1"]
            [lein-cljsbuild "1.0.3"]
            [lein-ring "0.8.13"]]
  :hooks [environ.leiningen.hooks leiningen.cljsbuild]
  :uberjar-name "ppc.jar"
  :profiles {:production {:env {:production true}}
             :dev {:dependencies [[midje "1.6.3"]]
                   :plugins [[lein-midje "3.1.3"]]}}
  :cljsbuild {:builds [{:source-paths ["cljs"]
                        :jar true
                        :compiler {:output-to "resources/public/js/app.js"
                                   :optimizations :whitespace
                                   :pretty-print true}}]}
  :ring {:handler ppc.web/app})
