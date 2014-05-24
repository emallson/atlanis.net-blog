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
  (apply 
   hash-map 
   (flatten
    (map (fn [row] [(keyword (clojure.string/lower-case (second row))) (nth row 2)])
         (re-seq #"(?m)^\s*#\+(\w+):\s*(.+)$" content)))))

(def org-date-formatter (formatter "'<'yyyy-MM-dd EEE HH:mm'>'"))

(def org-export-command
  "(progn
     (load \"~/.emacs.d/custom.el\")
     (package-initialize)
     (require 'org)
     (find-file \"%f\")
     (setq org-confirm-babel-evaluate nil)
     (org-html-export-as-html nil nil nil t)
     (princ (buffer-string)))")

(defn convert-org-to-html
  "Converts an Org file to HTML using the user's local emacs installation."
  [filename]
  (:out (sh "emacs" "--batch" "--eval" (clojure.string/replace org-export-command #"%f" filename))))

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
