(use '[clojure.string :only (join)])

; Match for pause time "0.1566980 secs]"
(def ^:constant pause-time "([\\d\\.]+) secs\\]")


; Match : "K M G B"
(def ^:constant order "[KMG][B]?")

; Match : "8944.0M"
(def ^:constant number (str "(\\d+)" order ) )

; Match for Java heap space stat "524288K->32124K(2009792K)"
(def ^:constant space "(\\d+)K->(\\d+)K\\((\\d+)K\\)")

; Match for Java heap space stat "8944.0M(8944.0M)->0.0B(8920.0M)"
; "([\\d\\.]+)K->([\\d\\.]+)K")
;
(def ^:constant space-g1-simple  (str number "->" number))

;Match for Java heap space stat "8944.0M(8944.0M)->0.0B(8920.0M)"
; "([\\d\\.]+)K\\(([\\d\\.]+)K\\)->([\\d\\.]+)K\\(([\\d\\.]+)K\\)")
;
(def ^:constant space-g1 (str "number\\(number\\)" "->" "number\\(number\\)"))

  
; Match "Survivors: 272.0M->296.0M"
(def ^:constant space-surv (str "Survivors: " space-g1-simple))

; Match "Heap: 9418.3M(15.0G)->498.3M(15.0G)"
(def ^:constant space-eden (str "Eden: " space-g1))

; Match "Heap: 9418.3M(15.0G)->498.3M(15.0G)"
(def ^:constant space-heap (str "Heap: " space-g1))

; Match for Execution stat "[Times: user=0.24 sys=0.06, real=0.16 secs]"
(def ^:constant exec-stat " \\[Times: user=([\\d\\.]+) sys=([\\d\\.]+), real=([\\d\\.]+) secs\\]")



; G1 pauses

