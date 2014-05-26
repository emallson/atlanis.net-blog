(ns atlanis.blog.rss
  (:require [clojure.data.xml :as xml]))

;;; This file is more-or-less copy-pasted from @magnars what-the-emacsd source.
;;; Many thanks to him!
;;; Link: https://github.com/magnars/what-the-emacsd/blob/master/src/what_the_emacsd/rss.clj

(defn- entry [post config]
  [:entry
   [:title (:title post)]
   [:updated (:date post)]
   [:author [:name "J David Smith"]]
   [:link {:href (str (:root config) (:path post))}]
   [:id (str "urn:atlanis-net-blog:feed:post:" (:name post))]
   [:content {:type "html"} (:content post)]])

(defn atom-xml [posts config]
  (xml/emit-str
   (xml/sexp-as-element
    [:feed {:xmlns "http://www.w3.org/2005/Atom"}
     [:id "urn:atlanis-net-blog:feed"]
     [:updated (-> posts first :date)]
     [:title {:type "text"} "Record of Motion"]
     [:link {:rel "self" :href (str (:root config) "atom.xml")}]
     (map #(entry % config) posts)])))
