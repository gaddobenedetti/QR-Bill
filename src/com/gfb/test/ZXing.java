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

import com.google.zxing.*;
import com.google.zxing.client.j2se.BufferedImageLuminanceSource;
import com.google.zxing.client.j2se.MatrixToImageWriter;
import com.google.zxing.common.BitMatrix;
import com.google.zxing.common.HybridBinarizer;
import com.google.zxing.qrcode.QRCodeWriter;
import com.google.zxing.qrcode.decoder.ErrorCorrectionLevel;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.nio.file.FileSystems;
import java.nio.file.Path;
import java.util.HashMap;
import java.util.Hashtable;
import java.util.Map;


public class ZXing {
    
    public static String readQRImage(File qrCode) throws IOException {
        BufferedImage bufferedImage = ImageIO.read(qrCode);
        LuminanceSource source = new BufferedImageLuminanceSource(bufferedImage);
        BinaryBitmap bitmap = new BinaryBitmap(new HybridBinarizer(source));
        
        MultiFormatReader reader = new MultiFormatReader();
        Hashtable<DecodeHintType, Object> hint = new Hashtable<DecodeHintType, Object>();
        hint.put(DecodeHintType.TRY_HARDER, BarcodeFormat.QR_CODE);

        String contents = null;
        try {
            Result result = reader.decode(bitmap, hint);
            contents = result.getText();
        } catch (NotFoundException e) {
            Helper.logMessage("Error 6: " + e.getMessage());
        }
        
        return contents;
    }
    
    public static void generateQRCodeImage(String text, int width, int height, String filePath, String embeddedImage) throws WriterException, IOException, NotFoundException {
        final float maxRatio = 0.12F;
        BitMatrix bitMatrix;
        
        QRCodeWriter qrCodeWriter = new QRCodeWriter();
        if (embeddedImage == null) {
            bitMatrix = qrCodeWriter.encode(text, BarcodeFormat.QR_CODE, width, height);
        } else {
            Map<EncodeHintType, ErrorCorrectionLevel> hints = new HashMap<>();
            hints.put(EncodeHintType.ERROR_CORRECTION, ErrorCorrectionLevel.M);
            bitMatrix = qrCodeWriter.encode(text, BarcodeFormat.QR_CODE, width, height, hints);
            BufferedImage qrImage = MatrixToImageWriter.toBufferedImage(bitMatrix);
            BufferedImage embImage = ImageIO.read(new File(embeddedImage));
            
            float widthRatio = (float) embImage.getWidth() / (float) qrImage.getWidth();
            float heightRatio = (float) embImage.getHeight() / (float) qrImage.getHeight();
            
            if (widthRatio < heightRatio) {
                if (heightRatio != maxRatio) {
                    int h = (int) (maxRatio * (float) qrImage.getHeight());
                    int w = (int) (((float) h / embImage.getHeight()) * (float) embImage.getWidth());
                    embImage = createScaledBufferedImage(embImage, w, h);
                }
            } else {
                if (widthRatio != maxRatio) {
                    int w = (int) (maxRatio * (float) qrImage.getWidth());
                    int h = (int) (((float) w / embImage.getWidth()) * (float) embImage.getHeight());
                    embImage = createScaledBufferedImage(embImage, w, h);
                }
            }

            int deltaWidth = qrImage.getWidth() - embImage.getWidth();
            int deltaHeight = qrImage.getHeight() - embImage.getHeight();

            BufferedImage combined = new BufferedImage(qrImage.getHeight(), qrImage.getWidth(), BufferedImage.TYPE_INT_ARGB);
            Graphics2D g = (Graphics2D) combined.getGraphics();
            g.drawImage(qrImage, 0, 0, null);
            g.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, 1f));
            g.drawImage(embImage, (int) Math.round(deltaWidth / 2), (int) Math.round(deltaHeight / 2), null);
            
            HybridBinarizer hb = new HybridBinarizer(new BufferedImageLuminanceSource(combined));
            bitMatrix = hb.getBlackMatrix();
        }
        
        Path path = FileSystems.getDefault().getPath(filePath);
        MatrixToImageWriter.writeToPath(bitMatrix, "PNG", path);
    }
    
    private static BufferedImage createScaledBufferedImage (BufferedImage embeddedImage, int width, int height) {
        Image tmp = embeddedImage.getScaledInstance(width, height, Image.SCALE_SMOOTH);
        BufferedImage dimg = new BufferedImage(width, height, BufferedImage.TYPE_INT_ARGB);

        Graphics2D g2d = dimg.createGraphics();
        g2d.drawImage(tmp, 0, 0, null);
        g2d.dispose();
        
        return dimg;
    }
    
}
