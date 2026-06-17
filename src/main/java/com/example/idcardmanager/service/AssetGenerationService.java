package com.example.idcardmanager.service;

import com.example.idcardmanager.model.BarcodeType;
import com.google.zxing.BarcodeFormat;
import com.google.zxing.client.j2se.MatrixToImageWriter;
import com.google.zxing.common.BitMatrix;
import com.google.zxing.oned.Code128Writer;
import com.google.zxing.oned.EAN13Writer;
import com.google.zxing.qrcode.QRCodeWriter;
import org.springframework.stereotype.Service;

import java.io.ByteArrayOutputStream;
import java.util.Base64;

@Service
public class AssetGenerationService {

    public String generateQRCodeBase64(String text) throws Exception {
        QRCodeWriter writer = new QRCodeWriter();
        BitMatrix bitMatrix = writer.encode(text, BarcodeFormat.QR_CODE, 200, 200);
        return toBase64(bitMatrix);
    }

    public String generateBarcodeBase64(String text, BarcodeType type) throws Exception {
        BitMatrix bitMatrix;
        if (type == BarcodeType.EAN_13) {
            // EAN-13 requires exactly 13 digits numeric string
            EAN13Writer writer = new EAN13Writer();
            bitMatrix = writer.encode(text, BarcodeFormat.EAN_13, 250, 80);
        } else {
            Code128Writer writer = new Code128Writer();
            bitMatrix = writer.encode(text, BarcodeFormat.CODE_128, 250, 80);
        }
        return toBase64(bitMatrix);
    }

    private String toBase64(BitMatrix matrix) throws Exception {
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        MatrixToImageWriter.writeToStream(matrix, "PNG", out);
        return Base64.getEncoder().encodeToString(out.toByteArray());
    }
}