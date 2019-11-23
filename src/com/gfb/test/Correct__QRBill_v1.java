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

public class Correct__QRBill_v1 {

    /**
     * As the first example QR code includes a number of errors in the alternative schema portion of the code, this
     * class corrects these errors and generates a corrected QR image. The original alternative schema code is:
     *
     * UV1;1.1;1278564;1A-2F-43-AC-9B-33-21-B0-CC-D4-28-56;TCXVMKC22;2019-02-10T15:12:39; 2019-02-10T15:18:16
     * XY2;2a-2.2r;_R1-CH2_ConradCH-2074-1_3350_2019-03-13T10:23:47_16,99_0,00_0,00_0,00_0,00_+8FADt/DQ=_1==
     *
     * The two schema are corrected and reintroduced as:
     *
     * UV;1.1;1278564;1A-2F-43-AC-9B-33-21-B0-CC-D4-28-56;TCXVMKC22;2019-02-10T15:12:39;2019-02-10T15:18:16
     * XY_2a-2.2r;_R1-CH2_ConradCH-2074-1_3350_2019-03-13T10:23:47_16,99_0,00_0,00_0,00_0,00_+8FADt/DQ=_1==
     *
     */

    // The file path of the input QR Code file
    private static final String     TEST_INVOICE    = "res/qr_tests_v1/Invoice1.png";
    // The file path of the output QR Code file
    private static final String     COPY_INVOICE    = "res/qr_tests_v1/Invoice1_new.png";
    // The width & height of the output QR Code file
    private static final int        QR_LENGTH       = 300;
    // The corrected alternative schema
    private static final String[]    NEW_SCHEMA     = {
            "UV;1.1;1278564;1A-2F-43-AC-9B-33-21-B0-CC-D4-28-56;TCXVMKC22;2019-02-10T15:12:39;2019-02-10T15:18:16",
            "XY_2a-2.2r;_R1-CH2_ConradCH-2074-1_3350_2019-03-13T10:23:47_16,99_0,00_0,00_0,00_0,00_+8FADt/DQ=_1=="
    };

    public static void main(String[] args) {

        QRBillHelper.logMessage("Test Subject: " + TEST_INVOICE + "\n");

        String[] responses = new String[3];

        File copyFile = new File(COPY_INVOICE);
        if (copyFile.exists())
            copyFile.delete();

        QRBill invoice = null;
        if (new File(TEST_INVOICE).exists()) {
            responses[0] = QRBillHelper.readQR (TEST_INVOICE, COPY_INVOICE,"First Scan Error", false);
            if (responses[0] != null) {
                invoice = QRBillHelper.serialize (responses[0], "Serialize Error", false);
            }
        } else {
            invoice = QRBillHelper.generate ("File not found Error", 1.0F);
        }

        if (invoice != null) {
            invoice.setAlternativeSchema(NEW_SCHEMA);

            try {
                responses[1] = invoice.getQRCode();

                if (QRBillHelper.writeQR(COPY_INVOICE, invoice, "QR Generation Error", QR_LENGTH, false)) {
                    responses[2] = QRBillHelper.readQR(COPY_INVOICE, COPY_INVOICE, "Second Scan Error", false);
                }
            } catch (QRBill.QRBillException e) {
                QRBillHelper.logMessage("Error)", "Malformed QR Code.");
            }

            QRBillHelper.showAlternitiveSchema(invoice);
        }
        QRBillHelper.showSummary(responses);
//        QRBillHelper.cleanupFiles(COPY_INVOICE);
    }

}
