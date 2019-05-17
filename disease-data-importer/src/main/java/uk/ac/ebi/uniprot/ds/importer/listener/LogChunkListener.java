package uk.ac.ebi.uniprot.ds.importer.listener;

import lombok.extern.slf4j.Slf4j;
import org.springframework.batch.core.ChunkListener;
import org.springframework.batch.core.scope.context.ChunkContext;

import java.util.List;

@Slf4j
public class LogChunkListener implements ChunkListener {

    @Override
    public void beforeChunk(ChunkContext chunkContext) {
        // do nothing
    }

    @Override
    public void afterChunk(ChunkContext chunkContext) {
        String stepName = chunkContext.getStepContext().getStepName();
        int writeCount = chunkContext.getStepContext().getStepExecution().getWriteCount();
        log.info("Executed chunk for {}: {}", stepName, writeCount);
    }

    @Override
    public void afterChunkError(ChunkContext chunkContext) {
        String stepName = chunkContext.getStepContext().getStepName();
        String status = chunkContext.getStepContext().getStepExecution().getStatus().name();
        log.warn("Failed to executed chunk for step: {} with status: {}", stepName, status);
        List<Throwable> errors = chunkContext.getStepContext().getStepExecution().getFailureExceptions();
        errors.forEach(throwable ->{
            log.error("Chunk error: ",throwable);
        });
    }

}
