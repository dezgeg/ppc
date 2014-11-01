(ns ppc.web
  (:require [compojure.core :refer :all]
            [compojure.route :as route]
            [clojure.java.io :as io]
            [ring.adapter.jetty :as jetty]
            [ring.middleware.defaults :refer [wrap-defaults site-defaults]]
            [ring.middleware.format :refer [wrap-restful-format]]
            [environ.core :refer [env]]
            [ppc.task :as task]))

(def tasks (atom [task/exampletask
                  task/exampletask]))

(defroutes app-routes
  (GET "/" [] 
       {:status 200
        :headers {"Content-Type" "text/html"}
        :body (slurp (io/resource "index.html"))})
  (GET "/api/tasks" []
       {:status 200
        :body @tasks})
  (GET "*" []
       (route/resources "/"))
  (route/not-found "Not Found"))

(def app
  (-> app-routes
    (wrap-restful-format :formats [:edn])))

(defn -main [& [port]]
  (let [port (Integer. (or port (env :port) 3000))]
    (jetty/run-jetty #'app {:port port :join? false})))
