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
import com.cv4j.image.util.TaskUtils;
import com.cv4j.image.util.Tools;

import java.util.concurrent.Callable;
import java.util.concurrent.CompletionService;
import java.util.concurrent.ExecutorCompletionService;
import java.util.concurrent.ExecutorService;

public class GaussianBlurFilter extends BaseFilter {

    private float[] kernel;
    private double sigma = 2;
    ExecutorService mExecutor;
    CompletionService<Void> service;

    public GaussianBlurFilter() {
        kernel = new float[0];
    }

    public void setSigma(double a) {
        this.sigma = a;
    }

    @Override
    public ImageProcessor doFilter(final ImageProcessor src){

        final int size = width*height;
        int dims = src.getChannels();
        makeGaussianKernel(sigma, 0.002, Math.min(width, height));

        mExecutor = TaskUtils.newFixedThreadPool("cv4j",dims);
        service = new ExecutorCompletionService<>(mExecutor);

        // save result
        for(int i=0; i<dims; i++) {

            final int temp = i;
            service.submit(new Callable<Void>() {
                public Void call() throws Exception {
                    byte[] inPixels = src.toByte(temp);
                    byte[] temp = new byte[size];
                    blur(inPixels, temp, width, height); // H Gaussian
                    blur(temp, inPixels, height, width); // V Gaussain
                    return null;
                }
            });
        }

        for (int i = 0; i < dims; i++) {
            try {
                service.take();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }

        mExecutor.shutdown();
        return src;
    }

    /**
     * <p> here is 1D Gaussian        , </p>
     *
     * @param inPixels
     * @param outPixels
     * @param width
     * @param height
     */
    private void blur(byte[] inPixels, byte[] outPixels, int width, int height)
    {
        int subCol = 0;
        int index = 0, index2 = 0;
        float sum = 0;
        int k = kernel.length-1;
        for(int row=0; row<height; row++) {
            int c = 0;
            index = row;
            for(int col=0; col<width; col++) {
                sum = 0;
                for(int m = -k; m< kernel.length; m++) {
                    subCol = col + m;
                    if(subCol < 0 || subCol >= width) {
                        subCol = 0;
                    }
                    index2 = row * width + subCol;
                    c = inPixels[index2] & 0xff;
                    sum += c * kernel[Math.abs(m)];
                }
                outPixels[index] = (byte)Tools.clamp(sum);
                index += height;
            }
        }
    }

    public void makeGaussianKernel(final double sigma, final double accuracy, int maxRadius) {
        int kRadius = (int)Math.ceil(sigma*Math.sqrt(-2*Math.log(accuracy)))+1;
        if (maxRadius < 50) maxRadius = 50;         // too small maxRadius would result in inaccurate sum.
        if (kRadius > maxRadius) kRadius = maxRadius;
        kernel = new float[kRadius];
        for (int i=0; i<kRadius; i++)               // Gaussian function
            kernel[i] = (float)(Math.exp(-0.5*i*i/sigma/sigma));
        double sum;                                 // sum over all kernel elements for normalization
        if (kRadius < maxRadius) {
            sum = kernel[0];
            for (int i=1; i<kRadius; i++)
                sum += 2*kernel[i];
        } else
            sum = sigma * Math.sqrt(2*Math.PI);

        for (int i=0; i<kRadius; i++) {
            double v = (kernel[i]/sum);
            kernel[i] = (float)v;
        }
    }
}
