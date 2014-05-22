(ns atlanis.blog.rss
  (:require [clojure.data.xml :as xml]
            [atlanis.blog.config :as config]))

;;; This file is more-or-less copy-pasted from @magnars what-the-emacsd source.
;;; Many thanks to him!
;;; Link: https://github.com/magnars/what-the-emacsd/blob/master/src/what_the_emacsd/rss.clj

(defn- entry [post]
  [:entry
   [:title (:title post)]
   [:updated (:date post)]
   [:author [:name "J David Smith"]]
   [:link {:href (str config/site-root (:path post))}]
   [:id (str "urn:atlanis-net-blog:feed:post:" (:name post))]
   [:content {:type "html"} (:content post)]])

(defn atom-xml [posts]
  (xml/emit-str
   (xml/sexp-as-element
    [:feed {:xmlns "http://www.w3.org/2005/Atom"}
     [:id "urn:atlanis-net-blog:feed"]
     [:updated (-> posts first :date)]
     [:title {:type "text"} "Record of Motion"]
     [:link {:rel "self" :href (str config/site-root "atom.xml")}]
     (map entry posts)])))
