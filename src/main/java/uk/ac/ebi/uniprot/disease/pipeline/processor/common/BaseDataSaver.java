package uk.ac.ebi.uniprot.disease.pipeline.processor.common;

import uk.ac.ebi.uniprot.disease.pipeline.request.DiseaseRequest;
import uk.ac.ebi.uniprot.disease.utils.JDBCConnectionUtils;

import java.sql.Connection;
import java.sql.SQLException;

public abstract class BaseDataSaver extends BaseProcessor {
    protected void updateMetrics(DiseaseRequest request, int size, long startTime, long endTime) {
        updateSavedRecordsCount(request, size);
        // update the time
        updateSavingTime(request, startTime, endTime);
    }

    protected Connection getConnection(DiseaseRequest req) throws SQLException {
        if(req.getConnxn() == null){
            Connection connxn = JDBCConnectionUtils.getConnection(req.getDbUserName(), req.getDbPassword(), req.getJdbcUrl());
            req.setConnxn(connxn);
        }

        return req.getConnxn();
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
