(ns webdev.item.handler
  (:require [webdev.item.model :as item]
            [webdev.item.view :as view])
  (:import (java.util UUID)))

(defn handle-index-items [req]
  (let [db (:webdev/db req)
        items (item/read-items! db)]
    {:status 200
     :headers {}
     :body (view/items-page items)}))

(defn handle-create-item [req]
  (let [db (:webdev/db req)
        name (get-in req [:params "name"])
        description (get-in req [:params "description"])
        item-id (item/create-item! db name description)]
    {:status 302
     :headers {"Location" "/items"}
     :body ""}))

(defn handle-delete-item [req]
  (let [db (:webdev/db req)
        item-id (UUID/fromString (:item-id (:route-params req)))
        exists? (item/delete-item! db item-id)]
    (if exists?
      {:status 302
       :headers {"Location" "/items"}
       :body ""}
      {:status 404
       :header {}
       :body "List not found"})))

(defn handle-update-item [req]
  (let [db (:webdev/db req)
        item-id (UUID/fromString (:item-id (:route-params req)))
        checked (get-in req [:params "checked"])
        exists? (item/update-item! db item-id (= "true" checked))]
    (if exists?
      {:status 302
       :headers {"Location" "/items"}
       :body ""}
      {:status 404
       :header {}
       :body "Item not found"})))