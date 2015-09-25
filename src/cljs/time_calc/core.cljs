(ns time-calc.core
  (:require [om.core :as om :include-macros true]
            [om-tools.dom :as dom :include-macros true]
            [om-tools.core :refer-macros [defcomponent]]
            [clojure.string :as string]
            [goog.date.duration :as duration])
  (:import [goog.date DateTime Interval]))

(enable-console-print!)

(def app-state (atom {:text "Hello Chestnut!"
                      :start-time nil
                      :end-time nil
                      :sum nil
                      :break 30}))

(defn format-break [v]
  (case (int v)
    60 "1:00"
    0 "-:-"
    (str "0:" v)))

(defn ->value [e]
  (-> e .-target .-value))

(defn ->simple-date [t]
  (let [[hh mm] (.split t ":")]
    (DateTime. 2015 0 1 hh mm)))

(defn calc-sum [start end break]
  (let [s-date (->simple-date start)
        e-date (->simple-date end)
        interval (Interval. 0 0 0 0 (int break))
        s-date (doto s-date (.add interval))
        dur (- e-date s-date)]
    (duration/format dur)))

(defn time-value? [s]
  (-> s (or "") (string/blank?) not))

(defn update-sum [app]
  (let [start-time (:start-time @app)
        end-time (:end-time @app)
        break (:break @app)]
    (when (and (time-value? start-time) (time-value? end-time))
      (om/update! app :sum (calc-sum start-time end-time break)))))

(defcomponent calculator [app owner]
  (will-mount [_] (update-sum app))
  (render-state [_ state]
                (dom/form
                 (dom/div {:class "row"}
                          (dom/div {:class "six columns"}
                                   (dom/label {:for "startTime"} "Starting Time")
                                   (dom/input {:type "time" :id "startTime" :class "u-full-width"
                                               :value (:start-time app)
                                               :on-change (fn [e]
                                                            (om/update! app :start-time
                                                                        (->value e))
                                                            (update-sum app))})))
                 (dom/div {:class "row"}
                          (dom/div {:class "six columns"}
                                   (dom/label {:for "startTime"} (str "Break " (format-break (:break app))))
                                   (dom/input {:type "range" :id "startTime" :class "u-full-width"
                                               :min 0 :max 60 :step 30 :value (:break app)
                                               :on-change (fn [e]
                                                            (om/update! app :break
                                                                             (->value e))
                                                            (update-sum app))})))
                 (dom/div {:class "row"}
                          (dom/div {:class "six columns"}
                                   (dom/label {:for "endTime"} "End Time")
                                   (dom/input {:type "time" :id "endTime" :class "u-full-width"
                                               :value (:end-time app)
                                               :on-change (fn [e]
                                                            (om/update! app :end-time
                                                                        (->value e))
                                                            (update-sum app))})))
                 (dom/div {:class "row"}
                          (dom/div {:class "six columns"}
                                   (dom/label {:for "sum"} "Time Span")
                                   (dom/input {:type "text" :id "sum" :class "u-full-width"
                                               :value (:sum app)}))))))

(defn main []
  (om/root
   calculator
   app-state
   {:target (. js/document (getElementById "app"))}))








