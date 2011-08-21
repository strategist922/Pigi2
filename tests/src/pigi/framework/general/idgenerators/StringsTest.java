package pigi.framework.general.idgenerators;

import static org.junit.Assert.*;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.junit.Test;

public class StringsTest {

    private static final String MINUS_LETTER_A = new String(new int[]{0x10ffbe}, 0, 1);

    @Test
    public void testInvert_empty() {
        assertEquals("", Strings.invert(""));
    }

    @Test
    public void testInvert_singlechar() {
        assertEquals("ﾞ", Strings.invert("a"));
    }

    @Test
    public void testInvert_big_unicode_char() {
        assertEquals("A", Strings.invert(MINUS_LETTER_A));
    }

    @Test
    public void testInvert_two_big_unicode_chars() {
        assertEquals("AA", Strings.invert(MINUS_LETTER_A + MINUS_LETTER_A));
    }

    @Test
    public void testInvert_big_unicode_with_others() {
        assertEquals("xAyz", Strings.invert("ﾇ" + MINUS_LETTER_A + "ﾆﾅ"));
    }

    @Test
    public void testInvert_big_unicode_with_regular() {
        assertEquals("ﾇAﾆﾅ", Strings.invert("x" + MINUS_LETTER_A + "yz"));
    }

    // TODO(vlad): make it a test
    @Test
    public void mixerTest() {
        List<String> aList = new ArrayList<String>();
        aList.add("a1");
        aList.add("a2");
        List<String> bList = new ArrayList<String>();
        bList.add("b1");
        bList.add("b2");
        List<String> cList = new ArrayList<String>();
        cList.add("c1");
        cList.add("c2");
        List<String> dList = new ArrayList<String>();
        dList.add("d1");
        Map<String, List<String>> param = new HashMap<String, List<String>>();
    
        param.put("a", aList);
        param.put("b", bList);
        param.put("c", cList);
        param.put("d", dList);
    
        List<String> out2 = Strings.mix(param, ", ");
        for (int i = 0; i < out2.size(); i++) {
            System.out.println(out2.get(i));
        }
    }
}
