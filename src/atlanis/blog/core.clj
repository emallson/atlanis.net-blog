(ns atlanis.blog.core
  (:require [ring.middleware.content-type :refer [wrap-content-type]]
            [stasis.core :as stasis]
            [optimus.assets :as assets]
            [optimus.prime :as optimus]
            [optimus.optimizations :as optimizations]
            [optimus.strategies :refer [serve-live-assets]]
            [optimus.export]))

(defn get-assets
  []
  (concat
   (assets/load-bundle "public" "/styles.css" ["/styles/default.css"])))

(defn get-pages
  []
  nil)

(def app
  (-> (stasis/serve-pages get-pages)
      (optimus/wrap get-assets optimizations/all serve-live-assets)
      wrap-content-type))
