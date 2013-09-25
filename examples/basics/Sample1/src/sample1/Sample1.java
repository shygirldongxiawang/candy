/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package sample1;

import hla.rti.RTIexception;

/**
 *
 * @author murat
 */
public class Sample1
{

    public static void main(String[] args)
    {
        {
            // get a federate name, use "exampleFederate" as default
            String federateName = args[0];
            String federationName = args[1];
            String fomfilePath = args[2];

            try
            {
                // run the example federate
                new Example13Federate().runFederate(federateName, federationName, fomfilePath);
            }
            catch (RTIexception rtie)
            {
                // an exception occurred, just log the information and exit
                rtie.printStackTrace();
            }
        }
    }
}
