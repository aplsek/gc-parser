(ns gc-parser.const)

(use '[clojure.string :only (join)])


;(def ^:constant SEP " ")
(def ^:constant SEP ",")
(def ^:constant UNDERSCORE "_")
;(def ^:constant SEP " | ")

;;; CONSTANTS : Basic Patterns


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
             "old.occ.start" "old.occ.end" "old.size.start" "old.size.end" 
             "promoRate"
             "time.user" "time.sys" "time.real" ]))


(def ^:constant SPACE " ")

; Match for Timestamp: "2273.426:"
(def ^:constant timestamp-pattern "([\\d\\.]+):")


; Match for pause time "0.1566980 secs]"
(def ^:constant pause-time "([\\d\\.]+) secs\\]")

; Match : "K M G B"
(def ^:constant order "[KMG]?B?")

; Match : "8944.0M"
;(def ^:constant number (str "\\d+" order  ) )
;(def ^:constant number (str "([\\d\\.]+)" order ) )
(def ^:constant number (str "([\\d\\.]+" order ")" ) )

; Match for Java heap space stat "524288K->32124K(2009792K)"
(def ^:constant space (str "(\\d+)" order  "->(\\d+)" order "\\((\\d+)" order "\\)"))

; Match for Java heap space stat "8944.0M(8944.0M)->0.0B(8920.0M)"
; "([\\d\\.]+)K->([\\d\\.]+)K")
;
(def ^:constant space-g1-simple  (str number "->" number))

;Match for Java heap space stat "8944.0M(8944.0M)->0.0B(8920.0M)"
; "([\\d\\.]+)K\\(([\\d\\.]+)K\\)->([\\d\\.]+)K\\(([\\d\\.]+)K\\)")
;
;(def ^:constant space-g1 (str "number\\(number\\)" "->" number"\\(" number "\\)"))
(def ^:constant space-g1 (str number "\\(" number "\\)" "->" number "\\(" number "\\)"))

(def ^:constant space-g1-cleanup (str number "->" number "\\(" number "\\)"))


; Match "Survivors: 272.0M->296.0M"
(def ^:constant space-surv (str "Survivors: " space-g1-simple))

; Match "Heap: 9418.3M(15.0G)->498.3M(15.0G)"
(def ^:constant space-eden (str "Eden: " space-g1))

; Match "Heap: 9418.3M(15.0G)->498.3M(15.0G)"
(def ^:constant space-heap (str "Heap: " space-g1))

; Match for Execution stat "[Times: user=0.24 sys=0.06, real=0.16 secs]"
(def ^:constant exec-stat " \\[Times: user=([\\d\\.]+) sys=([\\d\\.]+), real=([\\d\\.]+) secs\\]")

