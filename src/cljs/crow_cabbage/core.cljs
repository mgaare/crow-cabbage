(ns crow-cabbage.core
  (:require [reagent.core :as reagent]
            [re-frame.core :as re-frame]
            [crow-cabbage.events]
            [crow-cabbage.subs]
            [crow-cabbage.views :as views]
            [crow-cabbage.config :as config]))

(enable-console-print!)

(defn dev-setup []
  (when config/debug?
    (enable-console-print!)
    (println "dev mode")))

(defn mount-root []
  (re-frame/clear-subscription-cache!)
  (reagent/render [views/main]
                  (.getElementById js/document "app")))

(defn render []
  (re-frame/dispatch-sync [:initialize-db])
  (dev-setup)
  (mount-root))
