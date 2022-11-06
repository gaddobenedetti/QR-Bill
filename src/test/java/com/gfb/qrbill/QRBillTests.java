package com.gfb.qrbill;

import static org.junit.Assert.assertTrue;

import java.io.File;

import org.junit.Test;


public class QRBillTests {

    // The file path of the input QR Code file
    private static final String     TEST_INVOICE    = "res/test_qrs/Invoice#.jpg";

    // The file path of the output QR Code file
    private static final String     COPY_INVOICE    = "res/test_qrs/Invoice#_new.jpg";

    // Which of the three input files to be processed (1 - 4). If any other value is used,
    // all will be processed in turn.
    private static final int        QR_INDEX        = 0;

    // The width & height of the output QR Code file
    private static final int        QR_LENGTH       = 300;

    // Whether the raw QR code is also printed after each reading
    private static final boolean    SHOW_OUTPUTCODE = false;

    // The version of the QR code bill, if generated from data
    private static final Float      QR_VERSION      = 2.0F;

    @Test
    public void mainTests() {
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
            QRBillHelper.logMessage("Test Subject: " + testInvoiceFile + "\n");

            File copyFile = new File(copyInvoiceFile);
            if (copyFile.exists())
                copyFile.delete();
            QRBill invoice = null;
            if (new File(testInvoiceFile).exists()) {
                responses[0] = QRBillHelper.readQR (testInvoiceFile, copyInvoiceFile,"First Scan Error", SHOW_OUTPUTCODE);
                if (responses[0] != null) {
                    invoice = QRBillHelper.serialize (responses[0], "Serialize Error", SHOW_OUTPUTCODE);
                }
            } else {
                invoice = QRBillHelper.generate ("Failed to generate QR code", QR_VERSION);
            }

            if (invoice != null) {
                try {
                    responses[1] = invoice.getQRCode();

                    if (QRBillHelper.writeQR(copyInvoiceFile, invoice, "QR Generation Error", QR_LENGTH, SHOW_OUTPUTCODE)) {
                        responses[2] = QRBillHelper.readQR (copyInvoiceFile, copyInvoiceFile, "Second Scan Error", SHOW_OUTPUTCODE);
                    }
                } catch (QRBill.QRBillException e) {
                    QRBillHelper.logMessage("Error)", "Malformed QR Code.");
                }

                QRBillHelper.showAlternitiveSchema(invoice);
            }

            QRBillHelper.showSummary(responses);
        }
    }
}
