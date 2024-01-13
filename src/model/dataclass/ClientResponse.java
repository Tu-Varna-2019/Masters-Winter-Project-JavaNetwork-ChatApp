package model.dataclass;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;

@JsonInclude(Include.NON_NULL)
public class ClientResponse {
    private String status;
    private String message;
    private DataResponse data;
}
