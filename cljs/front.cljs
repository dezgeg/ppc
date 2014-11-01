(ns app
  (:use [jayq.core :only [$ css text html bind]])
  (:use-macros [jayq.macros :only [let-ajax ready]])
  (:require-macros [hiccups.core :as hiccups])
  (:require [jayq.core :as jq]
            [hiccups.runtime :as hiccupsrt]))

(hiccups/defhtml render-task [task]
  [:div {:class "task"}
   (:description task)])

(hiccups/defhtml render-tasks [tasks]
  [:h2 "Tasks"]
  [:div {:class "tasks"}
   (map render-task tasks)])

(defn refresh-page []
  (let-ajax [tasks {:url "/api/tasks"}]
    (html ($ :#tasks) (render-tasks tasks))))

(ready
  (refresh-page))
