;;; templates.clj
;;; This file was more or less straight-up copied from https://github.com/magnars/what-the-emacsd
(ns atlanis.blog.templates
  (:require [net.cgrand.enlive-html :refer :all]
            [optimus.link :as link]
            [clojure.java.io :as io]))

(defn- stylesheet-link
  "Creates a <link> tag to a stylesheet."
  [path config]
  {:tag :link, :attrs {:rel "stylesheet" :href (str (:root config) path)}})

(defn- inline-script
  "Creates an inline <script> tag."
  [request path]
  {:tag :script,
   :content (->> request :optimus-assets
                 (filter #(= path (:original-path %)))
                 first :contents)})

(deftemplate layout "templates/layout.html"
  [request body config]
  [:title] (after (map #(stylesheet-link % config) (link/bundle-paths request ["/styles.css"])))
  [:h1.site-title :a] (set-attr :href (:root config))
  [:div#content] (content body))

(def date-format
  (clj-time.format/formatter "HH:mm 'on' EEE, dd MMMM yyyy 'EST'"))

(def date-formatter
  (partial clj-time.format/unparse date-format))

(defn snip
  [path]
  (html-snippet (slurp (io/resource path))))

(defsnippet post-body (snip "templates/post-body.html")
  [root] [post]
  [:h1.entry-title] (content (:title post))
  [:div.entry-content] (content (html-snippet (:content post)))
  [:a.post-date] (content (date-formatter (:date post))))

(defsnippet all-posts (snip "templates/all-posts.html")
  [root] [posts config]
  [:article.post] (clone-for 
                   [[_ post] posts]
                   (let [link (str (:root config) (:path post))]
                     (content (at (post-body post)
                                  [:h1.entry-title] (do->
                                                     unwrap
                                                     (wrap :a {:class "entry-title-link"})
                                                     (wrap :h1 {:class "entry-title"}))
                                  #{[:a.post-date]
                                    [:a.entry-title-link]} (set-attr :href link)
                                    [:a.comments-link] (set-attr :href (str link "#disqus_thread")))))))

(defsnippet post-page (snip "templates/post-page.html")
  [root] [posts page-number num-pages config]
  [:article.post] (substitute (all-posts posts config))
  [:a#btn-newer-posts] #(when (> page-number 1)
                          (let [f (do->
                                   (set-attr :href (str (:root config) "/page/" (dec page-number) ".html")))]
                            (f %)))
  [:a#btn-older-posts] #(when (< page-number num-pages)
                          (let [f (do->
                                   (set-attr :href (str (:root config) "/page/" (inc page-number) ".html")))]
                            (f %))))

(defsnippet one-post (snip "templates/one-post.html")
  [root] [[_ post] config]
  [:article.post] (prepend (at (post-body post)
                               [:a.comments-link] nil))
  [:input#disqus_identifier] (set-attr :value (:name post))
  [:input#disqus_url] (set-attr :value (str (:root config) (:path post))))
