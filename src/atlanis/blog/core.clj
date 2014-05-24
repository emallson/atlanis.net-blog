(ns atlanis.blog.core
  (:require [ring.middleware.content-type :refer [wrap-content-type]]
            [clojure.string :as str]
            [stasis.core :as stasis]
            [optimus.assets :as assets]
            [optimus.prime :as optimus]
            [optimus.optimizations :as optimizations]
            [optimus.strategies :refer [serve-live-assets]]
            [optimus.export]
            [atlanis.blog.posts :as posts]
            [atlanis.blog.pages :as pages]
            [atlanis.blog.config :refer [config]]))

(defn get-assets
  []
  (concat
   (assets/load-bundle "public" "/styles.css" ["/styles/syntax.css"
                                               "/styles/syntax-tweaks.css"])))



(defn now
  []
  (java.util.Date.))

(defn unix-time
  [date]
  (.getTime date))

(def last-update (atom 0))
(def old-posts (atom {}))

(defn load-posts
  []
  (let [posts (posts/get-posts "resources/posts/" @last-update)]
    (reset! last-update (unix-time (now)))
    (swap! old-posts merge posts)))

(defn get-pages
  [config]
  (require 'atlanis.blog.templates :reload)
  (pages/get-pages (load-posts) config))

(def app
  (let [config (:ring config)]
    (-> (stasis/serve-pages #(get-pages config))
        (optimus/wrap get-assets (:optimizations config) serve-live-assets)
        wrap-content-type)))

(defn export
  [config]
  (let [pages (get-pages config),
        assets ((:optimizations config) (get-assets) {})]
    (stasis/empty-directory! (:export-directory config))
    (optimus.export/save-assets assets (:export-directory config))
    (stasis/export-pages pages (:export-directory config) {:optimus-assets assets})))

(defn -main
  [conf-section]
  (let [key (if (str/blank? conf-section)
              :production
              (keyword (subs conf-section 1)))]
    (export (get config key))))
