package duck.switches.android.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import java.util.Objects;

@JsonIgnoreProperties(ignoreUnknown = true)
public class Switch {
    private int id;
    private int order;
    private String name;
    private String localAddress;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getOrder() {
        return order;
    }

    public void setOrder(int order) {
        this.order = order;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getLocalAddress() {
        return localAddress;
    }

    public void setLocalAddress(String localAddress) {
        this.localAddress = localAddress;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Switch aSwitch = (Switch) o;
        return id == aSwitch.id
                && order == aSwitch.order
                && Objects.equals(name, aSwitch.name)
                && Objects.equals(localAddress, aSwitch.localAddress);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, order, name, localAddress);
    }

    @Override
    public String toString() {
        return name;
    }
}

