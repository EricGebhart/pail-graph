(defproject pail-graph "0.1.0-SNAPSHOT"
  :description "Library for using Graph Schema, Thrift, Pail and Cascalog."
  :url "http://github.com/EricGebhart/"
  :license {:name "Eclipse Public License"
            :url "http://www.eclipse.org/legal/epl-v10.html"}

  :min-lein-version "2.0.0"

  :source-paths ["src"]


  :dependencies [[org.clojure/clojure "1.5.1"]
                 [com.backtype/dfs-datastores "1.3.4"]
                 [org.apache.hadoop/hadoop-core "1.2.0" ]
                 [potemkin "0.3.4"]
                 [cascalog "2.0.0" ]
                 [clj-pail "0.1.3"]
                 [clj-thrift "0.1.1-SNAPSHOT"]
                 [pail-thrift "0.1.0"]
                 [pail-cascalog "0.1.0"]]

  :aot [pail-graph.data-unit-pail-structure
        pail-graph.union-pail-structure]

  :profiles {:1.3 {:dependencies [[org.clojure/clojure "1.3.0"]]}
             :1.4 {:dependencies [[org.clojure/clojure "1.4.0"]]}
             :1.5 {:dependencies [[org.clojure/clojure "1.5.1"]]}
             :1.6 {:dependencies [[org.clojure/clojure "1.6.0-master-SNAPSHOT"]]}

             :dev {:dependencies [[midje "1.5.1"]]
                   :plugins [[lein-thriftc "0.1.0"]
                             [lein-midje "3.0.1"]]
                   :prep-tasks ["thriftc" "javac"]
                   :source-paths ["src/test"]
                   :aot [pail-graph.fakes.structure]}}


  :deploy-repositories [["releases" {:url "https://clojars.org/repo" :username :gpg :password :gpg}]
                        ["snapshots" {:url "https://clojars.org/repo" :username :gpg :password :gpg}]])
