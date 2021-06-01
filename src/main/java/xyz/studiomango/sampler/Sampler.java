package xyz.studiomango.sampler;

import xyz.studiomango.sampler.nodes.Node;

/**
 * The class that contains a bunch of static methods that you might want to use
 * @author nahkd
 *
 */
public class Sampler {
    
    /**
     * Connect nodes in a chain. The first node connect to second one and so on.
     * @param nodes Array of nodes to connect
     */
    public static void connectChain(Node... nodes) {
        for (int i = 0; i < nodes.length - 1; i++) nodes[i].connectTo(nodes[i + 1]);
    }
    
}
