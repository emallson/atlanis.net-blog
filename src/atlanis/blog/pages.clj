(ns atlanis.blog.pages
  (:require [atlanis.blog.templates :as tpl]
            [atlanis.blog.rss :as rss]
            [atlanis.blog.config :as config]
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

(defn- post-pages
  [paginated-posts]
  (->> paginated-posts
       (map-indexed (fn [idx page]
                      [(str "page/" (inc idx) ".html")
                       (create-page #(tpl/post-page page
                                                    (inc idx)
                                                    (count paginated-posts)))]))
       (into {})))

(defn get-pages [posts]
  (stasis/merge-page-sources
   (let [paginated (partition-all config/page-size posts)]
     {:general-pages {"/index.html" (create-page #(tpl/post-page (first paginated) 1
                                                                 (count paginated)))
                      "/atom.xml" (rss/atom-xml posts)}
      :pages (post-pages paginated)
      :posts (single-posts posts)})))
