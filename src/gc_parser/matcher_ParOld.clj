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

(defn full-gc-pattern-meta []
    (let [timestamp   "([\\d\\.]+): \\[Full.*"
          young-gen  (str ": " space "]")
          old-gen    (str " \\[\\w+: " space "\\] ")
          meta-space-p   (str "\\[" meta-space "\\], ")
          heap       space]
       ;(println (str timestamp 
		   ;          young-gen 
			;			old-gen 
				;		heap ", " 
					;	meta-space-p 
						;pause-time 
					;	exec-stat))  
      (re-pattern (str timestamp 
		             young-gen 
						old-gen 
						heap ", " 
						meta-space-p 
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
    (let [[a ts yos yoe yss oos ooe oss hos hoe hss pos poe pss pt ut kt rt & e] entry
          promo (str "")]
      (join \, [ts "ParOld-full" pt 
			           yos yss yoe ""
				      "" "" 
              hos hss hoe ""
					   oos ooe oss "" 
              promo
              ut kt rt 
					   pos pss poe])))

(defn process-full-gc-meta [entry]
    (let [[a ts yos yoe yss oos ooe oss hos hoe hss pos poe pss pt ut kt rt & e] entry
          promo (str "")]
      (join \, [ts "ParOld-full-meta" pt 
			           yos yss yoe ""
				      "" "" 
              hos hss hoe ""
					   oos ooe oss "" 
              promo
              ut kt rt 
					   pos pss poe])))

;; TODO: we are putting the same values for before/after 
(defn getOldSizeOcc [yos yoe yss hos hoe hss]
   (let [oldOccStart  (- hoe yoe)
         oldOccEnd (- hoe yoe)
         oldSizeStart (- hss yss)
         oldSizeEnd (- hss yss)]
    (join SEP [oldOccStart oldOccEnd oldSizeStart oldSizeEnd]))
)

(defn promoRateParOld [yos yoe yss hos hoe hss]
   (let [rate (- (- hoe yoe) (- hos yos))]
    (str rate)
))

;;
;;
;; young.occ.end == survivor.size.end
;;
;;
(defn process-minor-gc [entry]
    (let [[a ts yos yoe yss hos hoe hss pt ut kt rt & e] entry
          promo (promoRateParOld yos yoe yss hos hoe hss)
          oldSizeAndOccp (getOldSizeOcc yos yoe yss hos hoe hss)  ]
	  ;(println (str "process minor: " entry "  END."))
 (join \, [ts "ParOld-minor" pt 
		        yos yss yoe "" 
          yoe yoe
				hos hss hoe "" 
        oldSizeAndOccp
        promo
				ut kt rt])))
