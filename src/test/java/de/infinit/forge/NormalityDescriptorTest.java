package de.infinit.forge;

import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;

import de.infinit.forge.NormalityDescriptor.Probe;

public class NormalityDescriptorTest {
    @Test
    public void testGetProbesFromResourceFile() {
        final String input = "test-4.log";
        // final String input = "test-100_000.log";
        // final String input = "test-1_000_000.log";
        NormalityDescriptor nd = new NormalityDescriptor();
        List<Probe> probes = nd.getProbes(input, 1, "error");
        Assertions.assertEquals(3, probes.size());
        Assertions.assertEquals(2, probes.get(0).getCount());
        Assertions.assertEquals(1, probes.get(1).getCount());
        Assertions.assertEquals(1, probes.get(2).getCount());
    }

    @Test
    public void testGetProbesFromStream() {
        final String input = "test-4.log";
        NormalityDescriptor nd = new NormalityDescriptor();
        Assertions.assertTrue(nd.getProbesFromStream(input));
    }

    @Disabled
    @Test
    public void testRegExp() {
        final String original = "[2018-07-27T11:00:39+02:00] [exercitationem:alert] [pid 8465:tid 1299] [client: 219.6.70.13] You can't hack the microchip without connecting the cross-platform SAS hard drive!";

        String regex = "\\[(.*)\\]\\s\\[(.*)\\]\\s\\[(.*)\\]\\s\\[(.*)\\]\\s(.+)";
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
}