; G1 evacuation:
; 433.905: [GC pause (G1 Evacuation Pause) (young) [Eden:
; 8944.0M(8944.0M)->0.0B(8920.0M) Survivors: 272.0M->296.0M Heap:
; 9418.3M(15.0G)->498.3M(15.0G)] [Times: user=0.71 sys=0.03, real=0.11
; secs]
;
(defn minor-gc-pattern-g1-evac []
   (let [timestamp  "([\\d\\.]+): \\[GC pause \\(G1 Evacuation Pause\\) \\(young\\) "
        eden        (str "[" space-eden " ")
        survivor    (str space-surv " ")
        heap        (str space-heap "] ")]
    (re-pattern (str timestamp eden survivor heap exec-stat))))


; G1 young
; 755.441: [GC pause (young), 0.4418240 secs] [Eden: 9024.0M(9024.0M)->0.0B(8384.0M) Survivors: 800.0M->1248.0M Heap:
;   13.2G(16.0G)->5072.0M(16.0G)] [Times: user=5.41 sys=0.01, real=0.44
;   secs]
;
(defn minor-gc-pattern-g1-young []
   (let [timestamp  "([\\d\\.]+): \\[GC pause \\(young\\), "
        eden        (str "[" space-eden " ")
        survivor    (str space-surv " ")
        heap        (str space-heap "] ")]
    (re-pattern (str timestamp pause-time eden survivor heap exec-stat))))

; G1 mixed
; 1165.366: [GC pause (mixed), 0.0793930 secs] [Eden: 672.0M(672.0M)->0.0B(672.0M) Survivors: 128.0M->128.0M Heap:
; 7421.9M(16.0G)->5212.4M(16.0G)] [Times: user=0.90 sys=0.00, real=0.08 secs]
;
;
(defn gc-pattern-g1-mixed []
   (let [timestamp  "([\\d\\.]+): \\[GC pause \\(mixed\\), "
        eden        (str "[" space-eden " ")
        survivor    (str space-surv " ")
        heap        (str space-heap "] ")]
    (re-pattern (str timestamp pause-time eden survivor heap exec-stat))))


; 1161.747: [GC concurrent-root-region-scan-start]
;
(defn gc-pattern-g1-concurrent-root-region-scan-start []
   (let [timestamp  "([\\d\\.]+): \\[GC concurrent-root-region-scan-start\\] "]
    (re-pattern (str timestamp))))

; 1162.042: [GC concurrent-root-region-scan-end, 0.2950840 secs]
;
(defn gc-pattern-g1-concurrent-root-region-scan-end []
   (let [timestamp  "([\\d\\.]+): \\[GC concurrent-root-region-scan-end, "]
    (re-pattern (str timestamp pause-time))))


; 1162.042: [GC concurrent-mark-start]
;
(defn gc-pattern-g1-concurrent-mark-start []
   (let [timestamp  "([\\d\\.]+): \\[GC concurrent-mark-start\\] "]
    (re-pattern (str timestamp))))


; 1162.842: [GC concurrent-mark-end, 0.8000300 secs]
;
(defn gc-pattern-g1-concurrent-mark-end []
   (let [timestamp  "([\\d\\.]+): \\[GC concurrent-mark-end, "]
    (re-pattern (str timestamp pause-time))))



; 1162.952: [GC concurrent-cleanup-start]
;
(defn gc-pattern-g1-concurrent-cleanup-start []
   (let [timestamp  "([\\d\\.]+): \\[GC concurrent-cleanup-start\\] "]
    (re-pattern (str timestamp))))
; 1162.952: [GC concurrent-cleanup-end, 0.0001380 secs]
;
(defn gc-pattern-g1-concurrent-cleanup-end []
   (let [timestamp  "([\\d\\.]+): \\[GC concurrent-cleanup-end, "]
    (re-pattern (str timestamp pause-time))))

; 1162.844: [GC remark 1162.846: [GC ref-proc, 0.0147680 secs], 0.0899440 secs]
;
;
(defn gc-pattern-g1-remark []
   (let [timestamp  "([\\d\\.]+): \\[GC remark, "]
    (re-pattern (str timestamp pause-time))))


; [Times: user=0.14 sys=0.00, real=0.09 secs]
;1162.934: [GC cleanup 11G->11G(16G), 0.0170410 secs]
; [Times: user=0.18 sys=0.00, real=0.01 secs]


; Example Minor GC entry
; 212.785: [GC [PSYoungGen: 524288K->32124K(611648K)] 524288K->32124K(2009792K), 0.1566980 secs] [Times: user=0.24 sys=0.06,
;    real=0.16 secs]
; Define regex pattern to parse young gen GC event
(defn minor-gc-pattern []
  (let [timestamp  "([\\d\\.]+): \\[GC .*\\[PS.*: "
        young-gen   (str space "] ")
        heap        (str space ", ")]
    (re-pattern (str timestamp young-gen  heap pause-time exec-stat))))

; Example Full GC entry
;"43587.513: [Full GC (System) [PSYoungGen: 964K->0K(598912K)]
; [PSOldGen: 142673K->120674K(1398144K)] 143637K->120674K(1997056K)
; [PSPermGen: 82179K->82179K(147520K)], 0.7556570 secs] [Times:
; user=0.76 sys=0.00, real=0.77 secs]"
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

