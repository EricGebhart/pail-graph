(ns pail-graph.base
  (:require [pail-graph.type :as type]
            [clj-thrift.union :as union]
            [clj-thrift.base :refer :all]))

; simple data extraction
(defn property-value
  "get the named field value from the current structure in the top level union.
   Union -> struct :<field-name> - value."
  [object field-name]
  (value (union/current-value object) field-name))

(defn property-union-value
  "get a value from a union, inside a struct inside a union.
   name is the property name inside the struct.
   Union -> Struct :<field-name> -> Union - value."
  [object field-name]
  (union/current-value (property-value object field-name)))

(defn field-keys
  "Give back an ordered vector of field keys for a struct or union."
  [object]
  (into [] (map keyword (type/ordered-field-names (type object)))))
