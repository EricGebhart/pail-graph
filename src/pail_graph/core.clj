(ns pail-graph.core
  "Defines Pail-Graph core functionality.
   clj-pail.core and  pail-cascalog.core are integrated
   into this namespace."
  (:require [pail-graph.type :as type]
            [clj-pail.core :refer :all]
            [pail-cascalog.core :refer :all]))

(defn tap-map [pail-struct]
  "Get the tap map for a Pail."
  (let [tapmapper (.getTapMapper pail-struct)
        type (.getType pail-struct)]
    (into {} (map tapmapper (type/property-paths type)))))

(defn list-taps [pail-struct]
  "Give a list of the Tap keys available for a Pail"
  (keys (tap-map pail-struct)))

(defn get-tap
  "Creates a `PailTap` from an existing vertically partitioned pail, by selecting an
   entry from the Pail's tap map."
  [pail-struct tap-key]
  (pail->tap pail :field-name (name tap-key) :attributes [(tap-key (pail/tap-map pail-struct))] ))


; these can go away when the pull request for clj-pail is done.
(defn ^Pail create
   "Creates a Pail from a PailSpec at `path`."
   [spec-or-structure path & {:keys [filesystem fail-on-exists]
                              :or {fail-on-exists true}}]
   (if (instance? spec-or-structure PailStructure)
     (recur (spec spec-or-structure))
     (if filesystem
       (Pail/create filesystem path spec-or-structure fail-on-exists)
       (Pail/create path spec-or-structure fail-on-exists))))

(defn find-or-create [pstruct path & {:as create-key-args}]
  "Get a pail from a path, or create one if not found"
  (try (pail path)
       (catch Exception e
                (apply create pstruct path (mapcat identity create-key-args)))))

(defn write-objects
  "Write a list of objects to a pail"
  [pail objects]
  (with-open [writer (.openWrite pail)]
    (doseq [o objects]
        (.writeObject writer o))))
