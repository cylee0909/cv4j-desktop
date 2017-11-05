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
package com.cv4j.core.filters;

import com.cv4j.core.datamodel.ImageProcessor;
import com.cv4j.image.util.Tools;

public class GlowFilter extends GaussianBlurFilter{

	private float amount = 0.2f;
	private int radius;
	
	public void setAmount( float amount ) {
		this.amount = amount;
	}
	
	public float getAmount() {
		return amount;
	}

	@Override
	public ImageProcessor doFilter(ImageProcessor src){

		int total = width*height;
		byte[] R1 = new byte[total];
		byte[] G1 = new byte[total];
		byte[] B1 = new byte[total];
		System.arraycopy(R, 0, R1, 0, total);
		System.arraycopy(G, 0, G1, 0, total);
		System.arraycopy(B, 0, B1, 0, total);

		// 高斯模糊
		super.doFilter(src);

		float a = 4*amount;

		int index = 0;
		for ( int y = 0; y < height; y++ ) {
			for ( int x = 0; x < width; x++ ) {
				int r1 = R[index] & 0xff;
				int g1 = G[index] & 0xff;
				int b1 = B[index] & 0xff;

				int r2 = R1[index] & 0xff;
				int g2 = G1[index] & 0xff;
				int b2 = B1[index] & 0xff;

				R[index] = (byte)Tools.clamp( (int)(r1 + a * r2) );
				G[index] = (byte)Tools.clamp( (int)(g1 + a * g2) );
				B[index] = (byte)Tools.clamp( (int)(b1 + a * b2) );
				index++;
			}
		}
		return src;
    }
}
