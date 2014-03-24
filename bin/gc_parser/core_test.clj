(ns gc-parser.core-test
  (:require [clojure.test :refer :all]
            [gc-parser.const :refer :all]
            [gc-parser.hello :refer :all]
            ))



(deftest a-test
  (testing "FIXME, I fail."
    (is (= 0 1))))

;(deftest a-test-parold-minor
;  ( let [line "212.785: [GC [PSYoungGen: 524288K->32124K(611648K)] 524288K->32124K(2009792K), 0.1566980 secs] [Times: user=0.24 sys=0.06, real=0.16 secs]"]
;  (testing "ParOld - minor GC"
;    (is (= 0 (re-seq (minor-gc-pattern) line))))))

(deftest test-g1-evac
  ( let [line g1-evac
         match (re-seq (minor-gc-pattern-g1-evac) line)
         res (process-g1-evac (first match))]
  (testing "G1 -evac young "
    (is (= g1-evac-ok res)))))

(deftest test-g1-young
  ( let [line g1-young
         match (re-seq (minor-gc-pattern-g1-young) line)
         res (process-g1-young (first match))
         ]
  (testing "G1 - young"
            (println (str "test g1 young: " res))
    (is (= g1-young-ok res)))))

(test-g1-evac)

;(test-g1-young)