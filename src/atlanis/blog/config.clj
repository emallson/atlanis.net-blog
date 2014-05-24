(ns atlanis.blog.config
  (:require [optimus.optimizations :as optimizations]))

(defn pwd []
  (System/getProperty "user.dir"))

(def config {:production {:root "http://atlanis.net/blog/"
                          :optimizations optimizations/all
                          :export-directory "./build/production/"}
             :development {:root (str "file://" (pwd) "/build/development/")
                           :optimizations optimizations/none
                           :export-directory "./build/development/"}
             :ring {:root "/"
                    :optimizations optimizations/none
                    :export-directory "./build/ring/"}})
