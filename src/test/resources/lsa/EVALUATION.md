# Evaluation

Steps to reproduce the evalution described in the [report](report.pdf) 

## Design Decision(DDD)

Design decision metrics

### Distribution

The DDD distribution is generated manually by counting each occurrence for each `.xml` file.  
See: `src/test/resources/lsa/1corpus/ccc`

### Precision, Recall, F2 and Accuracy

Execute `CarchaPipelineTest.java` with `-Xms128m  -Xmx5g -XX:MaxPermSize=512m` as VM arguments

Test class: `edu.isistan.carcha.CarchaPipelineTest`  
Test method: `testExecuteUIMAAnnotator`

## Traceability

## Precision and Recall

Test class: `src.test.java.edu.isistan.carcha.lsa.LSARunnerTest`
Test method: `testTraceability`

Take into account that we use between 60 and 300 dimensions and different thresholds.
This configuration runs for: 
 * Golden CCC + Golden DDD
 * Golden CCC + DDD
 * Golden CCC + Architectural documents.

The `CORPUS_DDD_FOLDER` and `CORPUS_CCC_FOLDER` variables contain the paths to the `DDDs` and `CCCs`.  
`GOLDEN_FOLDER` contains the path to the golden traceability files.