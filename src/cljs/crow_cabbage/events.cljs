(ns crow-cabbage.events
  (:require [re-frame.core :as re-frame]
            [crow-cabbage.db :as db]))

(re-frame/reg-event-db
 :initialize-db
 (fn  [_ _]
   db/default-db))

(re-frame/reg-event-db
 :add-to-cart
 (fn [db [_ {:keys [id] :as item}]]
   (-> db
       (update-in [:cart id :quantity]
                  (fnil inc 0))
       ;; there's probably a cleverer way to do this using a
       ;; subscription on the catalog, but the straightforward thing
       ;; seems to be just dump the whole item in the cart for
       ;; rendering purposes
       (assoc-in [:cart id :item] item))))
