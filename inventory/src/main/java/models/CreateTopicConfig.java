package models;

public class CreateTopicConfig {

	 private long retentionMs;

	    public CreateTopicConfig(long retentionMs) {
	        this.retentionMs = retentionMs;
	    }

	    public long getRetentionMs() {
	        return retentionMs;
	    }
	    
}
