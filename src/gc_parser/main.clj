(ns gc-parser.core
    (:require
                [gc-parser.const :refer :all]
                [gc-parser.matcher_g1 :refer :all]
                [gc-parser.matcher_ParOld :refer :all]
                [gc-parser.pre_formatter :refer :all]
                 [gc-parser.core :refer :all]
                ))

(use '[clojure.string :only (join)])

;; TODO : add commandline processing
(defn -main [& args]
  (let [[input output] args]
  (process-gc-file-preformat input TMP_GC_FILE )
  (process-gc-file TMP_GC_FILE output)
  )
)

;(-main "input/gc.G1.log" "data.txt")
