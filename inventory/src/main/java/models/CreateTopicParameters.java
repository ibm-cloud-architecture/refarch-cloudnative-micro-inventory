package models;

/**
 * Copyright 2015-2016 IBM
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  See the License for the specific language governing permissions and
 *  limitations under the License.
 */
/**
 * Licensed Materials - Property of IBM
 * (c) Copyright IBM Corp. 2015-2016
 */

public class CreateTopicParameters {
	private final String topicName;

    private final int partitionCount;

    private final CreateTopicConfig config;

    public CreateTopicParameters(String topicName, int partitionCount, CreateTopicConfig config) {
        this.topicName = topicName;
        this.partitionCount = partitionCount;
        this.config = config;
    }

    public String getTopicName() {
        return topicName;
    }

    public int getPartitionCount() {
        return partitionCount;
    }

    public CreateTopicConfig getConfig() {
        return config;
    }
}
