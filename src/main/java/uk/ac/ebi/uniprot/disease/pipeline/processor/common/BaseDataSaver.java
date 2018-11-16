package uk.ac.ebi.uniprot.disease.pipeline.processor.common;

import uk.ac.ebi.uniprot.disease.pipeline.request.DiseaseRequest;

public abstract class BaseDataSaver extends BaseProcessor {
    protected void updateMetrics(DiseaseRequest request, int size, long startTime, long endTime) {
        updateSavedRecordsCount(request, size);
        // update the time
        updateSavingTime(request, startTime, endTime);
    }

    private void updateSavedRecordsCount(DiseaseRequest request, int size){
        long total = request.getWorkflowMetrics().getRecordsSaved() + size;
        request.getWorkflowMetrics().setRecordsSaved(total);
    }

    private void updateSavingTime(DiseaseRequest request, long startTime, long endTime) {
        long currentTime = request.getWorkflowMetrics().getTotalSaveTime();
        long totalTime = currentTime + (endTime - startTime);
        request.getWorkflowMetrics().setTotalSaveTime(totalTime);
    }
}
