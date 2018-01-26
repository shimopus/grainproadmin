package pro.grain.admin.config;

import org.springframework.boot.context.properties.ConfigurationProperties;

/**
 * Properties specific to Grain.Pro Admin.
 *
 * <p>
 *     Properties are configured in the application.yml file.
 * </p>
 */
@ConfigurationProperties(prefix = "grainproadmin", ignoreUnknownFields = false)
public class GrainProAdminProperties {

    private final PriceUpload priceUpload = new PriceUpload();

    private final Price price = new Price();

    private Mailer mailer = new Mailer();

    private Site site = new Site();

    public PriceUpload getPriceUpload() {
        return priceUpload;
    }

    public Price getPrice() {
        return price;
    }

    public Mailer getMailer() {
        return mailer;
    }

    public Site getSite() {
        return site;
    }

    public void setSite(Site site) {
        this.site = site;
    }

    public static class PriceUpload {
        private int downloadBucketSize = 10000;

        public void setDownloadBucketSize(int downloadBucketSize) {
            this.downloadBucketSize = downloadBucketSize;
        }

        public int getDownloadBucketSize() {
            return downloadBucketSize;
        }
    }

    public static class Price {
        private int currentVersionNumber = 1;

        public void setCurrentVersionNumber(int currentVersionNumber) {
            this.currentVersionNumber = currentVersionNumber;
        }

        public int getCurrentVersionNumber() {
            return currentVersionNumber;
        }
    }

    public static class Mailer {
        private String url = "";

        public String getUrl() {
            return url;
        }

        public void setUrl(String url) {
            this.url = url;
        }
    }

    public static class Site {
        private String baseUrl = "";
        private String sellPagePath = "";
        private String buyPagePath = "";

        public String getSellPagePath() {
            return sellPagePath;
        }

        public void setSellPagePath(String sellPagePath) {
            this.sellPagePath = sellPagePath;
        }

        public String getBuyPagePath() {
            return buyPagePath;
        }

        public void setBuyPagePath(String buyPagePath) {
            this.buyPagePath = buyPagePath;
        }

        public String getBaseUrl() {
            return baseUrl;
        }

        public void setBaseUrl(String baseUrl) {
            this.baseUrl = baseUrl;
        }
    }
}
