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


