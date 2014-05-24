(ns atlanis.blog.core
  (:require [ring.middleware.content-type :refer [wrap-content-type]]
            [ring.util.response :refer [charset]]
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

(defn get-pages
  [config]
  (require 'atlanis.blog.templates :reload)
  (pages/get-pages (posts/load-posts) config))

(defn wrap-charset
  [handler charset-val]
  (fn [req]
    (if-let [resp (handler req)]
      (charset resp charset-val))))

(def app
  (let [config (:ring config)]
    (-> (stasis/serve-pages #(get-pages config))
        (optimus/wrap get-assets (:optimizations config) serve-live-assets)
        wrap-content-type
        (wrap-charset "UTF-8"))))

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
