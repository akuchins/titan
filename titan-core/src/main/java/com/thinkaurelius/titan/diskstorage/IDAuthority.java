package com.thinkaurelius.titan.diskstorage;

import com.thinkaurelius.titan.core.attribute.Duration;
import com.thinkaurelius.titan.diskstorage.keycolumnvalue.KeyRange;
import com.thinkaurelius.titan.graphdb.configuration.GraphDatabaseConfiguration;
import com.thinkaurelius.titan.graphdb.database.idassigner.IDBlockSizer;

import java.util.List;

/**
 * Handles the unique allocation of ids. Returns blocks of ids that are uniquely allocated to the caller so that
 * they can be used to uniquely identify elements. *
 *
 * @author Matthias Broecheler (me@matthiasb.com)
 */

public interface IDAuthority {

    /**
     * Returns a block of new ids in the form of {@link IDBlock}. It is guaranteed that
     * the block of ids for the particular partition id is uniquely assigned,
     * that is, the block of ids has not been previously and will not
     * subsequently be assigned again when invoking this method on the local or
     * any remote machine that is connected to the underlying storage backend.
     * <p/>
     * In other words, this method has to ensure that ids are uniquely assigned
     * per partition.
     * <p/>
     * It is furthermore guaranteed that any id of the returned IDBlock is smaller than the upper bound
     * for the given partition as read from the {@link IDBlockSizer} set on this IDAuthority and that the
     * number of ids returned is equal to the block size of the IDBlockSizer.
     *
     * @param partition
     *            Partition for which to request an id block
     * @param idNamespace namespace for ids within a partition
     * @param timeout
     *            When a call to this method is unable to return a id block
     *            before this timeout elapses, the implementation must give up
     *            and throw a {@code StorageException} ASAP
     * @return a range of ids for the {@code partition} parameter
     */
    public IDBlock getIDBlock(int partition, int idNamespace, Duration timeout)
            throws StorageException;

    /**
     * Returns the lower and upper limits of the key range assigned to this local machine as an array with two entries.
     *
     * @return
     * @throws StorageException
     */
    public List<KeyRange> getLocalIDPartition() throws StorageException;

    /**
     * Sets the {@link IDBlockSizer} to be used by this IDAuthority. The IDBlockSizer specifies the block size for
     * each partition guaranteeing that the same partition will always be assigned the same block size.
     * <p/>
     * The IDBlockSizer cannot be changed for an IDAuthority that has already been used (i.e. after invoking {@link #getIDBlock(int)}.
     *
     * @param sizer The IDBlockSizer to be used by this IDAuthority
     */
    public void setIDBlockSizer(IDBlockSizer sizer);

    /**
     * Closes the IDAuthority and any underlying storage backend.
     *
     * @throws StorageException
     */
    public void close() throws StorageException;

    /**
     * Return the globally unique string used by this {@code IDAuthority}
     * instance to recognize its ID allocations and distinguish its allocations
     * from those belonging to other {@code IDAuthority} instances.
     *
     * This should normally be the value of
     * {@link GraphDatabaseConfiguration#UNIQUE_INSTANCE_ID}, though that's not
     * strictly technically necessary.
     *
     * @return unique ID string
     */
    public String getUniqueID();


}
