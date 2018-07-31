package de.infinit.forge;

import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Stream;

import org.apache.commons.math3.complex.Complex;
import org.apache.commons.math3.transform.DftNormalization;
import org.apache.commons.math3.transform.FastFourierTransformer;
import org.apache.commons.math3.transform.TransformType;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

public class NormalityDescriptorTest {
    @Test
    public void testGetSamplesFromResourceFile() {
        final long samplingRate = 1;
        final String level = "error";
        final String inputFile = "test-4.log";
        // final String inputFile = "test-100_000.log";
        // final String inputFile = "test-1_000_000.log";
        NormalityDescriptor nd = new NormalityDescriptor();
        Map<String, List<Long>> samples = nd.getSamples(inputFile, samplingRate, level);
        Assertions.assertEquals(3, samples.get(level).size());
        Assertions.assertEquals(2, samples.get(level).get(0).longValue());
        Assertions.assertEquals(1, samples.get(level).get(1).longValue());
        Assertions.assertEquals(1, samples.get(level).get(2).longValue());
    }

    @Test
    public void testGetSamplesFromStream() {
        final String input = "test-4.log";
        NormalityDescriptor nd = new NormalityDescriptor();
        Assertions.assertTrue(nd.getStreamSamples(input));
    }

    @Test
    public void testRegExp() {
        final String original = "[2018-07-27T11:00:39+02:00] [exercitationem:alert] [pid 8465:tid 1299] [client: 219.6.70.13] You can't hack the microchip without connecting the cross-platform SAS hard drive!";

        String regex = "\\[(.*)]\\s\\[(.*)]\\s\\[(.*)]\\s\\[(.*)]\\s(.+)";
        Pattern pattern = Pattern.compile(regex);
        Matcher matches = pattern.matcher(original);

        Assertions.assertTrue(matches.find());
        Assertions.assertEquals("2018-07-27T11:00:39+02:00", matches.group(1));
        Assertions.assertEquals("exercitationem:alert", matches.group(2));
        Assertions.assertEquals("pid 8465:tid 1299", matches.group(3));
        Assertions.assertEquals("client: 219.6.70.13", matches.group(4));
        Assertions.assertEquals("You can't hack the microchip without connecting the cross-platform SAS hard drive!",
                matches.group(5));
    }

    @Test
    public void ffmAndIffmTest() {
        double[] samples = { 1, 0, -1, 0, 1, 0, -1, 0 }; // FFM: 'length' must be a "Zweierpotenz" => 2^4 = 8

        // forward transformation
        FastFourierTransformer transformer = new FastFourierTransformer(DftNormalization.STANDARD);
        Complex[] _forward = transformer.transform(samples, TransformType.FORWARD);
        double[] forward = Stream.of(_forward).mapToDouble(Complex::getReal).toArray(); // fetch real parts of complex
                                                                                        // results
        // inverse transformation
        Complex[] _inverse = transformer.transform(forward, TransformType.INVERSE);
        double[] inverse = Stream.of(_inverse).mapToDouble(Complex::getReal).toArray();

        // System.out.println("samples: " + Arrays.toString(samples));
        // System.out.println("ffm:     " + Arrays.toString(forward));
        // System.out.println("iffm:    " + Arrays.toString(inverse));

        // assertTrue(Arrays.equals(samples, inverse));
    }
}
