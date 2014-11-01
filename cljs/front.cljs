(ns app
  (:use [jayq.core :only [$ css text html bind]]
        [jayq.util :only [log]])
  (:use-macros [jayq.macros :only [let-ajax ready]])
  (:require-macros [hiccups.core :as hiccups])
  (:require [jayq.core :as jq]
            [hiccups.runtime :as hiccupsrt]))

(hiccups/defhtml render-add-task-form [list-name]
  [:div {:class "add-task-form form-inline" :data-task-list list-name}
    [:label {:for "description"} "Description:"]
    [:input {:name "description" :type "text"}]
    [:button {:class "add-task-button btn btn-default"} "Add task"]])

(hiccups/defhtml render-task [task my-task-list-name task-lists]
  [:div {:class "task" :data-task-id (:id task) :data-task-list-name my-task-list-name}
   (:description task)
   [:div {:class "form-inline"}
    "Move to task list: "
     [:select {:class "move-task-select"}
      (for [[list-name task-list] task-lists]
        [:option {:value list-name
                  :selected (= my-task-list-name list-name)}
        list-name])]]])

(hiccups/defhtml render-tasks [tasks my-task-list-name task-lists]
  [:div {:class "tasks"}
   (map #(render-task % my-task-list-name task-lists) tasks)])

(hiccups/defhtml render-task-list [[list-name task-list] task-lists]
  [:div {:class "task-list col-md-4"}
   [:h3 (str "list: " list-name)]
   (render-tasks task-list list-name task-lists)
   (render-add-task-form list-name)])

(hiccups/defhtml tasks-page [task-board]
  (let [task-lists (:task-lists task-board)]
    [:h2 "Tasks"]
    [:div
     (map #(render-task-list % task-lists) task-lists)]))

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

  (.on ($ js/document) "change" ".move-task-select"
        (fn [event]
          (let [to-list-name (.val ($ (aget event "target")))
                task-div (.parent ($ (aget event "target")))
                from-list-name (.data task-div "task-list-name")
                task-id (.data task-div "task-id")]
            (let-ajax [_ {:url "/api/tasks/move"
                          :data {:from-list-name from-list-name
                                 :to-list-name to-list-name
                                 :task-id task-id}
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
