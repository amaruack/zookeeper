package com.eseict.zoo.proc.node;

import com.eseict.zoo.exception.ZookeeperException;

public interface NodeProcess {

    public void init(NodeConfig config) throws ZookeeperException;

    public void init() throws ZookeeperException;

}
