(ns pail-graph.data-unit-pail-structure
  (:require [pail-graph.structure :refer [gen-structure]]
            [pail-thrift.serializer :as s]
            [pail-thrift.partitioner :as pt]
            [pail-graph.partitioner :as pg]
            [pail-graph.tapmapper :as t])
    (:import [people DataUnit])
  (:gen-class))

(gen-structure thrift-pail.DataUnitPailStructure
               :type DataUnit
               :serializer  (s/thrift-serializer DataUnit)
               ;:partitioner (pt/union-partitioner DataUnit)
               :partitioner (pg/union-property-name-partitioner DataUnit)
               :tapmapper   (t/union-tap-mapper))
