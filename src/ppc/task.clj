(ns ppc.task
  (:use [midje.sweet :only [fact facts =>]]))

(def id-counter (atom 0))

(defn create-task [description]
  (let [id (swap! id-counter inc)]
    {:id id
     :description description}))

(def exampletask
  (create-task "this is a sample task"))

(defn add-task-list [board list-name]
  (assoc-in board
         [:task-lists list-name]
         []))

(defn create-task-board []
  {:task-lists {}})

(defn add-task-to-list [board list-name task]
  (update-in board
             [:task-lists list-name]
             #(conj % task)))

(def example-task-board
  (let [empty-board (create-task-board)
        with-list (add-task-list empty-board "sample-list")]
    (add-task-to-list with-list "sample-list" exampletask)))

(defn move-task [& {:keys [board from-list-name to-list-name task-id]}]
  (let [from-list (get-in board [:task-lists from-list-name])
        pred (fn [elem] (= (:id elem) task-id))
        without (remove pred from-list)
        task (first (filter pred from-list))
        board2 (assoc-in board
                         [:task-lists from-list-name]
                         (vec without))
        final (add-task-to-list board2 to-list-name task)]
    final))

(fact "move-task"
  (let [board (add-task-list example-task-board "list2")
        board2 (move-task :board board
                          :from-list-name "sample-list"
                          :to-list-name "list2"
                          :task-id (get-in board [:task-lists "sample-list" 0 :id]))]
    (get-in board2 [:task-lists "list2" 0 :description]) => "this is a sample task"
    (get-in board2 [:task-lists "sample-list"]) => empty?))
