(ns pail-graph.type-test
  (:require [pail-graph.type :as type])
  (:import (people DataUnit PersonProperty PersonPropertyValue Location))
  (:use midje.sweet))


(facts "ordered-field-ids"
  (fact "returns an ordered of valid field ids for unions"
    (type/field-ids DataUnit) => (just [1 2])
    (type/field-ids PersonPropertyValue) => (just [1 2 4 5]))

  (fact "returns the set of valid field ids for structs"
    (type/field-ids PersonProperty) => (just [1 2])
    (type/field-ids Location) => (just [1 2 3 4 5 6])))

(facts "ordered-field-names"
  (fact "returns a ordered vector of valid field names for structs or unions"
    (type/ordered-field-names DataUnit) => (just ["property" "friendshipedge"])
    (type/ordered-field-names PersonPropertyValue) => (just ["first_name" "last_name" "location" "age"]))

  (fact "returns a vector of valid field ids for structs"
    (type/ordered-field-names PersonProperty) => (just ["id" "property"])
    (type/ordered-field-names Location) => (just ["address" "city" "county" "state" "country" "zip"])))

(facts "property-paths"
  (fact "returns a vector of valid field pathsfor structs or unions"
    (first (type/property-paths DataUnit)) => (just [{:id 1 :name "property"} {:id 1 :name "id"}])
    (second (type/property-paths DataUnit)) => (just [{:id 1 :name "property"} {:id 2, :name "property"} {:id 1 :name "first_name"}])
    (last (type/property-paths DataUnit)) => (just [{:id 2 :name "friendshipedge"} {:id 2 :name "id2"}])

    (first (type/property-paths PersonPropertyValue)) => (just [{:id 1 :name "first_name"}])
    (second (type/property-paths PersonPropertyValue)) => (just [{:id 2 :name "last_name"}])
    (last (type/property-paths PersonPropertyValue)) => (just [{:id 5 :name "age"}]))

  (fact "returns a vector of valid paths for structs or unions"
    (first (type/property-paths PersonProperty)) => (contains  [{:id 1 :name "id"}])
    (last (type/property-paths PersonProperty)) => (contains  [{:id 2 :name "property"}])

    (first (type/property-paths Location)) => (just {:id 1 :name "address"})
    (second (type/property-paths Location)) => (just {:id 2 :name "city"})
    (nth (type/property-paths Location) 2) => (just {:id 3 :name "county"})
    (last  (type/property-paths Location)) => (just {:id 6 :name "zip"})))
