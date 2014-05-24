(ns atlanis.blog.pages
  (:require [atlanis.blog.templates :as tpl]
            [stasis.core :as stasis]))

(defn- create-page
  [f config]
  (fn [context] (apply str (tpl/layout context (f) config))))

(defn- single-posts
  [posts config]
  (->> posts
       (partition-all 2 1)
       (map (fn [[post next-post]]
              [(:path post)
               (create-page #(tpl/one-post post next-post config) config)]))
       (into {})))

(defn get-pages [posts config]
  (stasis/merge-page-sources
   {:general-pages {"/index.html" (create-page #(tpl/all-posts posts config) config)}
    :posts (single-posts posts config)}))
