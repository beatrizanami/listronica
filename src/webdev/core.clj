(ns webdev.core
  (:require [ring.adapter.jetty :as jetty]
            [ring.middleware.reload :refer [wrap-reload]]
            [ring.middleware.params :refer [wrap-params]]
            [ring.middleware.resource :refer [wrap-resource]]
            [ring.middleware.file-info :refer [wrap-file-info]]
            [compojure.core :refer [defroutes ANY GET PUT POST DELETE]]
            [compojure.route :refer [not-found]]
            [ring.handler.dump :refer [handle-dump]]
            [webdev.item.model :as items]
            [webdev.item.handler :as handle-index-items]))

(def db (or
          (System/getenv "DATABASE_URL")
          "jdbc:postgresql://postgres:1234@postgres.local:5432/webdev"))

(defn greet [req]
  {:status  200
   :body    "Hello World!"
   :headers {}})

(defn goodbye [req]
  {:status  200
   :body    "Goodbye cruel world!"
   :headers {}})

(defn about [req]
  {:status  200
   :body    "I'm a Clojure developer :)"
   :headers {}})

(defn yo [req]
  (let [name (get-in req [:route-params :name])]
    {:status  200
     :body    (str "Yo " name "!")
     :headers {}}))

(def ops
  {"+" +
   "-" -
   "*" *
   ":" /})

(defn calc [req]
  (let [a (Integer. (get-in req [:route-params :a]))
        b (Integer. (get-in req [:route-params :b]))
        op (get-in req [:route-params :op])
        f (get ops op)]
    (if f
      {:status  200
       :body    (str (f a b))
       :headers {}}
      {:status  404
       :body    "Unknown operation"
       :headers {}})))

(defroutes routes
           (GET "/" [] greet)
           (GET "/goodbye" [] goodbye)
           (GET "/about" [] about)
           (ANY "/request" [] handle-dump)
           (GET "/yo/:name" [] yo)
           (GET "/calc/:a/:op/:b" [] calc)

           (GET "/items" [] handle-index-items/handle-index-items)
           (POST "/items" [] handle-index-items/handle-create-item)
           (DELETE "/items/:item-id" [] handle-index-items/handle-delete-item)
           (PUT "/items/:item-id" [] handle-index-items/handle-update-item)

           (not-found "Page not found"))

(defn wrap-db [handler]
  (fn [req]
    (handler (assoc req :webdev/db db))))

(defn wrap-server [handler]
  (fn [req]
    (assoc-in (handler req) [:headers "Server"] "BA List")))

(def sim-methods
  {"PUT"    :put
   "DELETE" :delete})

(defn wrap-simulated-methods [handler]
  (fn [req]
    (if-let [method (and (= :post (:request-method req))
                         (sim-methods (get-in req [:params "_method"])))]
      (handler (assoc req :request-method method))
      (handler req))))

(def app
  (wrap-server
    (wrap-file-info
      (wrap-resource
        (wrap-db
          (wrap-params
            (wrap-simulated-methods
              routes)))
        "static"))))

(defn -main [port]
  (items/create-table! db)
  (jetty/run-jetty app
                   {:port (Integer. port)}))

(defn -dev-main [port]
  (items/create-table! db)
  (jetty/run-jetty (wrap-reload #'app)
                   {:port (Integer. port)}))

;; TODO
;; Make homepage HTML, add a picture and show links to all GET routes
;; Rework the system to support multiple lists and multiple items each