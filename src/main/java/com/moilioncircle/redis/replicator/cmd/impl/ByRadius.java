/*
 * Copyright 2016-2017 Leon Chen
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

/**
 * @author Leon Chen
 * @since 3.5.0
 */
public class ByRadius {
	private static final long serialVersionUID = 1L;
	
	private double radius;
	private UnitType unitType;
	
	public ByRadius() {
	}
	
	public ByRadius(double radius, UnitType unitType) {
		this.radius = radius;
		this.unitType = unitType;
	}
	
	public double getRadius() {
		return radius;
	}
	
	public void setRadius(double radius) {
		this.radius = radius;
	}
	
	public UnitType getUnitType() {
		return unitType;
	}
	
	public void setUnitType(UnitType unitType) {
		this.unitType = unitType;
	}
}
