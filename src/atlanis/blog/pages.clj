(ns atlanis.blog.pages
  (:require [atlanis.blog.templates :as tpl]
            [stasis.core :as stasis]))

(defn export?
  "Determines whether a post should be exported"
  [[_ post]]
  (not (= (:export (:headers post)) "nil")))

(defn- create-page
  [f config]
  (fn [context] (apply str (tpl/layout context (f) config))))

(defn- single-posts
  [posts config]
  (->> posts
       (map (fn [post]
              [(:path (second post))
               (create-page #(tpl/one-post post config) config)]))
       (into {})))

(defn get-pages [posts config]
  (let [exported-posts (filter export? posts)]
    (stasis/merge-page-sources
     {:general-pages {"/index.html" (create-page #(tpl/all-posts exported-posts config) config)}
      :posts (single-posts exported-posts config)})))
