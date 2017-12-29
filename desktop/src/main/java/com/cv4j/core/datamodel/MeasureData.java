/*
 * Copyright (c) 2017-present, CV4J Contributors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.cv4j.core.datamodel;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.text.DecimalFormat;
import java.text.NumberFormat;

@NoArgsConstructor
@Setter
@Getter
public class MeasureData {

    private Point cp; // center x of the contour and center y of the contour
    private double angle; // angle of the contour rotated
    private double area; // measure the area of contour
    private double roundness; // measure the possible circle of the contour

    @Override
    public String toString() {

        NumberFormat format = new DecimalFormat("#.00");
        StringBuilder sb = new StringBuilder();
        sb.append("Point:").append(cp.x).append(",").append(cp.y).append("\n")
                .append("angle:").append((Math.abs(angle) == 0 ? 0.0 :format.format(angle))).append("\n")
                .append("area:").append((int)area).append("\n")
                .append("roundness:").append(format.format(roundness));
        return sb.toString();
    }
}
