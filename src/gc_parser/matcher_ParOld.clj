(ns gc-parser.matcher_ParOld 
    (:require 
                [gc-parser.const :refer :all]
                ))

(use '[clojure.string :only (join)])



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

; Variable definitions (for both process-full-gc & process-minor-gc
;     ts - timestamp (in seconds)
;     pt - GC Pause Time (in seconds)
;     ys - YoungGen space starting heap size (in KB)
;     ye - YoungGen space ending heap size (in KB)
;     ym - YoungGen space max heap size (in KB)
;     os - OldGen space starting heap size (in KB)
;     oe - OldGen space ending heap size (in KB)
;     om - OldGen space max heap size (in KB)
;     hs - Total heap space starting heap size (in KB)
;     he - Total heap space ending heap size (in KB)
;     hm - Total heap space max heap size (in KB)
;
;     ps - PermGen space starting heap size (in KB)
;     pe - PermGen space ending heap size (in KB)
;     pm - PermGen space max heap size (in KB)
;
;     ut - User Time (in seconds)
;     kt - Kernel Time (in seconds)
;     rt - Real Time (in seconds)
(defn process-full-gc [entry]
    (let [[a ts ys ye ym os oe om hs he hm ps pe pm pt ut kt rt & e] entry
          promo (str "")]
      (join \, [ts "ParOld-full" pt 
			           ys ye ym ""
				      "" "" 
              hs he hm ""
					   os oe om "" 
              promo
              ut kt rt 
					   ps pe pm])))
	  
(defn process-minor-gc [entry]
    (let [[a ts ys ye ym hs he hm pt ut kt rt & e] entry
          promo (str "")]
	  ;(println (str "process minor: " entry "  END."))
      (join \, [ts "ParOld-minor" pt 
		        ys ye ym "" 
          "" ""
				hs he hm 
        "" "" "" ""
        promo
				ut kt rt])))
