package rti;

import hla.rti.EventRetractionHandle;
import hla.rti.LogicalTime;
import hla.rti.RTIexception;
import hla.rti.ReflectedAttributes;
import hla.rti13.java1.FederateInternalError;

public interface IRTIFedAmbHelper {
    public void objectCreated(int theObject, int theObjectClass, String objectName) throws RTIexception;

    public void objectUpdated(int theObject, ReflectedAttributes theAttributes, byte[] tag, LogicalTime theTime,
            EventRetractionHandle retractionHandle) throws RTIexception, NumberFormatException, FederateInternalError;

}
