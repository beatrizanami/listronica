(defproject webdev "0.1.0-SNAPSHOT"
  :description "FIXME: write description"
  :url "http://example.com/FIXME"
  :license {:name "EPL-2.0 OR GPL-2.0-or-later WITH Classpath-exception-2.0"
            :url "https://www.eclipse.org/legal/epl-2.0/"}
  :dependencies [[org.clojure/clojure "1.10.1"]
                 [ring "1.8.2"]
                 [compojure "1.6.2"]]
  :min-lein-version "2.0.0"
  :uberjar-name "webdev.jar"
  :repl-options {:init-ns webdev.core}
  :main webdev.core
  :profiles {:dev {:main webdev.core/-dev-main}})
