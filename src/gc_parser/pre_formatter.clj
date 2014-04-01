(ns gc-parser.matcher_g1
    (:require
                [gc-parser.const :refer :all]
                ))

;(use '[clojure.string :only (join)])

(use '[clojure.string])


(defn minor-gc-pattern-g1-line []
   (let [gcline  "\\[GC pause|\\[Eden"
         ]
     ;(println "[dbg] - minor-gc-pattern-g1-evac - aaa")
     ;(println (str timestamp eden survivor heap exec-stat))
    (re-pattern (str gcline))
    ))

(defn minor-gc-pattern-g1-time []
   (let [gcline  "\\[Time"
         ]
     ;(println "[dbg] - minor-gc-pattern-g1-evac - aaa")
     ;(println (str timestamp eden survivor heap exec-stat))
    (re-pattern (str gcline))
    ))

(defn pattern-par-old-gc []
   (let [gcline  "\\[PSYoungGen:|\\[Full GC"
         ]
     ;(println "[dbg] - minor-gc-pattern-g1-evac - aaa")
     ;(println (str timestamp eden survivor heap exec-stat))
    (re-pattern (str gcline))
    ))


(defn filter_line
  [line write]
   (let [g1line  (re-seq (minor-gc-pattern-g1-line) line)
         g1time (re-seq (minor-gc-pattern-g1-time) line)
         gParOld (re-seq (pattern-par-old-gc) line)
         ]
     (when-not (nil? g1line)
              (println (str "match g1-evac :" g1line))
              (write (str (trim line) " " ))
              ; (writeln (str "writeln test"))        
     )
     (when-not (nil? g1time)
            (println ( str " g1-time = " g1time ))
         (write (trim line))
         (write "\n")
     )
      (when-not (nil? gParOld)

         (write gParOld)
         (write "\n")
     )
    
      (println (str "end. :"))
      ;; TODO - process the line and report if there is no match!!
  )
)

(defn process-gc-file-preformat [infile outfile]
  (let [gcdata (line-seq (clojure.java.io/reader (clojure.java.io/file infile)))]
    (with-open [w (clojure.java.io/writer "gc.log.tmp")]
      (let [write (fn [x] (.write w (str x)))]
        (doseq [line gcdata]
          (filter_line line write))))))


(process-gc-file-preformat "input/g1gc.log" "out" )