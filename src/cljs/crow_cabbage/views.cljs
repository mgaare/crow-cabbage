(ns crow-cabbage.views
  (:require [re-frame.core :as re-frame]))

(defn currency-format
  "Formats number as USD currency"
  [number]
  ;; thanks Stack Overflow!
  (.toLocaleString number js/undefined #js {:style "currency" :currency "USD"}))

(defn catalog-item
  [{:keys [id name imageURL price bulkPricing] :as item}]
  [:div {:class "catalog-item"}
   [:img {:src imageURL
          :label name}]
   [:div {:class "details"}
    [:h3 {:class "name"} name]
    [:p {:class "price"} (currency-format price)
     (when bulkPricing
       (str " or " (:amount bulkPricing)
            " for " (currency-format (:totalPrice bulkPricing))))]]
   [:button {:type "button"
             :on-click #(re-frame/dispatch [:add-to-cart item])}
    "Add to Cart"]])

(defn catalog
  []
  (let [items (re-frame/subscribe [:catalog])]
    [:div {:class "catalog"}
     (for [item @items]
       ^{:key item} [catalog-item item])]))

(defn cart-product
  [id {:keys [item quantity total-price] :as product}]
  [:div {:class "product"}
   [:button {:class "add"
             :type "button"
             :on-click #(re-frame/dispatch [:add-to-cart item])}
    "+"]
   [:button {:class "remove"
             :type "button"
             :on-click #(re-frame/dispatch [:remove-one-from-cart id])}
    "-"]
   [:button {:class "delete"
             :type "button"
             :on-click #(re-frame/dispatch [:delete-from-cart id])}
    "X"]
   [:div {:class "name"} (:name item)]
   [:div {:class "quantity"} (str quantity)]
   [:div {:class "price"} (currency-format total-price)]])

(defn cart
  []
  (let [contents (re-frame/subscribe [:cart])]
    [:div {:class "cart"}
     [:h2 "Cart"]
     (for [item @contents]
       (let [[id product] item]
         ^{:key id} [cart-product id product]))
     [:div {:class "total"}
      "Total: "
      (currency-format
       (reduce-kv (fn [total id product]
                    (+ total (:total-price product)))
                  0
                  @contents))]]))

(defn main
  []
  [:div {:class "app"}
   [catalog]
   [cart]])
