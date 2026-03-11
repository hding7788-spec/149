package ext.casc.ixb;

import java.io.InputStream;

import wt.util.WTContext;
import wt.util.WTProperties;

public class IXBUtil {
    

    private static final String SITE_DOMAIN_PRO = "wt.inf.container.SiteOrganization.internetDomain";

    public static String getDomainValue(){
        String domainOpp = new String();
        try {
            WTProperties wtp = WTProperties.getLocalProperties();
            String SITE_ORG_FILE = wtp.getProperty(
                    "wt.inf.container.SiteOrganization.file",
                    "wt/inf/container/SiteOrganization.properties");
            InputStream istream = null;
            try {
                istream = WTContext.getContext().getResourceAsStream(
                        SITE_ORG_FILE);
                wtp = new WTProperties(/* no defaults */null);
                if (istream != null) {
                    wtp.load(istream);
                }
                String SITE_DOMAIN_VALUE;
                SITE_DOMAIN_VALUE = wtp.getProperty(SITE_DOMAIN_PRO);
                String[] domainSplit = SITE_DOMAIN_VALUE.split("[.]");
                domainOpp = domainSplit[0];
                for (int i = 1; i < domainSplit.length; i++)
                    domainOpp = domainSplit[i] + "." + domainOpp;
            } finally {
                if (istream != null) {
                    istream.close();
                }
            }
        } catch (Throwable t) {
            throw new ExceptionInInitializerError(t);
        }
        return domainOpp;
    }

    
}
