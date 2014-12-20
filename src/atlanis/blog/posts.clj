(ns atlanis.blog.posts
  (:require [clojure.java.shell :refer [sh]]
            [stasis.core :as stasis]
            [clj-time.format :refer [parse formatter]]
            [me.raynes.fs :refer [base-name]]
            [atlanis.blog.config :as config]))

(defn last-modified
  [filename]
  (.lastModified (clojure.java.io/file filename)))

(defn get-org-headers
  "Gets the #+STUFF headers from an Org file. Returns a hash-map of them."
  [content]
  (reduce
   (fn [r [k v]]
     (cond
       (seq? (r k)) (assoc r k (conj (r k) v))
       (r k) (assoc r k (seq [(r k) v]))
       :else (assoc r k v)))
   {}
   (map (fn [row]
          [(-> row second clojure.string/lower-case keyword)
           (nth row 2)])
        (re-seq #"(?m)^\s*#\+(\w+):\s*(.+)$" content))))

(def org-date-formatter (formatter "'<'yyyy-MM-dd EEE HH:mm'>'"))

(defn convert-org-to-html
  "Converts an Org file to HTML using the user's local emacs installation."
  [filename]
  (:out (sh "emacs" "--batch" "--script" "resources/export-post.el" filename)))

(defn get-org-post
  "Gets a post from an Org file."
  [filename content]
  (println (str "Processing: " filename))
  (let [headers (get-org-headers content)]
    {:headers headers
     :modified (last-modified filename)
     :filename filename
     :title (:title headers)
     :date (parse org-date-formatter (:date headers))
     :path (str "/posts/" (base-name filename true) ".html")
     :content (convert-org-to-html filename)}))

(defn get-posts
  "Gets all posts from a given directory."
  ([directory] (get-posts directory 0))
  ([directory last-update]
     (->> (stasis/slurp-directory directory #"\.org$")
          (filter #(> (last-modified (str directory (first %))) last-update))
          (map #(get-org-post (str directory (first %)) (second %)))
          (map (fn [_] [(:filename _) _]))
          (into {}))))

(defn now
  []
  (java.util.Date.))

(defn unix-time
  [date]
  (.getTime date))

(def last-update (atom 0))
(def old-posts (atom {}))

(defn load-posts
  []
  (let [posts (get-posts "resources/posts/" @last-update)]
    (reset! last-update (unix-time (now)))
    (swap! old-posts merge posts)))
