(ns crow-cabbage.views
  (:require [re-frame.core :as re-frame]))

(defn catalog-item
  [{:keys [id name imageURL price bulkPricing] :as item}]
  [:div {:class "catalog-item"}
   [:img {:src imageURL
          :label name}]
   [:div {:class "details"}
    [:h3 {:class "name"} name]
    [:p {:class "price"} (str "$" price)
     (when bulkPricing
       (str " or " (:amount bulkPricing)
            " for $" (:totalPrice bulkPricing)))]]
   [:button {:type "button"
             :on-click #(re-frame/dispatch [:add-to-cart item])}
    "Add to Cart"]])

(defn catalog
  []
  (let [items (re-frame/subscribe [:catalog])]
    [:div {:class "catalog"}
     (for [item @items]
       ^{:key item} (catalog-item item))]))
(defn cart
  []
  (let [contents (re-frame/subscribe [:cart])]
    [:div {:class "cart"}
     [:h2 "Cart"]
     (for [product @contents]
       (let [[id {:keys [quantity item]}] product]
         ^{:key id}
         [:div {:class "product"}
          [:span {:class "name"} (:name item)]
          [:span {:class "quantity"} (str quantity)]]))]))

(defn main
  []
  [:div {:class "app"}
   [catalog]
   [cart]])
