(ns atlanis.blog.pages
  (:require [atlanis.blog.templates :as tpl]
            [atlanis.blog.rss :as rss]
            [atlanis.blog.config :as config]
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

(defn- post-pages
  [paginated-posts config]
  (->> paginated-posts
       (map-indexed (fn [idx page]
                      [(str "/page/" (inc idx) ".html")
                       (create-page #(tpl/post-page page
                                                    (inc idx)
                                                    (count paginated-posts)
                                                    config)
                                    config)]))
       (into {})))

(defn get-pages [posts config]
  (let [exported-posts (->> posts
                            (filter export?)
                            (sort-by #(:date (second %)))
                            (reverse)),
        paginated (partition-all (:page-size config) exported-posts),
        pages (post-pages paginated config)]
    (stasis/merge-page-sources
     {:general-pages {"/index.html" (first (vals pages))
                      "/atom.xml" (rss/atom-xml exported-posts config)}
      :pages pages
      :posts (single-posts exported-posts config)})))
