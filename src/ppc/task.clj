(ns ppc.task)


(def exampletask
  {:description "this is a task"})

(defn create-task [task]
  {:description (:description task)})

(defn add-task-list [list-name board]
  (assoc board
         list-name
         []))

(defn create-task-board []
  {:task-lists {}})

(defn add-task-to-list [board list-name task]
  (update-in board
             [:task-lists list-name]
             #(conj % task)))

(def example-task-board
  (let [empty-board (create-task-board)
        with-list (add-task-list "sample-list" empty-board)]
    (add-task-to-list with-list "sample-list" exampletask)))
