(ns gc-parser.hello 
    (:require 
                [gc-parser.const :refer :all]
                ))

(use '[clojure.string :only (join)])




; Match for pause time "0.1566980 secs]"
(def ^:constant pause-time "([\\d\\.]+) secs\\]")

; Match : "K M G B"
(def ^:constant order "[KMG]?B?")

; Match : "8944.0M"
;(def ^:constant number (str "\\d+" order  ) )
(def ^:constant number (str "([\\d\\.]+)" order ) )

; Match for Java heap space stat "524288K->32124K(2009792K)"
(def ^:constant space "(\\d+)K->(\\d+)K\\((\\d+)K\\)")

; Match for Java heap space stat "8944.0M(8944.0M)->0.0B(8920.0M)"
; "([\\d\\.]+)K->([\\d\\.]+)K")
;
(def ^:constant space-g1-simple  (str number "->" number))

;Match for Java heap space stat "8944.0M(8944.0M)->0.0B(8920.0M)"
; "([\\d\\.]+)K\\(([\\d\\.]+)K\\)->([\\d\\.]+)K\\(([\\d\\.]+)K\\)")
;
;(def ^:constant space-g1 (str "number\\(number\\)" "->" number"\\(" number "\\)"))
(def ^:constant space-g1 (str number "\\(" number "\\)" "->" number "\\(" number "\\)"))

  
; Match "Survivors: 272.0M->296.0M"
(def ^:constant space-surv (str "Survivors: " space-g1-simple))

; Match "Heap: 9418.3M(15.0G)->498.3M(15.0G)"
(def ^:constant space-eden (str "Eden: " space-g1))

; Match "Heap: 9418.3M(15.0G)->498.3M(15.0G)"
(def ^:constant space-heap (str "Heap: " space-g1))

; Match for Execution stat "[Times: user=0.24 sys=0.06, real=0.16 secs]"
(def ^:constant exec-stat " \\[Times: user=([\\d\\.]+) sys=([\\d\\.]+), real=([\\d\\.]+) secs\\]")

;  433.905: [GC pause (G1 Evacuation Pause) (young) [Eden: 8944.0M(8944.0M)->0.0B(8920.0M) Survivors: 272.0M->296.0M Heap: 9418.3M(15.0G)->498.3M(15.0G)] [Times: user=0.71 sys=0.03, real=0.11 secs]

(defn minor-gc-pattern-g1-evac []
   (let [timestamp  "([\\d\\.]+): \\[GC pause \\(G1 Evacuation Pause\\) \\(young\\)"
         eden        (str " \\[" space-eden " ")
         survivor    (str space-surv " ")
         heap        (str space-heap "\\]")]
     ;(println "[dbg] - minor-gc-pattern-g1-evac - aaa")
     ;(println (str timestamp eden survivor heap exec-stat))
    (re-pattern (str timestamp eden survivor heap exec-stat))
    ))
;  eden survivor heap exec-stat

; G1 young
; 755.441: [GC pause (young), 0.4418240 secs] [Eden: 9024.0M(9024.0M)->0.0B(8384.0M)
;   Survivors: 800.0M->1248.0M Heap:
;   13.2G(16.0G)->5072.0M(16.0G)] [Times: user=5.41 sys=0.01, real=0.44
;   secs]
;
(defn minor-gc-pattern-g1-young []
   (let [timestamp  "([\\d\\.]+): \\[GC pause \\(young\\), "
        eden        (str " \\[" space-eden)
        survivor    (str " " space-surv " ")
        heap        (str space-heap "\\]")]
     (println (str timestamp pause-time eden survivor heap exec-stat))
    (re-pattern (str timestamp pause-time eden survivor heap exec-stat))))
;timestamp pause-time eden survivor heap exec-stat

; Example Minor GC entry		 
; 212.785: [GC [PSYoungGen: 524288K->32124K(611648K)] 524288K->32124K(2009792K), 0.1566980 secs] [Times: user=0.24 sys=0.06, real=0.16 secs] 
; Define regex pattern to parse young gen GC event
(defn minor-gc-pattern []
    (let [timestamp  "([\\d\\.]+): \\[GC .*\\[PS.*: "
          young-gen   (str space "] ")
          heap       (str space ", ")]
         (re-pattern (str timestamp young-gen heap pause-time exec-stat))))                

; Example Full GC entry		 
;"43587.513: [Full GC (System) [PSYoungGen: 964K->0K(598912K)] [PSOldGen: 142673K->120674K(1398144K)] 143637K->120674K(1997056K) [PSPermGen: 82179K->82179K(147520K)], 0.7556570 secs] [Times: user=0.76 sys=0.00, real=0.77 secs]"
; Define the regex pattern to parse each line in gc log
(defn full-gc-pattern []
    (let [timestamp   "([\\d\\.]+): \\[Full.*"
          young-gen  (str ": " space "]")
          old-gen    (str " \\[\\w+: " space "\\] ")
          perm-gen   (str " \\[\\w+: "space "\\], ")
          heap       space]
         (re-pattern (str timestamp 
		                young-gen 
						old-gen 
						heap 
						perm-gen 
						pause-time 
						exec-stat))))


;433.905: 
;  yob  youngGen occupation :8944.0M
;  ysb YoungGen size before: (8944.0M)
;  yoa  Young gen occupation after :->0.0B
;  ysa Yougn Gen size after: (8920.0M) 
;  sb Survivors occupation before: 272.0M->
;  sa Survivor occupation after:  296.0M
;  hob  Heap occ before: 9418.3M
;  hsb heap size before (15.0G)
;  hoa heap occ after -> 498.3M(
;  hsa heap size after 15.0G)] 
;  ut  user time [Times: user=0.71
;  kt sys time  sys=0.03,
;  rt real time : real=0.11 secs]
; PauseTime = G1 Evac does not report pause time separately, only in Times
(defn process-g1-evac [entry]
  (let [[a ts yob ysb yoa ysa sb sa hob hoa hsa ut kt rt & e] entry]
    ;(println (str "Hello process-g1-evac:" entry))
    ;(println (str "   " ))
    (join \, [ts "g1evac" rt yob ysb yoa sb sa hob hoa hsa ut kt rt])))



