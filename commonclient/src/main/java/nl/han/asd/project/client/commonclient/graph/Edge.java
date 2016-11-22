package nl.han.asd.project.client.commonclient.graph;

import org.apache.commons.lang.builder.EqualsBuilder;
import org.apache.commons.lang.builder.HashCodeBuilder;

public class Edge {
    private String destinationNodeId;

    private float weight;

    public Edge(String destinationNodeId, float weight) {
        this.destinationNodeId = destinationNodeId;
        this.weight = weight;
    }

    public String getDestinationNodeId() {
        return destinationNodeId;
    }

    public float getWeight() {
        return weight;
    }

    @Override
    public boolean equals(Object obj) {
        if (!(obj instanceof Edge)) {
            return false;
        }
        if (obj == this) {
            return true;
        }

        Edge edgeToCompare = (Edge) obj;
        return new EqualsBuilder().append(destinationNodeId, edgeToCompare.destinationNodeId)
                .append(weight, edgeToCompare.weight).isEquals();
    }

    @Override
    public int hashCode() {
        return new HashCodeBuilder(17, 31).append(destinationNodeId).append(weight).toHashCode();
    }
}
