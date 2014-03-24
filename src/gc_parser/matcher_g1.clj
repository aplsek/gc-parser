(ns gc-parser.hello 
    (:require 
                [gc-parser.const :refer :all]
                ))

(use '[clojure.string :only (join)])



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

(defn gc-pattern-g1-cleanup []
   (let [timestamp      "([\\d\\.]+): \\[GC cleanup "
        space-cleanup   (str space-g1-cleanup ", ")]
     (println (str timestamp pause-time exec-stat))
    (re-pattern (str timestamp space-cleanup pause-time exec-stat))))



; G1 mixed
; 1165.366: [GC pause (mixed), 0.0793930 secs] [Eden: 672.0M(672.0M)->0.0B(672.0M) Survivors: 128.0M->128.0M Heap:
; 7421.9M(16.0G)->5212.4M(16.0G)] [Times: user=0.90 sys=0.00, real=0.08 secs]
;
;
(defn gc-pattern-g1-mixed []
   (let [timestamp  "([\\d\\.]+): \\[GC pause \\(mixed\\), "
        eden        (str " \\[" space-eden)
        survivor    (str " " space-surv " ")
        heap        (str space-heap "\\]")]
     (println (str timestamp pause-time eden survivor heap exec-stat))
    (re-pattern (str timestamp pause-time eden survivor heap exec-stat))))

; 1161.747: [GC concurrent-root-region-scan-start]
(defn gc-pattern-g1-conc-reg-start []
   (let [timestamp  "([\\d\\.]+): \\[GC concurrent-root-region-scan-start\\]"]
    (re-pattern (str timestamp))))


; 1162.042: [GC concurrent-root-region-scan-end, 0.2950840 secs]
;
(defn gc-pattern-g1-conc-reg-end []
   (let [timestamp  "([\\d\\.]+): \\[GC concurrent-root-region-scan-end, "]
    (re-pattern (str timestamp pause-time))))


; 1162.042: [GC concurrent-mark-start]
;
(defn gc-pattern-g1-conc-mark-start []
   (let [timestamp  "([\\d\\.]+): \\[GC concurrent-mark-start\\] "]
    (re-pattern (str timestamp))))


; 1162.842: [GC concurrent-mark-end, 0.8000300 secs]
;
(defn gc-pattern-g1-conc-mark-end []
   (let [timestamp  "([\\d\\.]+): \\[GC concurrent-mark-end, "]
    (re-pattern (str timestamp pause-time))))


; 1162.952: [GC concurrent-cleanup-start]
;
(defn gc-pattern-g1-conc-cl-start []
   (let [timestamp  "([\\d\\.]+): \\[GC concurrent-cleanup-start\\] "]
    (re-pattern (str timestamp))))
; 1162.952: [GC concurrent-cleanup-end, 0.0001380 secs]
;
(defn gc-pattern-g1-conc-cl-end []
   (let [timestamp  "([\\d\\.]+): \\[GC concurrent-cleanup-end, "]
    (re-pattern (str timestamp pause-time))))


; 1162.844: [GC remark 1162.846: [GC ref-proc, 0.0147680 secs], 0.0899440 secs]
;
;
(defn gc-pattern-g1-remark []
   (let [timestamp  "([\\d\\.]+): \\[GC remark, "]
    (re-pattern (str timestamp pause-time))))


; 1162.042: [GC concurrent-mark-start]
;
(defn gc-pattern-g1-conc-mark-start []
   (let [timestamp  "([\\d\\.]+): \\[GC concurrent-mark-start\\] "]
    (re-pattern (str timestamp))))


; 1162.842: [GC concurrent-mark-end, 0.8000300 secs]
;
(defn gc-pattern-g1-conc-mark-end []
   (let [timestamp  "([\\d\\.]+): \\[GC concurrent-mark-end, "]
    (re-pattern (str timestamp pause-time))))




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

(defn process-g1-mixed [entry]
  (let [[a ts ys ye ym hs he hm pt ut kt rt & e] entry]
    (join \, [ts "g1mixed" pt
              ys ye ym
              hs he hm
              ut kt rt])))

(defn process-g1-conc-reg-start[entry]
    (let [[a ts ys ye ym hs he hm pt ut kt rt & e] entry]
    (join \, [ts "g1conc-start"])))

(defn process-g1-conc-reg-end[entry]
    (let [[a ts ys ye ym hs he hm pt ut kt rt & e] entry]
    (join \, [ts "g1conc-end" pt ])))


(defn process-g1-conc-cl-start[entry]
    (let [[a ts ys ye ym hs he hm pt ut kt rt & e] entry]
    (join \, [ts "g1conc-cl-start"])))

(defn process-g1-conc-cl-end[entry]
    (let [[a ts ys ye ym hs he hm pt ut kt rt & e] entry]
    (join \, [ts "g1conc-cl-end"])))



(defn process-g1-conc-mark-start[entry]
    (let [[a ts ys ye ym hs he hm pt ut kt rt & e] entry]
    (join \, [ts "g1conc-mark-start"])))

(defn process-g1-conc-mark-end[entry]
    (let [[a ts ys ye ym hs he hm pt ut kt rt & e] entry]
    (join \, [ts "g1conc-mark-end"])))


(defn process-g1-remark[entry]
    (let [[a ts ys ye ym hs he hm pt ut kt rt & e] entry]
    (join \, [ts "g1remark"])))


(defn process-g1-cleanup [entry]
  (let [[a ts yob ysb yoa ysa sb sa hob hoa hsa ut kt rt & e] entry]
    ;(println (str "Hello process-g1-evac:" entry))
    ;(println (str "   " ))
    (join \, [ts "g1cleanup" rt yob ysb yoa sb sa hob hoa hsa ut kt rt])))



