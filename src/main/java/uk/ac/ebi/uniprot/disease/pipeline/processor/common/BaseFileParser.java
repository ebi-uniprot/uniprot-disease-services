package uk.ac.ebi.uniprot.disease.pipeline.processor.common;

import uk.ac.ebi.uniprot.disease.pipeline.request.DiseaseRequest;

public abstract class BaseFileParser extends BaseProcessor {

    protected void updateMetrics(DiseaseRequest request, int size, long startTime, long endTime, long totalRecordCount) {
        updateTotalRecordCount(request, totalRecordCount);
        updateRecordsParsedCount(request, size);
        updateParsingTime(request, startTime, endTime);
    }

    protected void updateTotalRecordCount(DiseaseRequest request, long totalRecordCount){
        request.getWorkflowMetrics().setTotalRecords(totalRecordCount);
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
