/*
 * Copyright 2016-2018 Leon Chen
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.moilioncircle.redis.replicator.cmd.impl;

import com.moilioncircle.redis.replicator.cmd.CommandSpec;

/**
 * @author Leon Chen
 * @since 2.1.0
 */
@CommandSpec(command = "GEOADD")
public class GeoAddCommand extends GenericKeyCommand {

    private static final long serialVersionUID = 1L;

    private Geo[] geos;
    private boolean ch;
    private ExistType existType;

    public GeoAddCommand() {
    }

    public GeoAddCommand(byte[] key, Geo[] geos) {
        this(key, geos, ExistType.NONE, false);
    }
    
    /**
     * @since 3.5.2
     * @param key key
     * @param geos geos
     * @param existType existType
     * @param ch ch
     */
    public GeoAddCommand(byte[] key, Geo[] geos, ExistType existType, boolean ch) {
        super(key);
        this.geos = geos;
        this.existType = existType;
        this.ch = ch;
    }

    public Geo[] getGeos() {
        return geos;
    }

    public void setGeos(Geo[] geos) {
        this.geos = geos;
    }
    
    /**
     * @since 3.5.2
     * @return ch
     */
    public boolean isCh() {
        return ch;
    }
    
    /**
     * @since 3.5.2
     * @param ch ch
     */
    public void setCh(boolean ch) {
        this.ch = ch;
    }
    
    /**
     * @since 3.5.2
     * @return existType
     */
    public ExistType getExistType() {
        return existType;
    }
    
    /**
     * @since 3.5.2
     * @param existType existType
     */
    public void setExistType(ExistType existType) {
        this.existType = existType;
    }
}
