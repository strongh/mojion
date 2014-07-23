(ns mojion.example
  (:use [mojion.leap :as leap])
  (:gen-class))

(defn -main [& _]
  (leap/connect
   (fn [event]
     (when-let [hand-coord (get-in event [:hands 0 :palmPosition 0])]
       (println "hand X coord:" hand-coord)))))
