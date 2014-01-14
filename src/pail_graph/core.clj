(ns pail-graph.core
  "Defines Pail-Graph core functionality.
   clj-pail.core and pail-cascalog.core are integrated
   into this namespace."
  (:require [pail-graph.type :as type]
            [potemkin :as pt]
            [clj-pail.core]
            [pail-cascalog.core])
  (:import (com.backtype.hadoop.pail Pail PailSpec PailStructure)))

; pull in clj-pail and pail-cascalog core functionality.
(pt/import-vars [clj-pail.core
                    object-seq
                    spec
                    pail
                    with-snapshot
                    create]
                [pail-cascalog.core
                    pail->tap
                    tap-options
                    tap])


(defn pail-structure
  "Given a pail return the PailStructure."
  [pail]
  (-> pail (.getSpec) (.getStructure)))

(defn get-structure
  "Given a PailStructure or a Pail return a PailStructure"
  [pail-or-structure]
  (if (instance? PailStructure pail-or-structure)
        pail-or-structure
        (pail-structure pail-or-structure)))

(defn tap-map
  "Get the tap map for a Pail or Pail Structure"
  [pail-or-struct]
  (let [pail-struct (get-structure pail-or-struct)
        tapmapper (.getTapMapper pail-struct)
        type (.getType pail-struct)]
    (into {} (map tapmapper (type/property-paths type)))))

(defn list-taps
  "Give a list of the Tap keys available for a Pail or Pail Structure"
  [pail-or-struct]
  (let [pail-struct (get-structure pail-or-struct)]
    (keys (tap-map pail-struct))))

(defn get-tap
  "Creates a `PailTap` from an existing vertically partitioned pail, by selecting an
   entry from the Pail's tap map. Takes a pail connection. returns nil if no tap found."
  [pail tap-key]
  (when-let [attrs (tap-key (tap-map pail))]
    (pail->tap pail :field-name (name tap-key)
               :attributes [attrs])))


;;;; TODO
(defn validate
  "Validate that a pail connection matches a pail structure. This is basically an implementation
   of the validation code in dfs-datastores pail create(). The specs are only compared if
   .getName is not nil. Otherwise it's just a check to make sure the PailStructure types match."
  [pail-connection structure]
  (let [conn-spec (.getSpec pail-connection)
        conn-struct (.getStructure conn-spec)
        struct-spec (spec structure)]
    (cond (and (.getName struct-spec) (not (.equals conn-spec struct-spec))) false
          (not (= (type structure) (type conn-struct))) false
          :else true)))

; these can go away when the pull request for clj-pail is done.
(defn ^Pail create
   "Creates a Pail from a PailSpec at `path`."
   [spec-or-structure path & {:keys [filesystem fail-on-exists]
                              :or {fail-on-exists true}
                              :as opts}]
   (if (instance? PailStructure spec-or-structure)
     (apply create (spec spec-or-structure) path (mapcat identity opts))
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
