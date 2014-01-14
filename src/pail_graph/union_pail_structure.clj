(ns pail-graph.union-pail-structure
  (:require [pail-graph.structure :refer [gen-structure]]
            [pail-thrift.serializer :as s]
            [pail-thrift.partitioner :as p]
            [pail-graph.tapmapper :as t])
    (:import [people DataUnit])
  (:gen-class))

(gen-structure pail-graph.UnionPailStructure
               :type DataUnit
               :serializer (s/thrift-serializer DataUnit)
               :partitioner (p/union-partitioner DataUnit)
               :tapmapper (t/union-tap-mapper))
