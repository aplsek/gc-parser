(ns gc-parser.core
    (:require
                [gc-parser.const :refer :all]
                [gc-parser.matcher_g1 :refer :all]
                [gc-parser.matcher_ParOld :refer :all]
                ))

(use '[clojure.string :only (join)])



(def headers_ParOld (join SEP  ["timestamp" "gc.type" "pause.time"
         "young.start" "young.end" "young.max"
 "heap.start" "heap.end" "heap.max"
     "time.user" "time.sys" "time.real"
 "old.start" "old.end" "old.max"
 "perm.start" "perm.end" "perm.max"]))

(def headers_G1 (join SEP  ["timestamp" "gc.type" "pause.time"
         "young.occ.start" "young.size.start" "young.occ.end" "young.size.end"
             "survivor.start" "survivor.end"
 "heap.occ.start" "heap.size.start" "heap.occ.end" "heap.size.end"
     "time.user" "time.sys" "time.real" ]))


(defn process-gc-file [infile outfile]
  (let [gcdata (line-seq (clojure.java.io/reader (clojure.java.io/file infile)))]
    (with-open [w (clojure.java.io/writer outfile)]
      (let [writeln (fn [x] (.write w (str x "\n")))]
        (writeln headers_G1)
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
         g1-cleanup (re-seq (gc-pattern-g1-cleanup) line)
         ]
     (when-not (nil? g1-evac)
               (println (str "match g1-evac :" g1-evac))
              ; (writeln (str "writeln test"))
               (writeln (process-g1-evac (first g1-evac)))
              ; (println (str "match g1-evtest write"))
               ;(println (str "conc cl start:" g1-conc-cl-start ))              
     )
     (when-not (nil? g1-young)
           ; (println ( str " match g1-young = " g1-young ))
            (writeln (process-g1-young (first g1-young)))
     )
     (when-not (nil? g1-mixed)
            '(println " match g1-mixed")
             (writeln (process-g1-mixed (first g1-mixed)))
     )
      (when-not (nil? g1-conc-reg-st)
           ; (println " match g1-conc-reg-st")
             (writeln (process-g1-conc-reg-start (first g1-conc-reg-st)))
     )
     (when-not (nil? g1-conc-reg-en)
     ; (println " match cocn reg en")
       (writeln (process-g1-conc-reg-end (first g1-conc-reg-en)))
     )
     (when-not (nil? g1-conc-cl-start)
     ; (println " match conc cl start")
       (writeln (process-g1-conc-cl-start (first g1-conc-cl-start)))
     )
     (when-not (nil? g1-conc-cl-end)
      (println " match conc cl end")
       (writeln (process-g1-conc-cl-end (first g1-conc-cl-end)))
     )
      (when-not (nil? g1-conc-mark-start)
      ;(println " match conc mark start")
       (writeln (process-g1-conc-mark-start (first g1-conc-mark-start)))
     )
     (when-not (nil? g1-conc-mark-end)
      (println " match g1 conc mark end")
       (writeln (process-g1-conc-mark-end (first g1-conc-mark-end)))
     )
     (when-not (nil? g1-remark)
      (println " match g1-remark")
      (writeln (process-g1-remark (first g1-remark)))
     )
      (when-not (nil? g1-cleanup)
      (println " match g1-cleanup")
      (writeln (process-g1-cleanup (first g1-cleanup)))
     )
      (println (str "end. :"))
  )
)

(defn www
  []
  (let [line "I am line"]
    (println (str "test let:" line))
    (when-not (nil? line)
      (println " match g1-mixed...")
   
     
    )
  )
)



(defn testt
  [x]
  (let [writeln java.lang.System/out]
    (resolve_line x writeln)
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


(defn toMB2
  [x]
  ;(println (str "start :" x))
  (when (.endsWith x "G")
           (* (parse-int x) 1000))
  (when (.endsWith x "M")
           (parse-int x) )
  (when (.endsWith x "K")
           (/ (parse-int x) 1000) )
  (when (.endsWith x "B")
           ((parse-int x)) )
)

(defn mmap
  [x]
   (println "mmpa test : " x)
   (let [res (map toMB x)]
     )
   res
   (println (str res))
   (println "mmpa test done")
  )


(defn conc
   [line]
   (println "Hello, World!  start :" line)
   (println (process-g1-young (first (re-seq (minor-gc-pattern-g1-young) line))))
  (println "done")
)

(defn tt
  []
  (println (str (toMB "9024.0G")))
  (println (str (toMB "9024K")))
  (println (str (toMB "9024M")))
   (println (str (toMB "9024B")))
   (println (str (toMB "g1young")))
  )

(defn ttt
  []
  (let [res (map toMB ["9024.0G" "9024.0M" "9024.0K"] )]
  (println (str res ))
  res
  )
)


;(www)

;(conc "755.441: [GC pause (young), 0.4418240 secs] [Eden: 9024.0M(9024.0M)->0.0B(8384.0M) Survivors: 800.0M->1248.0M Heap: 13.2G(16.0G)->5072.0M(16.0G)] [Times: user=5.41 sys=0.01, real=0.44 secs]")

;(tt)
;(ttt)

;-----------------------------------------------------------------------
; Convert Java GC log csv format
;-----------------------------------------------------------------------
(process-gc-file "input/gc3.log" "data.txt")
;(process-gc-file "input/gc.log.stripped" "data.txt")

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

;(testt G1_MIXED_TEST)

;(testt G1_CONCURRENT_REG_START_TEST )
;(testt G1_CONCURRENT_REG_END_TEST )


;(testt G1_REMARK_TEST)
;G1_CONC_MARK_ST_TEST

; TODO:
;(testt G1_REMARK_TEST)

;(test-let "433.905: [GC pause (G1 Evacuation Pause) (young)")(ns gc-parser.core
    (:require
                [gc-parser.const :refer :all]
                [gc-parser.matcher_g1 :refer :all]
                [gc-parser.matcher_ParOld :refer :all]
                ))

(use '[clojure.string :only (join)])



(def headers_ParOld (join SEP  ["timestamp" "gc.type" "pause.time"
         "young.start" "young.end" "young.max"
 "heap.start" "heap.end" "heap.max"
     "time.user" "time.sys" "time.real"
 "old.start" "old.end" "old.max"
 "perm.start" "perm.end" "perm.max"]))

(def headers_G1 (join SEP  ["timestamp" "gc.type" "pause.time"
         "young.occ.start" "young.size.start" "young.occ.end" "young.size.end"
             "survivor.start" "survivor.end"
 "heap.occ.start" "heap.size.start" "heap.occ.end" "heap.size.end"
     "time.user" "time.sys" "time.real" ]))


(defn process-gc-file [infile outfile]
  (let [gcdata (line-seq (clojure.java.io/reader (clojure.java.io/file infile)))]
    (with-open [w (clojure.java.io/writer outfile)]
      (let [writeln (fn [x] (.write w (str x "\n")))]
        (writeln headers_G1)
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
         g1-cleanup (re-seq (gc-pattern-g1-cleanup) line)
         ]
     (when-not (nil? g1-evac)
               (println (str "match g1-evac :" g1-evac))
              ; (writeln (str "writeln test"))
               (writeln (process-g1-evac (first g1-evac)))
              ; (println (str "match g1-evtest write"))
               ;(println (str "conc cl start:" g1-conc-cl-start ))              
     )
     (when-not (nil? g1-young)
           ; (println ( str " match g1-young = " g1-young ))
            (writeln (process-g1-young (first g1-young)))
     )
     (when-not (nil? g1-mixed)
            '(println " match g1-mixed")
             (writeln (process-g1-mixed (first g1-mixed)))
     )
      (when-not (nil? g1-conc-reg-st)
           ; (println " match g1-conc-reg-st")
             (writeln (process-g1-conc-reg-start (first g1-conc-reg-st)))
     )
     (when-not (nil? g1-conc-reg-en)
     ; (println " match cocn reg en")
       (writeln (process-g1-conc-reg-end (first g1-conc-reg-en)))
     )
     (when-not (nil? g1-conc-cl-start)
     ; (println " match conc cl start")
       (writeln (process-g1-conc-cl-start (first g1-conc-cl-start)))
     )
     (when-not (nil? g1-conc-cl-end)
      (println " match conc cl end")
       (writeln (process-g1-conc-cl-end (first g1-conc-cl-end)))
     )
      (when-not (nil? g1-conc-mark-start)
      ;(println " match conc mark start")
       (writeln (process-g1-conc-mark-start (first g1-conc-mark-start)))
     )
     (when-not (nil? g1-conc-mark-end)
      (println " match g1 conc mark end")
       (writeln (process-g1-conc-mark-end (first g1-conc-mark-end)))
     )
     (when-not (nil? g1-remark)
      (println " match g1-remark")
      (writeln (process-g1-remark (first g1-remark)))
     )
      (when-not (nil? g1-cleanup)
      (println " match g1-cleanup")
      (writeln (process-g1-cleanup (first g1-cleanup)))
     )
      (println (str "end. :"))
  )
)

(defn www
  []
  (let [line "I am line"]
    (println (str "test let:" line))
    (when-not (nil? line)
      (println " match g1-mixed...")
   
     
    )
  )
)



(defn testt
  [x]
  (let [writeln java.lang.System/out]
    (resolve_line x writeln)
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


(defn toMB2
  [x]
  ;(println (str "start :" x))
  (when (.endsWith x "G")
           (* (parse-int x) 1000))
  (when (.endsWith x "M")
           (parse-int x) )
  (when (.endsWith x "K")
           (/ (parse-int x) 1000) )
  (when (.endsWith x "B")
           ((parse-int x)) )
)

(defn mmap
  [x]
   (println "mmpa test : " x)
   (let [res (map toMB x)]
     )
   res
   (println (str res))
   (println "mmpa test done")
  )


(defn conc
   [line]
   (println "Hello, World!  start :" line)
   (println (process-g1-young (first (re-seq (minor-gc-pattern-g1-young) line))))
  (println "done")
)

(defn tt
  []
  (println (str (toMB "9024.0G")))
  (println (str (toMB "9024K")))
  (println (str (toMB "9024M")))
   (println (str (toMB "9024B")))
   (println (str (toMB "g1young")))
  )

(defn ttt
  []
  (let [res (map toMB ["9024.0G" "9024.0M" "9024.0K"] )]
  (println (str res ))
  res
  )
)


;(www)

;(conc "755.441: [GC pause (young), 0.4418240 secs] [Eden: 9024.0M(9024.0M)->0.0B(8384.0M) Survivors: 800.0M->1248.0M Heap: 13.2G(16.0G)->5072.0M(16.0G)] [Times: user=5.41 sys=0.01, real=0.44 secs]")

;(tt)
;(ttt)

;-----------------------------------------------------------------------
; Convert Java GC log csv format
;-----------------------------------------------------------------------
(process-gc-file "input/gc3.log" "data.txt")
;(process-gc-file "input/gc.log.stripped" "data.txt")

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

;(testt G1_MIXED_TEST)

;(testt G1_CONCURRENT_REG_START_TEST )
;(testt G1_CONCURRENT_REG_END_TEST )


;(testt G1_REMARK_TEST)
;G1_CONC_MARK_ST_TEST

; TODO:
;(testt G1_REMARK_TEST)

;(test-let "433.905: [GC pause (G1 Evacuation Pause) (young)")