;  433.905: [GC pause (G1 Evacuation Pause) (young) [Eden: 8944.0M(8944.0M)->0.0B(8920.0M) Survivors: 272.0M->296.0M Heap: 9418.3M(15.0G)->498.3M(15.0G)] [Times: user=0.71 sys=0.03, real=0.11 secs]


;;;; G1 events / phases

;(def ^:constant g1-evacuation-event     (str "G1 Evacuation Pause"))
;(def ^:constant g1-young-event          (str "young"))
;(def ^:constant g1-initial-mark-event   (str "initial-mark"))
;(def ^:constant g1-mixed-event          (str "mixed"))
;(def ^:constant g1-tospace-exhast-event (str "to-space exhausted"))

(def ^:constant g1-evacuation-event     (str "G1 Evacuation Pause"))
(def ^:constant g1-young-event          (str "young"))
(def ^:constant g1-initial-mark-event   (str "initial-mark"))
(def ^:constant g1-mixed-event          (str "mixed"))
(def ^:constant g1-tospace-exhast-event (str "to-space exhausted"))



(def ^:constant g1_event_pattern2 (str "("
                                       "\\(" g1-young-event "\\)" "|" 
                                       "\\(" g1-initial-mark-event "\\)" "|" 
                                       "\\(" g1-mixed-event  "\\)" "|" 
                                       "\\(" g1-tospace-exhast-event "\\)" "|"
                                       SPACE
                                       ")+"))

(def ^:constant g1_event_pattern (str "(\\(young\\)|\\(to-space\\ exhausted\\)|\\(mixed\\)| )+" ))
;  gcevent     (str "(\\(young\\)|\\(to-space\\ exhausted\\)|\\(mixed\\)| )+"  ", ")




;;; OUTPUT formatting:





(def ^:constant TMP_GC_FILE "gc.log.tmp")



;; EXAMPLES:


(def ^:constant G1_EVAC_TEST "433.905: [GC pause (G1 Evacuation Pause) (young) [Eden: 894422.022MB(8944.0M)->0.0M(8944.0M) Survivors: 272.0M->296.0M Heap: 9418.3M(15.0G)->498.3M(15.0G)] [Times: user=0.71 sys=0.03, real=0.11 secs]")
(def ^:constant G1_EVAC_TEST_OK "433.905,g1evac,0.03,894422.022,8944.0,0.0,272.0,296.0,9418.3,15.0,498.3,15.0,0.71,0.03")
(def ^:constant G1_YOUNG_TEST "755.441: [GC pause (young), 0.4418240 secs] [Eden: 9024.0M(9024.0M)->0.0B(8384.0M) Survivors: 800.0M->1248.0M Heap: 13.2G(16.0G)->5072.0M(16.0G)] [Times: user=5.41 sys=0.01, real=0.44 secs]")
(def ^:constant G1_YOUNG_TEST_OK "755.441,g1young,5.41,0.4418240,9024.0,9024.0,8384.0,800.0,1248.0,13.2,16.0,5072.0,16.0,5.41")
(def ^:constant g1-young-s "755.441: [GC pause (young), 0.4418240 secs] [Eden: 9024.0M(9024.0M)->0.0B(8384.0M) Survivors: 800.0M->1248.0M Heap: 13.2G(16.0G)->5072.0M(16.0G)] [Times: user=5.41 sys=0.01, real=0.44 secs]")

(def ^:constant G1_MIXED_TEST "1165.366: [GC pause (mixed), 0.0793930 secs] [Eden: 672.0M(672.0M)->0.0B(672.0M) Survivors: 128.0M->128.0M Heap: 7421.9M(16.0G)->5212.4M(16.0G)] [Times: user=0.90 sys=0.00, real=0.08 secs]")

(def ^:constant G1_MIXED_TEST_OK "1165.366,g1mixed,128.0,0.0793930,672.0,672.0,0.0,672.0,128.0,7421.9,16.0,5212.4")

(def ^:constant G1_CONCURRENT_REG_START_TEST "1161.747: [GC concurrent-root-region-scan-start]")
(def ^:constant G1_CONCURRENT_REG_START_OK "1161.747,g1conc-start")

(def ^:constant G1_CONCURRENT_REG_END_TEST "1162.042: [GC concurrent-root-region-scan-end, 0.2950840 secs]")
(def ^:constant G1_CONCURRENT_REG_END_OK "1162.042,g1conc-end,,0.2950840")

(def ^:constant G1_CLEANUP_TEST "1162.934: [GC cleanup 11G->11G(16G), 0.0170410 secs] [Times: user=0.18 sys=0.00, real=0.01 secs]")
(def ^:constant G1_CLEANUP_OK "1162.042,g1cleanup,,0.2950840")

(def ^:constant G1_CONC_CLEAN_END_TEST "1162.952: [GC concurrent-cleanup-end, 0.0001380 secs]")
(def ^:constant G1_CONC_CLEAN_END_OK "1162.042,g1conc-end,,0.2950840")

(def ^:constant G1_CONC_CLEAN_START_TEST "1162.952: [GC concurrent-cleanup-start]")
(def ^:constant G1_CONC_CLEAN_START_OK "1162.042,g1conc-end,,0.2950840")


(def ^:constant G1_CONC_MARK_START_TEST "1162.042: [GC concurrent-mark-start]")

(def ^:constant G1_CONC_MARK_END_TEST "1162.842: [GC concurrent-mark-end, 0.8000300 secs]")
(def ^:constant G1_CONC_MARK_END_TEST_OK "")


(def ^:constant G1_REMARK_TEST "1162.844: [GC remark 1162.846: [GC ref-proc, 0.0147680 secs], 0.0899440 secs] [Times: user=0.14 sys=0.00, real=0.09 secs]")


(def ^:constant G1_FULL_TEST "2273.426: [Full GC 28G->2265M(28G), 10.5452700 secs] [Eden: 0.0B(24.0G)->0.0B(24.0G) Survivors: 0.0B->0.0B Heap: 28.0G(28.0G)->2265.4M(28.0G)] [Times: user=13.17 sys=0.23, real=10.54 secs]")
(def ^:constant G1_FULL_TEST_OK "273.426,g1full,28000,0,24000,0,24000,0,0,28000,28000,2265,28000,28000,2265,4000,4000,0,13.17,0.23,10.54")

 






;;;;;;;;;;;;;;;;;;;;; Helper methods

(defn parse-int [s]
   (Integer. (re-find  #"\d+" s )))

(defn toMB
  [x]
  (cond
    (.endsWith x "G") (* (parse-int x) 1000)
    (.endsWith x "K") (double (/ (parse-int x) 1000))
    (.endsWith x "M") (parse-int x)  
    (.endsWith x "B") (parse-int x)  
    :else x
   )
  )