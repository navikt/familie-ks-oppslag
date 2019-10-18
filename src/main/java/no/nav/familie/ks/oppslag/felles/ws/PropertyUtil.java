package no.nav.familie.ks.oppslag.felles.ws;

public class PropertyUtil {
	
	private PropertyUtil() {
		// hidden ctor
	}

    public static String getProperty(String key) {
        String val = System.getProperty(key);
        if (val == null) {
            val = System.getenv(key.toUpperCase().replace('.', '_'));
        }
        return val;
    }
}
