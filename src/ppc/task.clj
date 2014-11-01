(ns ppc.task)

(def exampletask
  {:description "this is a task"})

(defn create-task [task]
  {:description (:description task)})
