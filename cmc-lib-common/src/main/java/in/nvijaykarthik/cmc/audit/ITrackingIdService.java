package in.nvijaykarthik.cmc.audit;

import org.springframework.messaging.Message;

public interface ITrackingIdService {

    public String getTxnId(Message<String> msg);   

}
