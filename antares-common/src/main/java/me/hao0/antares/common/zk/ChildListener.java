package me.hao0.antares.common.zk;

/**
 * Node child listener
 */
public abstract class ChildListener {

    /**
     * Callback when a child add
     * @param path the child node path
     * @param data the child node data
     */
    protected void onAdd(String path, byte[] data) {
    }

    /**
     * Callback when a child is deleted
     * @param path the deleted child node path
     */
    protected void onDelete(String path) {
    }

    /**
     * Callback when a child is updated
     * @param path    the child node path
     * @param newData the child node new data
     */
    protected void onUpdate(String path, byte[] newData) {
    }
}