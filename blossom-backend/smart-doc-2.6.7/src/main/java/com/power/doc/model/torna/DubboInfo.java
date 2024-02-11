package com.power.doc.model.torna;

/**
 * @author xingzi 2021/4/28 12:54
 **/
public class DubboInfo {

    private String interfaceName;
    private String author;
    private String version;
    private String protocol;
    private String dependency;

    public DubboInfo builder() {
        return new DubboInfo();
    }

    public String getInterfaceName() {
        return interfaceName;
    }

    public DubboInfo setInterfaceName(String interfaceName) {
        this.interfaceName = interfaceName;
        return this;
    }

    public String getAuthor() {
        return author;
    }

    public DubboInfo setAuthor(String author) {
        this.author = author;
        return this;
    }

    public String getVersion() {
        return version;
    }

    public DubboInfo setVersion(String version) {
        this.version = version;
        return this;
    }

    public String getProtocol() {
        return protocol;
    }

    public DubboInfo setProtocol(String protocol) {
        this.protocol = protocol;
        return this;
    }

    public String getDependency() {
        return dependency;
    }

    public DubboInfo setDependency(String dependency) {
        this.dependency = dependency;
        return this;
    }

    @Override
    public String toString() {
        return "DubboInfo{" +
            "interfaceName='" + interfaceName + '\'' +
            ", author='" + author + '\'' +
            ", version='" + version + '\'' +
            ", protocol='" + protocol + '\'' +
            ", dependency='" + dependency + '\'' +
            '}';
    }
}
