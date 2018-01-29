package models;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;

import org.eclipse.microprofile.config.inject.ConfigProperty;

@ApplicationScoped
public class ElasticsearchConfig {
	
	@Inject
    @ConfigProperty(name="elasticsearch-url")
	private String url;
	
    private String user;
    
    private String password;
    
    @Inject
    @ConfigProperty(name="elasticsearch-index")
    private String index;
    
    @Inject
    @ConfigProperty(name="elasticsearch-doc_type")
    private String doc_type;

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String getUser() {
        return user;
    }

    public void setUser(String user) {
        this.user = user;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getIndex() {
        return index;
    }

    public void setIndex(String index) {
        this.index = index;
    }

    public String getDoc_type() {
        return doc_type;
    }

    public void setDoc_type(String doc_type) {
        this.doc_type = doc_type;
    }
}
