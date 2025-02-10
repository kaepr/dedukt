(ns dedukt.core)

(def db
  [[11 :user/name "richhickey"]
   [22 :user/name "tonsky"]
   [33 :user/name "pithyless"]])

(def q
  [:find '?name
   :where
   [11 :user/name '?name]])

(defn parse-query
  "Returns query in map form.

  Each clause becomes a key inside map."
  [q]
  (let [find-index (.indexOf q :find)
        where-index (.indexOf q :where)
        vars (subvec q (inc find-index) where-index)
        wheres (subvec q (inc where-index))]
    {:find vars
     :where wheres}))

(defn match
  "Returns the first matching fact and where clause."
  [[e a v :as _fact] where-clauses]
  (first
   (filter
    (fn [[we wa]]
      (and (= e we)
           (= a wa)))
    where-clauses)))

(defn run [db q]
  (let [{:keys [find where]} (parse-query q)
        rf (fn [acc fact]
             (if-let [matched (match fact where)]
               (conj acc [(nth fact 2)])
               acc))]
    (reduce rf #{} db)))

(run db q)
