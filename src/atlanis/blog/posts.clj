(ns atlanis.blog.posts
  (:require [clojure.java.shell :refer [sh]]
            [stasis.core :as stasis]))

(defn get-org-headers
  "Gets the #+STUFF headers from an Org file. Returns a hash-map of them."
  [content]
  (apply 
   hash-map 
   (flatten
    (map (fn [row] [(keyword (clojure.string/lower-case (second row))) (nth row 2)])
         (re-seq #"(?m)^\s*#\+(\w+):\s*(.+)$" content)))))


(def org-export-command
  "(progn
     (package-initialize)
     (require 'org)
     (find-file \"%f\")
     (org-html-export-as-html nil nil nil t)
     (princ (buffer-string)))")

(defn convert-org-to-html
  "Converts an Org file to HTML using the user's local emacs installation."
  [filename]
  (sh "emacs" "--batch" "--eval" (clojure.string/replace org-export-command #"%f" filename)))

(defn get-org-post
  "Gets a post from an Org file."
  [filename content]
  {:headers (get-org-headers content)
   :content (convert-org-to-html filename)})

(defn get-posts
  "Gets all posts from a given directory."
  [directory]
  (map #(get-org-post (str directory (first %)) (second %)) (stasis/slurp-directory directory #"\.org$")))
