package uk.ac.ebi.uniprot.disease.pipeline.processor.common;

import uk.ac.ebi.uniprot.disease.pipeline.request.DiseaseRequest;

public abstract class BaseFileParser extends BaseProcessor {

    protected void updateMetrics(DiseaseRequest request, int size, long startTime, long endTime) {
        updateRecordsParsedCount(request, size);
        updateParsingTime(request, startTime, endTime);
    }

    private void updateRecordsParsedCount(DiseaseRequest request, int size){
        long total = request.getWorkflowMetrics().getRecordsParsed() + size;
        request.getWorkflowMetrics().setRecordsParsed(total);
    }

    private void updateParsingTime(DiseaseRequest request, long startTime, long endTime) {
        long currentTime = request.getWorkflowMetrics().getTotalParseTime();
        long totalTime = currentTime + (endTime - startTime);
        request.getWorkflowMetrics().setTotalParseTime(totalTime);
    }
}
