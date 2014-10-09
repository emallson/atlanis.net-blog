(ns atlanis.blog.posts-test
  (:require [clojure.test :refer :all]
            [atlanis.blog.posts :refer :all]))

(deftest test-get-org-headers
  (let [content "#+TITLE: Content
                 #+DATE: [2014-01-01 Wed 23:59]

                 Some content here..."
        headerless-content "Some content here..."]
    (is (= (get-org-headers content)
           {:title "Content", :date "[2014-01-01 Wed 23:59]"}))
    (is (= (get-org-headers headerless-content)
           {}))))

(deftest test-convert-org-to-html
  (let [file "resources/posts/echogenetic.org"]
    (is (not (empty? (convert-org-to-html file))))))

(deftest test-get-org-post
  (let [filename "resources/posts/echogenetic.org"
        headers {:date "[2013-12-28 Sat 14:20]",
                 :title "Echogenetic -- WUBWUB",
                 :postid "296",
                 :blog "rom",
                 :category "Music",
                 :tags "music, front line assembly, dubstep, industrial"}
        post (get-org-post filename (slurp filename))]
    (is (= (:headers post) headers))
    (is (= (:title post) (:title headers)))
    (is (= (:date post) (clj-time.format/parse
                         org-date-formatter (:date headers))))
    (is (= (:path post) "/posts/echogenetic.html"))
    (is (not (empty? (:content post))))))

(deftest test-get-posts
  (let [post-count (-> (clojure.java.io/file "resources/posts/")
                       file-seq count dec)]
    (is (= (count (get-posts "resources/posts/")) post-count))))
