package no.nav.familie.ks.oppslag.dokarkiv;

import com.fasterxml.jackson.module.kotlin.MissingKotlinParameterException;
import no.nav.familie.ks.kontrakter.dokarkiv.api.ArkiverDokumentRequest;
import no.nav.familie.ks.kontrakter.dokarkiv.api.ArkiverDokumentResponse;
import no.nav.security.oidc.api.ProtectedWithClaims;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.HashMap;
import java.util.Map;

@RestController
@ProtectedWithClaims(issuer = "intern")
@RequestMapping("/api/arkiv")
public class DokarkivController {
    private static final Logger LOG = LoggerFactory.getLogger(DokarkivController.class);

    private DokarkivService journalføringService;

    DokarkivController(DokarkivService journalføringService) {
        this.journalføringService = journalføringService;
    }

    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ExceptionHandler({MethodArgumentNotValidException.class, HttpMessageNotReadableException.class})
    public Map<String, String> handleValidationExceptions(
            Exception e) {
        Map<String, String> errors = new HashMap<>();
        if (e instanceof MethodArgumentNotValidException) {
            MethodArgumentNotValidException ex = (MethodArgumentNotValidException) e;
            ex.getBindingResult().getAllErrors().forEach((error) -> {
                String fieldName = ((FieldError) error).getField();
                String errorMessage = error.getDefaultMessage();
                errors.put(fieldName, errorMessage);
            });
        } else if ( e instanceof  HttpMessageNotReadableException) {
            HttpMessageNotReadableException ex = (HttpMessageNotReadableException) e;
            if (ex.getMostSpecificCause() instanceof MissingKotlinParameterException) {
                MissingKotlinParameterException kotEx = (MissingKotlinParameterException) ex.getMostSpecificCause();
                errors.put(kotEx.getParameter().getName(), kotEx.getMsg());
            }
        }
        LOG.warn("Valideringsfeil av input ved arkivering: " + errors);
        return errors;
    }

    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    @ExceptionHandler(RuntimeException.class)
    public Map<String, String> handleValidationExceptions(
            RuntimeException ex) {
        LOG.warn("Uventet arkiveringsfeil: ", ex);
        return Map.of("message", ex.getMessage());
    }

    @PostMapping(produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseStatus(HttpStatus.CREATED)
    public ArkiverDokumentResponse arkiverDokument(@Valid @RequestBody ArkiverDokumentRequest arkiverDokumentRequest) {
        return journalføringService.lagInngåendeJournalpost(arkiverDokumentRequest);
    }
}
