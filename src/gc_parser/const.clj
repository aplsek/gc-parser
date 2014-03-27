(ns gc-parser.const)

;;; CONSTANTS : Basic Patterns




; Match for pause time "0.1566980 secs]"
(def ^:constant pause-time "([\\d\\.]+) secs\\]")

; Match : "K M G B"
(def ^:constant order "[KMG]?B?")

; Match : "8944.0M"
;(def ^:constant number (str "\\d+" order  ) )
;(def ^:constant number (str "([\\d\\.]+)" order ) )
(def ^:constant number (str "([\\d\\.]+" order ")" ) )

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



;;; OUTPUT formatting:

;(def ^:constant SEP " ")
(def ^:constant SEP ",")
;(def ^:constant SEP " | ")


;; EXAMPLES:


(def ^:constant g1-evac-test "433.905: [GC pause (G1 Evacuation Pause) (young) [Eden: 894422.022MB(8944.0M)->0.0M(8944.0M) Survivors: 272.0M->296.0M Heap: 9418.3M(15.0G)->498.3M(15.0G)] [Times: user=0.71 sys=0.03, real=0.11 secs]")
(def ^:constant g1-evac-test-ok "433.905,g1evac,0.03,894422.022,8944.0,0.0,272.0,296.0,9418.3,15.0,498.3,15.0,0.71,0.03")
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



;1162.934: [GC cleanup 11G->11G(16G), 0.0170410 secs] [Times: user=0.18 sys=0.00, real=0.01 secs]
;1162.952: [GC concurrent-cleanup-start]
;1162.952: [GC concurrent-cleanup-end, 0.0001380 secs]





;;;;;;;;;;;;;;;;;;;;; Helper methods

(defn parse-int [s]
   (Integer. (re-find  #"\d+" s )))

(defn toMB 
  [x]
  (println (str "toMB:" x))
  (cond
    (.endsWith x "G") (* (parse-int x) 1000)
    (.endsWith x "K") (double (/ (parse-int x) 1000)) 
    (.endsWith x "M") (parse-int x)   
    (.endsWith x "B") (parse-int x)  
    :else x
   )
  )


