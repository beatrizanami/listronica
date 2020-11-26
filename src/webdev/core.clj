(ns webdev.core
  (:require [ring.adapter.jetty :as jetty]
            [ring.middleware.reload :refer [wrap-reload]]
            [compojure.core :refer [defroutes GET]]
            [compojure.route :refer [not-found]]
            [ring.handler.dump :refer [handle-dump]]))

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

(defroutes app
           (GET "/" [] greet)
           (GET "/goodbye" [] goodbye)
           (GET "/about" [] about)
           (GET "/request" [] handle-dump)
           (GET "/yo/:name" [] yo)
           (GET "/calc/:a/:op/:b" [] calc)
           (not-found "Page not found"))

(defn -main [port]
  (jetty/run-jetty app
                   {:port (Integer. port)}))

(defn -dev-main [port]
  (jetty/run-jetty (wrap-reload #'app)
                   {:port (Integer. port)}))

