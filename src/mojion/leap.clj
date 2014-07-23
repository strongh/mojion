(ns mojion.leap
  (:require [gniazdo.core :as ws]
            [cheshire.core :as json]))

;; https://developer.leapmotion.com/documentation/javascript/supplements/Leap_JSON.html

(def ^:const LEAP-URL "ws://localhost:6437/v5.json")

(defn send-option
  [socket option v]
  (ws/send-msg socket (json/generate-string {option v})))

(defn event-type
  [event]
  (condp #(contains? %2 %1) event
    :version :version
    :type :device-event
    :tracking-data))

(defn connect
  "I don't do a whole lot."
  [event-fns
   & {:keys [background? focused? enable-gestures?]
      :or {background? true
           focused?    false
           enable-gestures? true}}]
  (let [event-map (if (fn? event-fns)
                    {:tracking-data event-fns}
                    event-fns)
        socket (ws/connect
                LEAP-URL :on-receive
                (fn [raw-event]
                  (let [e (json/parse-string raw-event true)
                        e-type (event-type e)]
                    ((get event-map e-type identity) e))))]
    (Thread/sleep 1000)
    (when background?
      (send-option socket :background true))
    (when focused?
      (send-option socket :focused true))
    (when enable-gestures?
      (send-option socket :enableGestures true))))
