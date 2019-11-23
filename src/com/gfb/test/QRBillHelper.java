package com.gfb.test;

import com.google.zxing.NotFoundException;
import com.google.zxing.WriterException;

import java.io.File;
import java.io.IOException;

public class QRBillHelper {

    private static final String     CH_CROSS        = "res/qrbill_kreuz.png";

    public static String readQR (String inputPath, String outputPath, String errorTag, boolean showCode) {
        try {
            String response = ZXing.readQRImage(new File(inputPath));
            if (showCode) {
                if (!inputPath.equals(outputPath)) {
                    logMessage("First Scan:\n" + response + "\n");
                } else {
                    logMessage("Second Scan:\n" + response + "\n");
                }
            }
            return response;
        } catch (IOException e) {
            logMessage("Error", errorTag + ": " + e.getMessage() + "\n");
            return null;
        }
    }

    public static QRBill serialize (String qrData, String errorTag, boolean showCode) {
        try {
            QRBill invoice = new QRBill(qrData);
            String response = invoice.getQRCode();
            if (showCode)
                logMessage("Serialized:\n" + response + "\n");
            return invoice;
        } catch (QRBill.QRBillException e) {
            logMessage("Error", errorTag + ": " + e.getMessage() + "\n");
            return null;
        }
    }

    public static QRBill generate (String errorTag, Float version) {
        QRBill invoice = new QRBill();
        invoice.setVersion(version);
        invoice.setActor(QRBill.ACTOR_CR, "CR name", QRBill.ADDTYPE_STRUCTURED, "CR street", "CR housenumber", "CR postalcode", "CR location", "CH");
        invoice.setActor(QRBill.ACTOR_UDR,"UDR name", QRBill.ADDTYPE_STRUCTURED, "UDR street", "UDR housenumber", "UDR postalcode", "UDR location", "CH");
        invoice.setAmount(299.95F);
        invoice.setIBAN("CH1234567890123456789");
        invoice.setCurrency(QRBill.CURRENCY_EUR);
        invoice.setAlternativeSchema("S1;Foobar1;Foobar2;Foobar3;Foobar4;Foobar5", 1);

        invoice.setDueDate(2020, 2, 29);

        try {
            invoice.getQRCode();
            return invoice;
        } catch (QRBill.QRBillException e) {
            logMessage("Error", errorTag + ": " + e.getMessage() + "\n");
            return null;
        }
    }

    public static boolean writeQR (String qrFilePath, QRBill qrData, String errorTag, int length, boolean showCode) {
        try {
            ZXing.generateQRCodeImage(qrData.getQRCode(), length, length, qrFilePath, CH_CROSS);
            return true;
        } catch (IOException e) {
            logMessage("Error", errorTag + ": " + e.getMessage() + "\n");
            return false;
        } catch (WriterException e) {
            logMessage("Error", errorTag + ": " + e.getMessage() + "\n");
            return false;
        } catch (NotFoundException e) {
            logMessage("Error", errorTag + ": " + e.getMessage() + "\n");
            return false;
        } catch (QRBill.QRBillException e) {
            logMessage("Error", errorTag + ": " + e.getMessage() + "\n");
            return false;
        }
    }

    public static void showAlternitiveSchema (QRBill qrBill) {
        for (int i = 0; i < 2; i++)
            showAlternitiveSchema (qrBill, i);
    }

    public static void showAlternitiveSchema (QRBill qrBill, int index) {
        String[] altSch = qrBill.getAlternativeSchema();
        if (altSch != null && altSch[index].length() > 0) {
            String[] temp = qrBill.getAlternativeSchema(index);
            if (temp.length > 2) {
                logMessage("Full Alternative Schema: " + altSch[index]);
                logMessage("Alternative Schema ID: " + temp[0]);
                logMessage("Alternative Schema Seperator: " + temp[1]);
                logMessage("Alternative Schema Data:");
                for (int i = 2; i < temp.length; i++)
                    logMessage("    " + temp[i] + (i == temp.length - 1 ? "\n" : ""));
            } else {
                logMessage("Full Alternative Schema: " + altSch[index] + "\n");
            }

        }
    }

    public static void showSummary (String[] data) {
        if (data[0] == null) {
            logMessage("Generated Not NULL: " + String.valueOf(data[1] != null));
        } else {
            logMessage("First Scan Not NULL: " + String.valueOf(data[0] != null));
            logMessage("Serialized Not NULL: " + String.valueOf(data[1] != null));
            logMessage("Serialized COPY: " + (data[1] == null ? "NULL" : String.valueOf(data[0].equalsIgnoreCase(data[1]))));
        }
        logMessage("Second Scan Not NULL: " + String.valueOf(data[2] != null));
        logMessage("Second Scan COPY: " + (data[1] == null ? "NULL" : String.valueOf(data[1].equalsIgnoreCase(data[2])) + "\n\n"));
    }

    public static void cleanupFiles (String filename) {
        for (int i = 0; i < 4; i++) {
            File fileToDelete = new File(filename.replace("#", String.valueOf(i + 1)));
            if (fileToDelete.exists())
                fileToDelete.delete();
        }
    }

    public static void logMessage (String msg) {
        logMessage ("QR", msg);
    }

    public static void logMessage (String tag, String msg) {
        System.out.println("[" + tag + "] " + msg);
    }
}
