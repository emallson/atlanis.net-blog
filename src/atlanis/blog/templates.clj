;;; templates.clj
;;; This file was more or less straight-up copied from https://github.com/magnars/what-the-emacsd
(ns atlanis.blog.templates
  (:require [net.cgrand.enlive-html :refer :all]
            [optimus.link :as link]
            [clojure.java.io :as io]
            [atlanis.blog.config :as config]))

(defn- stylesheet-link
  "Creates a <link> tag to a stylesheet."
  [path]
  {:tag :link, :attrs {:rel "stylesheet" :href (str config/site-root path)}})

(defn- inline-script
  "Creates an inline <script> tag."
  [request path]
  {:tag :script,
   :content (->> request :optimus-assets
                 (filter #(= path (:original-path %)))
                 first :contents)})

(deftemplate layout "templates/layout.html"
  [request body]
  [:title] (after (map stylesheet-link (link/bundle-paths request ["/styles.css"])))
  [:h1.site-title :a] (set-attr :href config/site-root)
  [:div#content] (content body))

(def date-format
  (clj-time.format/formatter "HH:mm 'on' EEE, dd MMMM yyyy 'EST'"))

(def date-formatter
  (partial clj-time.format/unparse date-format))

(defn snip
  [path]
  (html-snippet (slurp (io/resource path))))

(defsnippet all-posts (snip "templates/all-posts.html")
  [root] [posts]
  [:article.post] (clone-for 
                   [post posts]
                   [:h1.entry-title :a] (do->
                                         (set-attr :href (str config/site-root (:path post)))
                                        (content (:title post)))
                   [:div#content] (do->
                                   (remove-attr :id) 
                                   (content (html-snippet (:content post))))
                   [:a#comments-link] (do->
                                       (remove-attr :id)
                                       (set-attr :href (str config/site-root (:path post) "#disqus_thread")))
                   [:a#timestamp] (do->
                                   (remove-attr :id)
                                   (set-attr :href (str config/site-root (:path post)))
                                   (content (date-formatter (:date post))))))

(defsnippet one-post (snip "templates/one-post.html")
  [root] [post next-post]
  [:h1.entry-title] (content (:title post))
  [:div#content] (do->
                  (remove-attr :id)
                  (content (html-snippet (:content post))))
  [:span#timestamp] (do->
                  (remove-attr :id)
                  (content (date-formatter (:date post))))
  [:a#more] (do->
             (remove-attr :id)
             (set-attr :href (str config/site-root (:path next-post)))
             (content (if next-post "Next Post")))
  [:input#disqus_identifier] (set-attr :value (:name post))
  [:input#disqus_url] (set-attr :value (str config/site-root (:path post))))
