(ns gc-parser.core
    (:require 
                [gc-parser.const :refer :all]
                [gc-parser.matcher_g1 :refer :all]
                [gc-parser.matcher_ParOld :refer :all]
                ))

(use '[clojure.string :only (join)])



(defn process-gc-file [infile outfile]
  (let [gcdata (line-seq (clojure.java.io/reader (clojure.java.io/file infile)))]
    (with-open [w (clojure.java.io/writer outfile)]
      (let [writeln (fn [x] (.write w (str x "\n")))]
        (doseq [line gcdata]
          (resolve_line line writeln))))))


;;;;;
;
;
; TODO: add ParOld processing
;
(defn resolve_line
  [line writeln]
   (let [g1-evac  (re-seq (minor-gc-pattern-g1-evac) line)
         g1-young (re-seq (minor-gc-pattern-g1-young) line)
         g1-mixed (re-seq (gc-pattern-g1-mixed) line)
         g1-conc-reg-st  (re-seq (gc-pattern-g1-conc-reg-start) line)
         g1-conc-reg-en  (re-seq (gc-pattern-g1-conc-reg-end) line)
         g1-conc-cl-start (re-seq (gc-pattern-g1-conc-cl-start) line)
         g1-conc-cl-end (re-seq (gc-pattern-g1-conc-cl-end) line)
         g1-conc-mark-start (re-seq (gc-pattern-g1-conc-mark-start) line)
         g1-conc-mark-end (re-seq (gc-pattern-g1-conc-mark-end) line)
         g1-remark (re-seq (gc-pattern-g1-remark) line)
         g1-clanup (re-seq (gc-pattern-g1-cleanup) line)
         ]
     (when-not (nil? g1-evac)
               (println (str "match g1-evac!!!!! :" g1-evac))
              ; (writeln (str "writeln test"))
               (writeln (process-g1-evac (first g1-evac)))
               (println (str "match g1-evtest write"))
               (println (str "conc cl start:" g1-conc-cl-start ))
               
     )
     (when-not (nil? g1-young)
            (println ( str " match g1-young = " g1-young ))
            (writeln (process-g1-young (first g1-young)))
     )
     (when-not (nil? g1-mixed)
            (println " match g1-mixed!!!!!")
             (writeln (process-g1-mixed (first g1-mixed)))
     )
      (when-not (nil? g1-conc-reg-st)
            (println " match g1-mixed!!!!!")
             (writeln (process-g1-conc-reg-start (first g1-conc-reg-st)))
     )
     (when-not (nil? g1-conc-reg-en)
      (println " match cocn reg en")
       (writeln (process-g1-conc-reg-end (first g1-conc-reg-en)))
     )
     (when-not (nil? g1-conc-cl-start)
      (println " match conc cl start")
       (writeln (process-g1-conc-cl-start (first g1-conc-cl-start)))
     )
     (when-not (nil? g1-conc-cl-end)
      (println " match conc cl end")
       (writeln (process-g1-conc-cl-end (first g1-conc-cl-end)))
     )
      (when-not (nil? g1-conc-mark-start)
      (println " match conc mark end")
       (writeln (process-g1-conc-mark-start (first g1-conc-mark-start)))
     )
     (when-not (nil? g1-conc-mark-end)
      (println " match g1 conc mark st")
       (writeln (process-g1-conc-mark-end (first g1-conc-mark-end)))
     )
     (when-not (nil? g1-remark)
      (println " match g1-remark")
      (writeln (process-g1-remark (first g1-remark)))
     )
      (println (str "end. :"))
  )
)

(defn www
  []
  (let [line "I am line"]
    (println (str "test let:" line))
    (when-not (nil? line)
      (println " match g1-mixed!!!!!")
    
     
    )
  )
)

(defn test-let
  [x]
  (let [[a ts ys ye] x]
    (println (str "test let:" a ts ys ye))
    )
  )

(defn main
  "I don't do a whole lot."
  [& args]
  (println "Hello, World!  start :")
  (process-gc-file "input/one.log" "data.txt")
  )


;(www)


;-----------------------------------------------------------------------
; Convert Java GC log csv format
;-----------------------------------------------------------------------
(process-gc-file "input/gc3.log" "data.txt")

;(process-gc-file "gc.log" "data.csv")
;(process-gc-file "gc.log" "data.csv")
; 433.905: [GC pause (G1 Evacuation Pause) (young) [Eden: 8944.0M(8944.0M)->0.0B(8920.0M) Survivors: 272.0M->296.0M Heap: 9418.3M(15.0G)->498.3M(15.0G)] [Times: user=0.71 sys=0.03, real=0.11 secs]

;(def ^:constant g1-evac "433.905: [GC pause (G1 Evacuation Pause) (young) [Eden: 894422.022MB(8944.0M)->0.0M(8944.0M) Survivors: 272.0M->296.0M Heap: 9418.3M(15.0G)->498.3M(15.0G)] [Times: user=0.71 sys=0.03, real=0.11 secs]")
;(def ^:constant g1-evac-ok "433.905,g1evac,0.03,894422.022,8944.0,0.0,272.0,296.0,9418.3,15.0,498.3,15.0,0.71,0.03")
;(def ^:constant g1-young "755.441: [GC pause (young), 0.4418240 secs] [Eden: 9024.0M(9024.0M)->0.0B(8384.0M) Survivors: 800.0M->1248.0M Heap: 13.2G(16.0G)->5072.0M(16.0G)] [Times: user=5.41 sys=0.01, real=0.44 secs]")
;(def ^:constant g1-young-ok "755.441,g1minor,,,,")
;(def ^:constant g1-young-s "755.441: [GC pause (young), 0.4418240 secs] [Eden: 9024.0M(9024.0M)->0.0B(8384.0M) Survivors: 800.0M->1248.0M Heap: 13.2G(16.0G)->5072.0M(16.0G)] [Times: user=5.41 sys=0.01, real=0.44 secs]")


;(testt g1-evac)

;(testt G1_YOUNG_TEST)

;(testt G1_CONCURRENT_REG_START_TEST )

;(testt G1_CONCURRENT_REG_END_TEST )


;G1_CONC_MARK_END_TEST
;G1_CONC_MARK_ST_TEST

;(testt G1_REMART_TEST)

;(test-let "433.905: [GC pause (G1 Evacuation Pause) (young)")

