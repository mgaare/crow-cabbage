(ns crow-cabbage.events
  (:require [re-frame.core :as re-frame]
            [crow-cabbage.db :as db]))

(re-frame/reg-event-db
 :initialize-db
 (fn  [_ _]
   db/default-db))

(defn price
  "Returns cart price for a given item at given quantity, taking into
   account special bulk pricing."
  [item quantity]
  (let [{:keys [amount totalPrice]} (:bulkPricing item)
        price (:price item)]
    (if (and amount (>= quantity amount))
      (let [bulk-units (quot quantity amount)
            reg-units (mod quantity amount)]
        (+ (* reg-units price)
           (* bulk-units totalPrice)))
      (* quantity price))))

(defn with-new-price
  "Updates a product in a cart with the new total price."
  [{:keys [item quantity] :as product}]
  (assoc product :total-price (price item quantity)))

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
       (assoc-in [:cart id :item] item)
       (update-in [:cart id] with-new-price))))

(re-frame/reg-event-db
 :delete-from-cart
 (fn [db [_ id]]
   (update db :cart dissoc id)))

(re-frame/reg-event-db
 :remove-one-from-cart
 (fn [db [_ id]]
   (let [cart-state (get-in db [:cart id])]
     (cond (nil? cart-state)
           db
           (= 1 (:quantity cart-state))
           (update db :cart dissoc id)
           :else
           (-> db
               (update-in [:cart id :quantity] dec)
               (update-in [:cart id] with-new-price))))))
