(ns atlanis.blog.pages
  (:require [atlanis.blog.templates :as tpl]
            [stasis.core :as stasis]))

(defn- create-page
  [f]
  (fn [context] (apply str (tpl/layout context (f)))))

(defn- single-posts
  [posts]
  (->> posts
       (partition-all 2 1)
       (map (fn [[post next-post]]
              [(:path post)
               (create-page #(tpl/one-post post next-post))]))
       (into {})))

(defn get-pages [posts]
  (stasis/merge-page-sources
   {:general-pages {"/index.html" (create-page #(tpl/all-posts posts))}
    :posts (single-posts posts)}))
