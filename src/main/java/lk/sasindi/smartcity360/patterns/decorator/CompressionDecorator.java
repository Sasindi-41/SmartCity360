package lk.sasindi.smartcity360.patterns.decorator;

import lk.sasindi.smartcity360.model.EnergyStreamContext;

import java.io.ByteArrayOutputStream;
import java.util.zip.Deflater;
import java.util.zip.DeflaterOutputStream;

public class CompressionDecorator extends EnergyStreamDecorator {

    public CompressionDecorator(EnergyStream wrappedStream) {
        super(wrappedStream);
    }

    @Override
    public void process(EnergyStreamContext context) throws Exception {

        super.process(context);

        try {
            ByteArrayOutputStream output = new ByteArrayOutputStream();

            try (DeflaterOutputStream deflater =
                         new DeflaterOutputStream(output, new Deflater(Deflater.BEST_COMPRESSION))) {

                deflater.write(context.getPayload());
            }

            context.setPayload(output.toByteArray());
            context.setCompressionAlgorithm("DEFLATE");

            context.addLog(
                    "CompressionDecorator",
                    "COMPRESS",
                    true,
                    "Energy stream successfully compressed using DEFLATE."
            );

        } catch (Exception exception) {

            context.addLog(
                    "CompressionDecorator",
                    "COMPRESS",
                    false,
                    "Compression failed: " + safeMessage(exception)
            );

            throw exception;
        }
    }

    private String safeMessage(Exception exception) {
        return exception.getMessage() == null
                ? "Unknown compression error"
                : exception.getMessage();
    }
}