(ns gc-parser.main
    (:require
                [gc-parser.const :refer :all]
                [gc-parser.matcher_g1 :refer :all]
                [gc-parser.matcher_ParOld :refer :all]
                [gc-parser.pre_formatter :refer :all]
                 [gc-parser.core :refer :all]
                )
    (:gen-class))

(use '[clojure.string :only (join)])

;; TODO : add commandline processing
;;    see http://www.beaconhill.com/blog/?p=283
;;
(defn -main [& args]
  (let [[input output] args]
  (process-gc-file-preformat input TMP_GC_FILE )
  (process-gc-file TMP_GC_FILE output)
  )
)

;(-main "input/gc.G1.log" "data.txt")
