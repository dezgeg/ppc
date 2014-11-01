(ns app
  (:use [jayq.core :only [$ css text html bind]]
        [jayq.util :only [log]])
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

(hiccups/defhtml tasks-page [tasks]
  [:div
   (render-tasks tasks)]
)

(defn refresh-page []
  (let-ajax [tasks {:url "/api/tasks"}]
    (html ($ :#tasks) (tasks-page tasks))))

(ready
  (refresh-page)
  (log ($ :#add-task-button))
  (bind ($ :#add-task-button) "click"
        (fn [event]
            (let [description-textbox ($ "[name='description']")]
              (let-ajax [_ {:url "/api/tasks"
                  :data {:task {:description (.val description-textbox)}}
                  :contentType "application/edn"
                  :type "POST"}]
              (refresh-page)
              (.val description-textbox ""))))))
