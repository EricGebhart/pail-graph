(ns pail-graph.core-test
  (:require
            [pail-graph.core :as pt]
            [pail-graph.tapmapper :as tapmapper]
            [pail-graph.type :as type])
  (:import [pail-graph DataUnitPailStructure])
  (:use midje.sweet))


(facts "list-taps"
    (tabular "Get a tap map for the current structure"
        (fact
         (nth (seq (pt/list-taps (DataUnitPailStructure.))) ?count) => ?result)

         ?count  ?result
         0      :property
         1      :first_name
         2      :last_name
         3      :location
         4      :age
         5      :friendshipedge))

(facts "tap-map"
    (tabular "Get a tap map for the current structure"
        (fact
         (nth (seq (pt/tap-map (DataUnitPailStructure.))) ?count) => ?result)

         ?count  ?result
         0      [ :property   ["property"]]
         1      [ :first_name ["property" "first_name"]]
         2      [ :last_name  ["property" "last_name"]]
         3      [ :location   ["property" "location"]]
         4      [ :age        ["property" "age"]]
         5      [ :friendshipedge ["friendshipedge"]]))
