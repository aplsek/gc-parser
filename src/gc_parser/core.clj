(ns gc-parser.core
    (:require
                [gc-parser.const :refer :all]
                [gc-parser.matcher_g1 :refer :all]
                [gc-parser.matcher_ParOld :refer :all]
                [gc-parser.pre_formatter :refer :all]
                ))

(use '[clojure.string :only (join)])



(defn getG1PhaseName [line]
  (str G1 (clojure.string/replace (clojure.string/join UNDERSCORE line) SPACE "" )) 
)


(defn resolve_line
  [line writeln]
   (let [g1-evac  (re-seq (minor-gc-pattern-g1-evac) line)
         g1-young (re-seq (minor-gc-pattern-g1-young) line)
         g1-conc-reg-st  (re-seq (gc-pattern-g1-conc-reg-start) line)
         g1-conc-reg-en  (re-seq (gc-pattern-g1-conc-reg-end) line)
         g1-conc-cl-start (re-seq (gc-pattern-g1-conc-cl-start) line)
         g1-conc-cl-end (re-seq (gc-pattern-g1-conc-cl-end) line)
         g1-conc-mark-start (re-seq (gc-pattern-g1-conc-mark-start) line)
         g1-conc-mark-end (re-seq (gc-pattern-g1-conc-mark-end) line)
         g1-remark (re-seq (gc-pattern-g1-remark) line)
         g1-cleanup (re-seq (gc-pattern-g1-cleanup) line)
         minor-gc (re-seq (minor-gc-pattern) line)
				 full-gc (re-seq (full-gc-pattern) line)
         full-gc-meta (re-seq (full-gc-pattern-meta) line)
         g1full (re-seq (g1-full-pattern) line)
         g1event  (re-seq (g1-event-pattern) line)
         ]
     (when (nil? line) ())
     (when-not (nil? g1-evac)
                (writeln (process-g1-evac (getG1PhaseName g1event) (map toMB (first g1-evac))))
     )
     (when-not (nil? g1-young)
            (writeln (process-g1-event (getG1PhaseName g1event) (map toMB (first g1-young))))
     )
     (when-not (nil? g1full)
       (writeln (process-g1-full "g1full" (map toMB (first g1full))))
     )
      (when-not (nil? g1-conc-reg-st)
            (writeln (process-g1-conc-reg-start (first g1-conc-reg-st)))
     )
     (when-not (nil? g1-conc-reg-en)
       (writeln (process-g1-conc-reg-end (first g1-conc-reg-en)))
     )
     (when-not (nil? g1-conc-cl-start)
       (writeln (process-g1-conc-cl-start (first g1-conc-cl-start)))
     )
     (when-not (nil? g1-conc-cl-end)
       (writeln (process-g1-conc-cl-end (first g1-conc-cl-end)))
     )
      (when-not (nil? g1-conc-mark-start)
       (writeln (process-g1-conc-mark-start (first g1-conc-mark-start)))
     )
     (when-not (nil? g1-conc-mark-end)
       (writeln (process-g1-conc-mark-end (first g1-conc-mark-end)))
     )
     (when-not (nil? g1-remark)
       (writeln (process-g1-remark (first g1-remark)))
     )
     (when-not (nil? g1-cleanup)
       (writeln (map toMB (first g1-cleanup))))
     (when-not (nil? full-gc) 
				(writeln (process-full-gc (map toMB (first full-gc)))))
     (when-not (nil? full-gc-meta) 
				(writeln (process-full-gc-meta (map toMB (first full-gc-meta)))))
		 (when-not (nil? minor-gc) 
        (writeln (process-minor-gc (map toMB (first minor-gc)))))
     ;(println (str "ERR :" line))
      ;; TODO - process the line and report if there is no match!!
      ;;
  )
)




(defn process-gc-file [infile outfile]
  (let [gcdata (line-seq (clojure.java.io/reader (clojure.java.io/file infile)))]
    (with-open [w (clojure.java.io/writer outfile)]
      (let [writeln (fn [x] (.write w (str x NEWLINE)))]
        (writeln headers_ALL_GC_TYPES)
        (doseq [line gcdata]
            ;(println (str "line=" line))
          (resolve_line line writeln))))))


;(process-gc-file-preformat "input/gc.parOld-test.log" TMP_GC_FILE )
;(process-gc-file TMP_GC_FILE "data.txt")

;(process-gc-file-preformat "input/parOld-jdk8.log" TMP_GC_FILE )
;(process-gc-file TMP_GC_FILE "data.txt")


;(process-gc-file-preformat "input/gc.G1.log" TMP_GC_FILE )
;(process-gc-file TMP_GC_FILE "data.txt")

;(process-gc-file-preformat "input/gc3.log" TMP_GC_FILE )
;(process-gc-file TMP_GC_FILE "data.txt")
