/*
 * Created by sahmad on 05/02/19 09:12
 * UniProt Consortium.
 * Copyright (c) 2002-2019.
 *
 */

package uk.ac.ebi.uniprot.ds.controller.error;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;
import uk.ac.ebi.uniprot.ds.controller.filter.RequestCorrelation;
import uk.ac.ebi.uniprot.ds.controller.response.ErrorResponse;
import uk.ac.ebi.uniprot.ds.exception.AssetNotFoundException;

@RestControllerAdvice(annotations = RestController.class)
public class DiseaseControllerAdvice extends ResponseEntityExceptionHandler {

    @ExceptionHandler(value = AssetNotFoundException.class)
    public ResponseEntity<ErrorResponse> handleAssetNotFoundException(AssetNotFoundException anf, WebRequest req){

        ErrorResponse errorResponse = new ErrorResponse(RequestCorrelation.getCorrelationId(), null,
                anf.getMessage(), HttpStatus.NOT_FOUND.value());

        return new ResponseEntity<>(errorResponse, HttpStatus.NOT_FOUND);
    }
}
