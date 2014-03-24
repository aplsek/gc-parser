(ns gc-parser.regex)

(use '[clojure.string :only (join)])

; Use when-let to test
;[Times: user=0.25 sys=0.02, real=0.24 secs] 
(def ^:constant pause-time "([\\d\\.]+) secs\\]")
(def ^:constant space "(\\d+)K->(\\d+)K\\((\\d+)K\\)")
(def ^:constant exec-stat " \\[Times: user=([\\d\\.]+) sys=([\\d\\.]+), real=([\\d\\.]+) secs\\]")


(def ^:constant space-heap (str "Heap: " space))


(defn minor-gc-pattern-g1-evac []
   (let [timestamp  "([\\d\\.]+): GC "
         heap       (str "Heap: " space)]
     (println "[dbg] - minor-gc-pattern-g1-evac")
     (println (str timestamp heap))
    (re-pattern (str timestamp heap))))

(defn minor-gc-pattern []
    (let [timestamp  "([\\d\\.]+): \\[GC .*\\[PS.*: "
          young-gen   (str space "] ")
          heap       (str space ", ")]
         (re-pattern (str timestamp young-gen  heap pause-time exec-stat))))     

(defn process-g1-evac [entry]
  (let [[a ts ys ye ym hs he hm pt ut kt rt & e] entry]
    (println (str "ENTRY: " entry))
    (println (str "   " ))
    (join \, [ts "g1evac" pt
              ys ye ym
              hs he hm
              ut kt rt])))

(defn process-minor-gc [entry]
    (let [[a ts ys ye ym hs he hm pt ut kt rt & e] entry]
	  (println (str "process minor: " entry "  END."))
      (join \, [ts "minor" pt 
		        ys ye ym 
				hs he hm 
				ut kt rt])))





(defn testt
  [line]
    (println "test :")
   (let [g1-evac  (re-seq (minor-gc-pattern-g1-evac) line)
         minor-gc (re-seq (minor-gc-pattern) line)]
     (println "test :")
      (println (str "str :" g1-evac))
      (when-not (nil? minor-gc)
              ( (println " match minor-gc!!!!!")
                (println (process-minor-gc (first minor-gc))))
     )
      (when-not (nil? g1-evac)
              ( (println " match g1-evac!!!!!")
                (println (process-g1-evac (first g1-evac))))
     )
      (println (str "end. :"))
  )
)

(defn test-let
  [x]
  (let [[a ts ys ye] x]
    (println (str "test let:" a ts ys ye))
    )
  )

(testt "1123.4: GC Heap: 524288K->32124K(611648K)")

;(testt "212.785: [GC [PSYoungGen: 524288K->32124K(611648K)] 524288K->32124K(2009792K), 0.1566980 secs] [Times: user=0.24 sys=0.06, real=0.16 secs] ")
