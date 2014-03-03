(defproject atlanis.blog "0.1"
  :description "atlanis.net blog using stasis"
  :url "http://atlanis.net/blog/"
  :license {:name "GNU General Public License v3"
            :url "https://www.gnu.org/licenses/gpl-3.0-standalone.html"}
  :dependencies [[org.clojure/clojure "1.5.1"]
                 [stasis "0.7.1"]
                 [ring "1.2.1"]
                 [optimus "0.14.2"]
                 [enlive "1.1.5"]
                 [me.raynes/fs "1.4.4"]]
  :ring {:handler atlanis.blog.core/app}
  :target-path "target/%s"
  :profiles {:uberjar {:aot :all}
             :dev {:plugins [[lein-ring "0.8.10"]]
                   :source-paths ["dev"]}}
  :main atlanis.blog.core)
