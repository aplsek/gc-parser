(ns gc-parser.matcher_g1
    (:require
                [gc-parser.const :refer :all]
                ))

(use '[clojure.string :only (join)])



(defn minor-gc-pattern-g1-evac []
   (let [timestamp  "([\\d\\.]+): \\[GC pause \\(G1 Evacuation Pause\\) \\(young\\)"
         eden        (str " \\[" space-eden " ")
         survivor    (str space-surv " ")
         heap        (str space-heap "\\]")]
    (re-pattern (str timestamp eden survivor heap exec-stat))
    ))


;; TODO: cleanup
(def ^:constant xxx (str "("
                                        g1-evacuation-event  "|"  
                                        g1-young-event "|" 
                                        g1-initial-mark-event "|" 
                                        g1-mixed-event  "|" 
                                        g1-tospace-exhast-event "|"
                                       ")"
                                       ))


(def ^:constant g1_event_names_pattern (str g1-young-event "|" g1-mixed-event "|" g1-tospace-exhast-event "|" g1-initial-mark-event "|" g1-evacuation-event))

(defn g1-event-pattern []
   (let [gcevent g1_event_names_pattern]
    (re-pattern (str gcevent))))


; G1 young
; 755.441: [GC pause (young), 0.4418240 secs] [Eden: 9024.0M(9024.0M)->0.0B(8384.0M)
;   Survivors: 800.0M->1248.0M Heap:
;   13.2G(16.0G)->5072.0M(16.0G)] [Times: user=5.41 sys=0.01, real=0.44
;   secs]
;
;
(defn minor-gc-pattern-g1-young []
   (let [timestamp  "([\\d\\.]+): \\[GC pause "
        gcevent     (str "(\\(young\\)|\\(to-space\\ exhausted\\)|\\(mixed\\)| )+"  ", ")
       ; gcevent     (str g1_event_pattern ", ")
        eden        (str " \\[" space-eden)
        survivor    (str " " space-surv " ")
        heap        (str space-heap "\\]")]
     ;(println (str timestamp gcevent pause-time eden survivor heap exec-stat))
    (re-pattern (str timestamp gcevent pause-time eden survivor heap exec-stat))))


;
; 2273.426: [Full GC 28G->2265M(28G), 10.5452700 secs]
;   [Eden: 0.0B(24.0G)->0.0B(24.0G) Survivors: 0.0B->0.0B Heap: 28.0G(28.0G)->2265.4M(28.0G)]
; [Times: user=13.17 sys=0.23, real=10.54 secs]
;
;
(defn g1-full-pattern []
   (let [timestamp     (str  timestamp-pattern "\\[Full GC ")
         total_space (str space)
         eden        (str " \\[" space-eden)
         survivor    (str " " space-surv " ")
         heap        (str space-heap "\\]")
         ]
    (re-pattern (str timestamp space pause-time eden survivor heap exec-stat))))


(defn gc-pattern-g1-cleanup []
   (let [timestamp      "([\\d\\.]+): \\[GC cleanup "
        space-cleanup   (str space-g1-cleanup ", ")]
    ; (println (str timestamp pause-time exec-stat))
    (re-pattern (str timestamp space-cleanup pause-time exec-stat))))




; 1161.747: [GC concurrent-root-region-scan-start]
(defn gc-pattern-g1-conc-reg-start []
   (let [timestamp  "([\\d\\.]+): \\[GC concurrent-root-region-scan-start\\]"]
    (re-pattern (str timestamp))))


; 1162.042: [GC concurrent-root-region-scan-end, 0.2950840 secs]
;
(defn gc-pattern-g1-conc-reg-end []
   (let [timestamp  "([\\d\\.]+): \\[GC concurrent-root-region-scan-end, "]
    (re-pattern (str timestamp pause-time))))





; 1162.952: [GC concurrent-cleanup-start]
;
(defn gc-pattern-g1-conc-cl-start []
   (let [timestamp  "([\\d\\.]+): \\[GC concurrent-cleanup-start\\]"]
    (re-pattern (str timestamp))))

; 1162.952: [GC concurrent-cleanup-end, 0.0001380 secs]
;
(defn gc-pattern-g1-conc-cl-end []
   (let [timestamp  "([\\d\\.]+): \\[GC concurrent-cleanup-end, "]
    ; (println (str timestamp pause-time))
    (re-pattern (str timestamp pause-time))))


; 1162.844: [GC remark 1162.846: [GC ref-proc, 0.0147680 secs], 0.0899440 secs]
;
;
(defn gc-pattern-g1-remark []
   (let [timestamp  "([\\d\\.]+): \\[GC remark "
         timestamp2 "([\\d\\.]+): \\[GC ref-proc, "
         double_pause-time (str pause-time ", " pause-time)]
     (re-pattern (str timestamp timestamp2 double_pause-time exec-stat))))


; 1162.042: [GC concurrent-mark-start]
;
(defn gc-pattern-g1-conc-mark-start []
   (let [timestamp  "([\\d\\.]+): \\[GC concurrent-mark-start\\]"]
    (re-pattern (str timestamp))))


; 1162.842: [GC concurrent-mark-end, 0.8000300 secs]
;
(defn gc-pattern-g1-conc-mark-end []
   (let [timestamp  "([\\d\\.]+): \\[GC concurrent-mark-end, "]
    (re-pattern (str timestamp pause-time))))



;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;

(defn promoRate [yob yoa sb sa hob hoa]
   (-  ( - hoa (+ yoa sa)) (- hob (+ yob sb)))
  )

(defn allocRate [yob yoa sb sa hob hoa]
  ;; TODO:?? need the previous GC event to compute
)

(defn oldOccup [yob yoa sb sa hob hoa]
  ;(println (str "oldOccup run:" yob ))    
  (let []
    (join SEP [(- hob (+ yob sb)) ( - hoa (+ yoa sa)) ]))
)

(defn oldsize [ysb ysa sb sa hsb hsa]
      (join SEP [( - hsb (+ ysb sb)) ( - hsa (+ ysa sa))
                                  ])
)


;433.905:
;  ts - timestamp (in seconds)
;  pt - GC Pause Time (in seconds)
;
;  yob  youngGen occupation :8944.0M
;  ysb YoungGen size before: (8944.0M)
;  yoa  Young gen occupation after :->0.0B
;  ysa Yougn Gen size after: (8920.0M)
;
;  sb Survivors occupation before: 272.0M->
;  sa Survivor occupation after:  296.0M
;
;  hob  Heap occ before: 9418.3M
;  hsb heap size before (15.0G)
;  hoa heap occ after -> 498.3M(
;  hsa heap size after 15.0G)]
;
;  ut  user time [Times: user=0.71
;  kt sys time  sys=0.03,
;  rt real time : real=0.11 secs]
; PauseTime = G1 Evac does not report pause time separately, only in Times
(defn process-g1-evac [name entry]
  (let [[a ts yob ysb yoa ysa sb sa hob hsb hoa hsa ut kt rt & e] entry
        oldGenOcc (oldOccup yob yoa sb sa hob hoa)
        oldGenSize (oldsize ysb ysa sb sa hsb hsa)
        promo (promoRate ysb ysa sb sa hsb hsa)
        permGen (str ",,,")
        ]
    (join SEP [ts name rt 
               yob ysb yoa ysa 
               sb sa 
               hob hsb hoa hsa
               oldGenOcc oldGenSize 
               promo 
               ut kt rt])))

