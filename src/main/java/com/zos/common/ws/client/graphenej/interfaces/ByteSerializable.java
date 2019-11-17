package com.zos.common.ws.client.graphenej.interfaces;

/**
 * Interface implemented by all entities for which makes sense to have a byte-array representation.
 */
public interface ByteSerializable {

    byte[] toBytes();
}
