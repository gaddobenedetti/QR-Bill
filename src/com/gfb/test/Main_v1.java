/*
 * Copyright 2018 Gaddo F Benedetti
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy of this software
 * and associated documentation files (the "Software"), to deal in the Software without restriction,
 * including without limitation the rights to use, copy, modify, merge, publish, distribute,
 * sublicense, and/or sell copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all copies or
 * substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR IMPLIED, INCLUDING
 * BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE AND
 * NONINFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM,
 * DAMAGES OR OTHER LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE SOFTWARE.
 */

package com.gfb.test;

import java.io.File;

public class Main_v1 {

    // The file path of the input QR Code file
    private static final String     TEST_INVOICE    = "res/qr_tests_v1/Invoice#.png";
    // The file path of the output QR Code file
    private static final String     COPY_INVOICE    = "res/qr_tests_v1/Invoice#_new.png";
    // Which of the three input files to be processed (1 - 3). If any other value is used,
    // all will be processed in turn.
    private static final int        QR_INDEX        = 2;
    // The width & height of the output QR Code file
    private static final int        QR_LENGTH       = 300;
    // Whether the raw QR code is also printed after each reading
    private static final boolean    SHOW_OUTPUTCODE = false;
    // The version of the QR code bill, if generated from data
    private static final Float      QR_VERSION      = 1.0F;

    public static void main(String[] args) {
        
        int start = 0;
        int end = 4;
        if (QR_INDEX > start && QR_INDEX <= end) {
            start = QR_INDEX - 1;
            end = QR_INDEX;
        }

        for (int i = start + 1; i < end + 1; i++) {
            String testInvoiceFile = TEST_INVOICE.replace("#", String.valueOf(i));
            String copyInvoiceFile = COPY_INVOICE.replace("#", String.valueOf(i));
            String[] responses = new String[3];
            Helper.logMessage("Test Subject: " + testInvoiceFile + "\n");

            File copyFile = new File(copyInvoiceFile);
            if (copyFile.exists())
                copyFile.delete();
            QRBill invoice = null;
            if (new File(testInvoiceFile).exists()) {
                responses[0] = Helper.readQR (testInvoiceFile, copyInvoiceFile,"First Scan Error", SHOW_OUTPUTCODE);
                if (responses[0] != null) {
                    invoice = Helper.serialize (responses[0], "Serialize Error", SHOW_OUTPUTCODE);
                }
            } else {
                invoice = Helper.generate ("Failed to generate QR code", QR_VERSION);
            }

            if (invoice != null) {
                try {
                    responses[1] = invoice.getQRCode();

                    if (Helper.writeQR(copyInvoiceFile, invoice, "QR Generation Error", QR_LENGTH, SHOW_OUTPUTCODE)) {
                        responses[2] = Helper.readQR (copyInvoiceFile, copyInvoiceFile, "Second Scan Error", SHOW_OUTPUTCODE);
                    }
                } catch (QRBill.QRBillException e) {
                    Helper.logMessage("Error)", "Malformed QR Code.");
                }

                Helper.showAlternitiveSchema(invoice);
            }

            Helper.showSummary(responses);
        }

//        Helper.cleanupFiles(COPY_INVOICE);
        
    }
    
}