(defn process-g1-event [name entry]
  (let [[a ts phase pt yob ysb yoa ysa sb sa hob hsb hoa hsa ut kt rt & e] entry
        oldGenOcc (oldOccup yob yoa sb sa hob hoa)
        oldGenSize (oldsize ysb ysa sb sa hsb hsa)
        promo (promoRate ysb ysa sb sa hsb hsa)
        ]
    (join SEP [ts name pt 
               yob ysb yoa ysa
               sb sa
               hob hsb hoa hsa
               oldGenOcc oldGenSize
               promo
               ut kt rt])))

(defn process-g1-full [name entry]
  (let [[a ts pt heap_before heap_after heap_size  yob ysb yoa ysa sb sa hob hsb hoa hsa ut kt rt & e] entry
        oldGenOcc (oldOccup yob yoa sb sa hob hoa)
        oldGenSize (oldsize ysb ysa sb sa hsb hsa)
        promo (promoRate ysb ysa sb sa hsb hsa)
        ]
    (join SEP [ts name pt 
               yob ysb yoa ysa
               sb sa
               hob hsb hoa hsa
               oldGenOcc oldGenSize
               promo
               ut kt rt])))
 
(defn process-g1-conc-reg-start[entry]
    (let [[a ts ys ye ym hs he hm pt ut kt rt & e] entry]
    (join SEP [ts "g1conc-region-start"])))

(defn process-g1-conc-reg-end[entry]
    (let [[a ts pt yob ysb yoa ysa sb sa hob hsb hoa hsa ut kt rt & e] entry]
    (join SEP [ts "g1conc-region-end" pt])))


(defn process-g1-conc-cl-start[entry]
    (let [[a ts ys ye ym hs he hm pt ut kt rt & e] entry]
    (join SEP [ts "g1conc-cl-start"])))

(defn process-g1-conc-cl-end[entry]
    (let [[a ts pt ] entry]
    ;  (println (str "g1conc-cl-end:"  "  pt= " pt "  ts= " ts "  a= " a))
    (join SEP [ts "g1conc-cl-end" pt] )))



(defn process-g1-conc-mark-start[entry]
    (let [[a ts ys ye ym hs he hm pt ut kt rt & e] entry]
    (join SEP [ts "g1conc-mark-start"])))

(defn process-g1-conc-mark-end[entry]
    (let [[a ts pt ] entry]
    ;  (println (str "g1conc-cl-end:" entry  "  pt= " pt "  ts= " ts "  a= " a))
    (join SEP [ts "g1conc-mark-end" pt] )))


(defn process-g1-remark[entry]
    (let [[a ts ts2 pt2 pt1 ys ye ym hs he hm pt ut kt rt & e] entry]
    (join SEP [ts "g1remark" pt1])))


(defn process-g1-cleanup [entry]
  (let [[a ts hob hoa hsa pt & e] entry]
    ;(println (str "Hello process-g1-cleanup:" entry))
    ;(println (str "   " ))
    (join SEP [ts "g1cleanup" pt " " " " " " " " " " " " " " hob hoa hsa])))