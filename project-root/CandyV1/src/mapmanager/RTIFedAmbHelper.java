package mapmanager;

import hla.rti.EventRetractionHandle;
import hla.rti.LogicalTime;
import hla.rti.ReflectedAttributes;

public interface RTIFedAmbHelper {
    public void objectCreated(int theObject, int theObjectClass, String objectName);

    public void objectUpdated(int theObject, ReflectedAttributes theAttributes, byte[] tag, LogicalTime theTime,
            EventRetractionHandle retractionHandle);

}
