(ns ppc.web
  (:require [compojure.core :refer :all]
            [compojure.route :as route]
            [clojure.java.io :as io]
            [ring.adapter.jetty :as jetty]
            [ring.middleware.defaults :refer [wrap-defaults site-defaults]]
            [ring.middleware.format :refer [wrap-restful-format]]
            [environ.core :refer [env]]
            [ppc.task :refer :all]))

(def task-board (atom example-task-board))

(def list-name "sample-list")

(defroutes app-routes
  (GET "/" [] 
       {:status 200
        :headers {"Content-Type" "text/html"}
        :body (slurp (io/resource "index.html"))})
  (GET "/api/tasks" []
       {:status 200
        :body @task-board})
  (POST "/api/tasks" [task]
       (swap! task-board #(add-task-to-list % list-name (create-task task)))
       {:status 200
        :body "OK"})
  (POST "/api/task-lists" [task-list]
       (println (pr-str @task-board))
       (swap! task-board #(add-task-list % (:name task-list)))
       {:status 200
        :body "OK"})
  (GET "*" []
       (route/resources "/"))
  (route/not-found "Not Found"))

(def app
  (-> app-routes
    (wrap-restful-format :formats [:edn])))

(defn -main [& [port]]
  (let [port (Integer. (or port (env :port) 3000))]
    (jetty/run-jetty #'app {:port port :join? false})))
