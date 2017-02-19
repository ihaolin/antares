package me.hao0.antares.common.zk;

/**
 * Node child listener
 */
public abstract class NodeListener {

    /**
     * Callback when the node is created, or the node data is updated
     * @param newData the node new data
     */
    public void onUpdate(byte[] newData){}

    /**
     * Callback when the node is deleted
     */
    public void onDelete(){}
}