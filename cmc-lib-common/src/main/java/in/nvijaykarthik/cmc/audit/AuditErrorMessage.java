package in.nvijaykarthik.cmc.audit;

import java.util.Map;

import lombok.Data;
import lombok.EqualsAndHashCode;


@EqualsAndHashCode
@Data
public class AuditErrorMessage {

    private String id;
    private String timestamp;
    private String status;
    private String inputMessage;
    private Map<String, String> inputHeaders;
    private String outputMessage;
    private Map<String, String> outputHeaders;
    private String exceptionStackTrace;
}

   