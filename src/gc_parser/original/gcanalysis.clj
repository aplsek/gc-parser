(ns gc-parser.gcanalysis)

(use '[clojure.string :only (join)])  
;
; Use when-let to test
;[Times: user=0.25 sys=0.02, real=0.24 secs] 
(def ^:constant pause-time "([\\d\\.]+) secs\\]")
(def ^:constant space "(\\d+)K->(\\d+)K\\((\\d+)K\\)")
(def ^:constant exec-stat " \\[Times: user=([\\d\\.]+) sys=([\\d\\.]+), real=([\\d\\.]+) secs\\]")

; Example Minor GC entry		 
; 212.785: [GC [PSYoungGen: 524288K->32124K(611648K)] 524288K->32124K(2009792K), 0.1566980 secs] [Times: user=0.24 sys=0.06, real=0.16 secs] 
; Define regex pattern to parse young gen GC event
(defn minor-gc-pattern []
    (let [timestamp  "([\\d\\.]+): \\[GC .*\\[PS.*: "
          young-gen   (str space "] ")
          heap       (str space ", ")]
         (re-pattern (str timestamp young-gen  heap pause-time exec-stat))))                

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
	  (println (str "process minor: " entry "  END."))
      (join \, [ts "minor" pt 
		        ys ye ym 
				hs he hm 
				ut kt rt])))

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
				 full-gc (re-seq (full-gc-pattern) line)]
			  (when-not (nil? full-gc) 
				(writeln (process-full-gc (first full-gc))))
			  (when-not (nil? minor-gc) 
				(writeln (process-minor-gc (first minor-gc))))))))))


(defn testt
  [line]
    (println "test :")
   (let [g1-evac  (re-seq (minor-gc-pattern) line)]
     (println "test :")
      (println (str "str :" g1-evac))
      (when-not (nil? g1-evac)
              ( (println " match g1-minor!!!!!")
                (println (process-minor-gc (first g1-evac))))
     )
      (println (str "end. :"))
  )
   )

(testt "212.785: [GC [PSYoungGen: 524288K->32124K(611648K)] 524288K->32124K(2009792K), 0.1566980 secs] [Times: user=0.24 sys=0.06, real=0.16 secs]")

;-----------------------------------------------------------------------	  
; Convert Java GC log csv format
;-----------------------------------------------------------------------				
;(process-gc-file "gc.log" "data.csv")