(defn process-g1-young [entry]
  (let [[a ts yob ysb yoa ysa sb sa hob hoa hsa ut kt rt & e] entry]
    (println (str "Hello process-g1-young:" entry))
    (println (str "   " ))
    (join \, [ts "g1young" rt yob ysb yoa sb sa hob hoa hsa ut kt rt])))

;;;
;
;
;
;
;




(defn process-gc-file [infile outfile]
  (let [gcdata (line-seq (clojure.java.io/reader (clojure.java.io/file infile)))]
    (with-open [w (clojure.java.io/writer outfile)]
      (let [writeln (fn [x] (.write w (str x "\n")))]
        (doseq [line gcdata]
          (let [
                g1-evac  (re-seq (minor-gc-pattern-g1-evac) line)]
            (when-not (nil? g1-evac)
              ( (println " match g1-evac")
                writeln (process-g1-evac (first g1-evac))))
            ))))))



(defn testt
  [line]
    (println "test :")
   (let [g1-evac  (re-seq (minor-gc-pattern-g1-evac) line)
         g1-young (re-seq (minor-gc-pattern-g1-young) line)
         ]
     (println "test :" g1-young)
      (when-not (nil? g1-evac)
              ( (println " match g1-evac!!!!!")
                (println (process-g1-evac (first g1-evac))))
     )
       (when-not (nil? g1-young)
             ( (println " match g1-young!!!!!")
               (println (process-g1-evac (first g1-young))))
     )
      (println (str "end. :"))
  )
)

(defn testt2
  [line]
    (println "test :")
   (let [g1-evac  (re-seq (minor-gc-pattern-g1-evac) line)
         g1-young (re-seq (minor-gc-pattern-g1-young) line)
         ]
     (println "test :" g1-evac)
      (when-not (nil? g1-evac)
                (println (process-g1-evac (first g1-evac))))
     
       (when-not (nil? g1-young)
               (println (process-g1-young (first g1-young))))
     
      (println (str "end. :"))
  )
)

;([\d\.]+): \[GC pause \(G1 Evacuation Pause\) \(young\) \[Eden: ([\d\.]+)[KMG]B?\(([\d\.]+)[KMG]B?\)->([\d\.]+)[KMG]B?\(([\d\.]+)[KMG]B?\) Survivors: ([\d\.]+)[KMG]B?->([\d\.]+)[KMG]B? Heap: ([\d\.]+)[KMG]B?\(([\d\.]+)[KMG]B?\)->([\d\.]+)[KMG]B?\(([\d\.]+)[KMG]B?\)]

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



;-----------------------------------------------------------------------
; Convert Java GC log csv format
;-----------------------------------------------------------------------
;(process-gc-file "gc.log" "data.csv")
;(process-gc-file "gc.log" "data.csv")
; 433.905: [GC pause (G1 Evacuation Pause) (young) [Eden: 8944.0M(8944.0M)->0.0B(8920.0M) Survivors: 272.0M->296.0M Heap: 9418.3M(15.0G)->498.3M(15.0G)] [Times: user=0.71 sys=0.03, real=0.11 secs]

;(def ^:constant g1-evac "433.905: [GC pause (G1 Evacuation Pause) (young) [Eden: 894422.022MB(8944.0M)->0.0M(8944.0M) Survivors: 272.0M->296.0M Heap: 9418.3M(15.0G)->498.3M(15.0G)] [Times: user=0.71 sys=0.03, real=0.11 secs]")
;(def ^:constant g1-evac-ok "433.905,g1evac,0.03,894422.022,8944.0,0.0,272.0,296.0,9418.3,15.0,498.3,15.0,0.71,0.03")
;(def ^:constant g1-young "755.441: [GC pause (young), 0.4418240 secs] [Eden: 9024.0M(9024.0M)->0.0B(8384.0M) Survivors: 800.0M->1248.0M Heap: 13.2G(16.0G)->5072.0M(16.0G)] [Times: user=5.41 sys=0.01, real=0.44 secs]")
;(def ^:constant g1-young-ok "755.441,g1minor,,,,")
;(def ^:constant g1-young-s "755.441: [GC pause (young), 0.4418240 secs] [Eden: 9024.0M(9024.0M)->0.0B(8384.0M) Survivors: 800.0M->1248.0M Heap: 13.2G(16.0G)->5072.0M(16.0G)] [Times: user=5.41 sys=0.01, real=0.44 secs]")


(testt g1-evac)

(testt g1-young)

;(test-let "433.905: [GC pause (G1 Evacuation Pause) (young)")

