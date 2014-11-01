(ns app
  (:use [jayq.core :only [$ css text html bind]]
        [jayq.util :only [log]])
  (:use-macros [jayq.macros :only [let-ajax ready]])
  (:require-macros [hiccups.core :as hiccups])
  (:require [jayq.core :as jq]
            [hiccups.runtime :as hiccupsrt]))

(hiccups/defhtml render-add-task-form [list-name]
  [:div {:class "add-task-form" :data-task-list list-name}
    [:label {:for "description"} "Description:"]
    [:input {:name "description" :type "text"}]
    [:button {:class "add-task-button"} "Add task"]])

(hiccups/defhtml render-task [task]
  [:div {:class "task"}
   (:description task)])

(hiccups/defhtml render-tasks [tasks]
  [:div {:class "tasks"}
   (map render-task tasks)])

(hiccups/defhtml render-task-list [[list-name task-list]]
  [:div {:class "task-list"}
   [:h3 (str "list: " list-name)]
   (render-tasks task-list)
   (render-add-task-form list-name)])

(hiccups/defhtml tasks-page [task-board]
  [:h2 "Tasks"]
  [:div
   (map render-task-list (:task-lists task-board))])

(defn refresh-page []
  (let-ajax [tasks {:url "/api/tasks"}]
    (html ($ :#tasks) (tasks-page tasks))))

(ready
  (refresh-page)

  (.on ($ js/document) "click" ".add-task-button"
        (fn [event]
          (let [form (.parent ($ (aget event "target")))
                list-name (.data form "task-list")
                description-textbox (.find form "[name='description']")]
            (let-ajax [_ {:url "/api/tasks"
                :data {:task {:description (.val description-textbox)} :task-list {:name list-name}}
                :contentType "application/edn"
                :type "POST"}]
            (refresh-page)
            (.val description-textbox "")))))

  (bind ($ :#add-task-list-button) "click"
        (fn [event]
            (let [name-textbox ($ "[name='name']")]
              (let-ajax [_ {:url "/api/task-lists"
                  :data {:task-list {:name (.val name-textbox)}}
                  :contentType "application/edn"
                  :type "POST"}]
              (refresh-page)
              (.val name-textbox ""))))))
