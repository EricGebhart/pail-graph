(ns pail-graph.tapmapper-test
  (:require
            [pail-graph.tapmapper :as tapmapper]
            [pail-graph.type :as type])
  (:import (pail_graph.people DataUnit PersonProperty PersonPropertyValue Location))
  (:use midje.sweet))


(facts "UnionTapMapper"
  (let [tapmapper (tapmapper/union-tap-mapper)]

    (tabular "Get a tap map for the current structure"
        (fact
         (nth (seq (into {} (map tapmapper (type/property-paths ?type)))) ?count) => ?result)


           ?type       ?count  ?result
           DataUnit      0      [:property [1]]
           DataUnit      1      [:friendshipedge [2]])))

(facts "UnionNameTapMapper"
  (let [tapmapper (tapmapper/union-name-tap-mapper)]

    (tabular "Get a tap map for the current structure"
        (fact
         (nth (seq (into {} (map tapmapper (type/property-paths ?type)))) ?count) => ?result)


           ?type       ?count  ?result
           DataUnit      0      [:property ["property"]]
           DataUnit      1      [:friendshipedge ["friendshipedge"]])))

(facts "UnionNamePropertyTapMapper"
  (let [tapmapper (tapmapper/union-name-property-tap-mapper)]

    (tabular "Get a tap map for the current structure"
        (fact
         (nth (seq (into {} (map tapmapper (type/property-paths ?type)))) ?count) => ?result)


           ?type       ?count  ?result
           DataUnit      0      [:property ["property"]]
           DataUnit      1      [:first_name ["property" "first_name"]]
           DataUnit      2      [:last_name ["property" "last_name"]]
           DataUnit      3      [:location ["property" "location"]]
           DataUnit      4      [:age ["property" "age"]]
           DataUnit      5      [:friendshipedge ["friendshipedge"]])))

(facts "UnionPropertyTapMapper"
  (let [tapmapper (tapmapper/union-property-tap-mapper)]

    (tabular "Get a tap map for the current structure"
        (fact
         (nth (seq (into {} (map tapmapper (type/property-paths ?type)))) ?count) => ?result)


           ?type       ?count  ?result
           DataUnit      0      [:property   [1]]
           DataUnit      1      [:first_name [1 1]]
           DataUnit      2      [:last_name  [1 2]]
           DataUnit      3      [:location   [1 4]]
           DataUnit      4      [:age        [1 5]]
           DataUnit      5      [:friendshipedge [2]])))
