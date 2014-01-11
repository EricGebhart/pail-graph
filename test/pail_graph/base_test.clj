(ns pail-graph.base-test
  (:require [pail-graph.base :as base])
  (:import (pail-graph.people DataUnit PersonPropertyValue Location))
  (:use midje.sweet))


(facts "field-keys"
  (tabular "returns a list of field keys for a struct or union"
    (fact (base/field-keys ?object) => ?value)

    ?object                       ?value
    (Location. "123 haywood" "Asheville")   [:address :city :county :state :country :zip]
    (Location. "1 Pack Place" "Asheville")  [:address :city :county :state :country :zip]
    (Location.)                             [:address :city :county :state :country :zip])

  (tabular "returns a union field keys"
    (fact (base/field-keys ?object) => ?result)

    ?object                                   ?result
    (personpropertyValue/first_name "Eric")  [:first_name :last_name :location :age]))


(facts "property-union-value"
  (tabular "Pulls current value from union one level below. Top Union.Struct.Union-with-value."
           (fact (base/property-union-value (base/build ?type ?attributes) :property) => ?result)

    ?type       ?attributes                ?result
    DataUnit    {:property
                 {:id "123"
                  :property
                  {:first_name "Eric"}}}   "Eric"))


(facts "property-struct-value"
  (tabular "Pulls current value from struct at top level. Top Union.Struct-with-value."
           (fact (base/property-value (base/build ?type ?attributes) :id) => ?result)

    ?type       ?attributes                  ?result
    DataUnit    {:property
                    {:id "abc"
                     :last_name "Gebhart"}}  "abc")
