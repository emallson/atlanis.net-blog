(ns atlanis.blog.config
  (:require [optimus.optimizations :as optimizations]))

(defn pwd []
  (System/getProperty "user.dir"))

(def config {:production {:root "http://atlanis.net/blog"
                          :optimizations optimizations/all
                          :export-directory "./build/production/"
                          :page-size 10}
             :development {:root (str "file://" (pwd) "/build/development")
                           :optimizations optimizations/none
                           :export-directory "./build/development/"
                           :page-size 10}
             :ring {:root "http://localhost:3000"
                    :optimizations optimizations/none
                    :export-directory "./build/ring/"
                    :page-size 10}})