; Variable definitions (for both process-full-gc & process-minor-gc
;     ts - timestamp (in seconds)
;     ys - YoungGen space starting heap size (in KB)
;     ye - YoungGen space ending heap size (in KB)
;     ym - YoungGen space max heap size (in KB)
;     os - OldGen space starting heap size (in KB)
;     oe - OldGen space ending heap size (in KB)
;     om - OldGen space max heap size (in KB)
;     hs - Total heap space starting heap size (in KB)
;     he - Total heap space ending heap size (in KB)
;     hm - Total heap space max heap size (in KB)
;     pt - GC Pause Time (in seconds)
;     ps - PermGen space starting heap size (in KB)
;     pe - PermGen space ending heap size (in KB)
;     pm - PermGen space max heap size (in KB)
;     ut - User Time (in seconds)
;     kt - Kernel Time (in seconds)
;     rt - Real Time (in seconds)
(defn process-full-gc [entry]
  (let [[a ts ys ye ym os oe om hs he hm ps pe pm pt ut kt rt & e] entry]
    (join \, [ts "full" pt
              ys ye ym
              hs he hm
              ut kt rt
              os oe om
              ps pe pm])))

(defn process-minor-gc [entry]
  (let [[a ts ys ye ym hs he hm pt ut kt rt & e] entry]
    (join \, [ts "minor" pt
              ys ye ym
              hs he hm
              ut kt rt])))

(defn process-g1-evac [entry]
  (let [[a ts ys ye ym hs he hm pt ut kt rt & e] entry]
    (join \, [ts "g1evac" pt
              ys ye ym
              hs he hm
              ut kt rt])))

(defn process-g1-mixed) [entry]
    (let [[a ts ys ye ym hs he hm pt ut kt rt & e] entry]
      println (str "g1-mixed match: " a ts)
      )



(def headers (join \,  ["timestamp" "gc.type" "pause.time"
                        "young.start" "young.end" "young.max"
                        "heap.start" "heap.end" "heap.max"
                        "time.user" "time.sys" "time.real"
                        "old.start" "old.end" "old.max"
                        "perm.start" "perm.end" "perm.max"]))


(defn process-gc-file [infile outfile]
  (let [gcdata (line-seq (clojure.java.io/reader (clojure.java.io/file infile)))]
    (with-open [w (clojure.java.io/writer outfile)]
      (let [writeln (fn [x] (.write w (str x "\n")))]
        (writeln headers)
        (doseq [line gcdata]
          (let [minor-gc (re-seq (minor-gc-pattern) line)
                g1-evac  (re-seq (minor-gc-pattern-g1-evac) line)
                g1-mixed  (re-seq (gc-pattern-g1-mixed) line)
                g1-conc-rrs-start  (re-seq (gc-pattern-g1-concurrent-root-region-scan-start) line)
                g1-conc-rrs-end  (re-seq (gc-pattern-g1-concurrent-root-region-scan-end) line)
                g1-conc-mark-start  (re-seq (gc-pattern-g1-concurrent-mark-start) line)
                g1-conc-mark-end  (re-seq (gc-pattern-g1-concurrent-mark-end) line)
                g1-conc-cl-start  (re-seq (gc-pattern-g1-concurrent-cleanup-start) line)
                g1-conc-cl-end  (re-seq (gc-pattern-g1-concurrent-cleanup-end) line)
                g1-remark  (re-seq (gc-pattern-g1-remark) line)
                full-gc  (re-seq (full-gc-pattern) line)]
            (when-not (nil? full-gc)
              (writeln (process-full-gc (first full-gc))))
            (when-not (nil? g1-evac)
             (writeln (process-g1-evac (first g1-evac))))
            (when-not (nil? g1-mixed )
              (writeln (process-g1-mixed  (first g1-mixed ))))
            ;(when-not (nil? g1-remark )
            ;  (writeln (process-g1-remark  (first g1-remark ))))
            ;(when-not (nil? g1-conc-rrs-start )
            ;  (writeln (process-g1-conc-rrs-start  (first g1-conc-rrs-start ))))
            ;(when-not (nil? g1-conc-mark-end )
            ;  (writeln (process-g1-conc-mark-end  (first g1-conc-mark-end ))))
            ;(when-not (nil? g1-conc-mark-start )
            ;  (writeln (process-g1-conc-mark-start  (first g1-conc-mark-start ))))
            ;(when-not (nil? g1-conc-mark-end )
            ;  (writeln (process-g1-conc-mark-end  (first g1-conc-mark-end ))))
            ;(when-not (nil? g1-conc-cl-start )
            ;  (writeln (process-g1-conc-cl-start  (first  g1-conc-cl-start ))))
            ;(when-not (nil? g1-conc-cl-end )
            ;  (writeln (process-g1-conc-cl-end  (first g1-conc-cl-end ))))
            (when-not (nil? minor-gc)
              (writeln (process-minor-gc (first minor-gc))))))))))



(defn -main
  "I don't do a whole lot."
  [& args]
  (println "Hello, World!"))

;-----------------------------------------------------------------------
; Convert Java GC log csv format
;-----------------------------------------------------------------------
(process-gc-file "gc.log" "data.csv")
(process-gc-file "gc.log" "data.csv")


