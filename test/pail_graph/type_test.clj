(ns pail-graph.type-test
  (:require [pail-graph.type :as type])
  (:import (pail-graph.people DataUnit PersonProperty PersonPropertyValue Location))
  (:use midje.sweet))


(facts "field-ids"
  (fact "returns the set of valid field ids for unions"
    (type/field-ids DataUnit) => (just #{1 2})
    (type/field-ids PersonPropertyValue) => (just #{1 2 4 5}))

  (fact "returns the set of valid field ids for structs"
    (type/field-ids PersonProperty) => (just #{1 2})
    (type/field-ids Location) => (just #{1 2 3 4 5 6})))

(facts "ordered-field-ids"
  (fact "returns the set of valid field ids for unions"
    (type/field-ids DataUnit) => (just [1 2])
    (type/field-ids PersonPropertyValue) => (just [1 2 4 5]))

  (fact "returns the set of valid field ids for structs"
    (type/field-ids PersonProperty) => (just [1 2])
    (type/field-ids Location) => (just [1 2 3 4 5 6])))

(facts "field-names"
  (fact "returns the set of valid field names for structs or unions"
    (type/field-names DataUnit) => (just #{"property" "friendshipedge"})
    (type/field-names PersonPropertyValue) => (just #{"first_name" "last_name" "location" "age"}))

  (fact "returns the set of valid field ids for structs"
    (type/field-names PersonProperty) => (just #{"id" "property"})
    (type/field-names Location) => (just #{"address" "city" "county" "state" "country" "zip"})))

(facts "ordered-field-names"
  (fact "returns a vector of valid field names for structs or unions"
    (type/ordered-field-names DataUnit) => (just ["property" "friendshipedge"])
    (type/ordered-field-names PersonPropertyValue) => (just ["first_name" "last_name" "location" "age"]))

  (fact "returns a vector of valid field ids for structs"
    (type/ordered-field-names PersonProperty) => (just ["id" "property"])
    (type/ordered-field-names Location) => (just ["address" "city" "county" "state" "country" "zip"])))

(facts "field-ids-names"
  (fact "returns a vector of valid field ids and names for structs or unions"
    (type/field-ids-names DataUnit) => (just [[1 "property"] [2 "friendshipedge"]])
    (type/field-ids-names PersonPropertyValue) => (just [[1 "first_name"] [2 "last_name"] [4 "location"] [5 "age"]]))

  (fact "returns a vector of valid field ids and names for structs or unions"
    (type/field-ids-names PersonProperty) => (just [[1 "id"] [2 "property"]])
    (type/field-ids-names Location) => (just [[1 "address"] [2 "city"] [3 "county"] [4 "state"] [5 "country"] [6 "zip"]])))

(facts "property-paths"
  (fact "returns a vector of valid field pathsfor structs or unions"
    (first (type/property-paths DataUnit)) => (just [[1 "property"] [1 "firstName"]])
    (second (type/property-paths DataUnit)) => (just [[1 "property"] [2 "lastName"]])
    (last (type/property-paths DataUnit)) => (just [[2 "friendshipedge"]])

    (first (type/property-paths PersonPropertyValue)) => (just [[1 "first_name"]])
    (second (type/property-paths PersonPropertyValue)) => (just [[2 "last_name"]])
    (last (type/property-paths PersonPropertyValue)) => (just [[5 "age"]]))

  (fact "returns a vector of valid paths for structs or unions"
    (first (type/property-paths PersonProperty)) => (contains  [[1 "id"]])
    (last (type/property-paths PersonProperty)) => (contains  [[2 "property"]])

    (first (type/field-ids-names Location)) => (just [1 "address"])
    (second (type/field-ids-names Location)) => (just [2 "city"])
    (nth (type/field-ids-names Location) 2) => (just [3 "state"])
    (last  (type/field-ids-names Location)) => (just [4 "country"])))
