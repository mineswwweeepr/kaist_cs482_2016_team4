package kr.ac.kaist.vclab.bubble;

import android.opengl.GLES20;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by avantgarde on 2016-11-02.
 */

public class MapGenerator {
    // size
    float sizeX;
    float sizeY;
    float sizeZ;

    // dimension
    int dimX;
    int dimZ;

    // unit length
    float unit;

    // height map (2D array of y values)
    float[][] heightMap;

    // flag for filling the faces
    boolean fill;

    public MapGenerator(float sizeX, float sizeY, float sizeZ, float unit, boolean fill) {
        this.unit = unit;
        this.fill = fill;

        this.dimX = (int) (sizeX / this.unit);
        this.dimZ = (int) (sizeZ / this.unit);

        this.sizeX = this.dimX * this.unit;
        this.sizeZ = this.dimZ * this.unit;
        this.sizeY = sizeY;

        heightMap = new float[this.dimX + 1][this.dimZ + 1];

        for (int i = 0; i <= this.dimX; i++) {
            for (int j = 0; j <= this.dimZ; j++) {
                heightMap[i][j] = 0;
            }
        }

        for (int i = 0; i <= this.dimX; i++) {
            for (int j = 0; j <= this.dimZ; j++) {
                float nx = i / (float) dimX - 0.5f;
                float nz = j / (float) dimZ - 0.5f;

                heightMap[i][j] = 4.0f * (
                        PerlinNoise.noise(nx * 3, nz * 3, 1)
                                + 0.5f * PerlinNoise.noise(nx * 6, nz * 6, 2)
                                + 0.25f * PerlinNoise.noise(nx * 12, nz * 12, 4)
                );

                if (heightMap[i][j] < -sizeY) {
                    heightMap[i][j] = -sizeY;
                }
            }
        }
    }

    public float[] getVertices() {
        List<float[]> buffer = new ArrayList<>();

        if (fill) {
            // top
            for (int i = 0; i < dimX; i++) {
                for (int j = 0; j < dimZ; j++) {
                    // lower triangle
                    buffer.add(new float[]{
                            unit * i, heightMap[i][j], unit * j,
                            unit * i, heightMap[i][j + 1], unit * (j + 1),
                            unit * (i + 1), heightMap[i + 1][j + 1], unit * (j + 1)
                    });

                    // upper triangle
                    buffer.add(new float[]{
                            unit * i, heightMap[i][j], unit * j,
                            unit * (i + 1), heightMap[i + 1][j + 1], unit * (j + 1),
                            unit * (i + 1), heightMap[i + 1][j], unit * j
                    });
                }
            }

            // bottom
            buffer.add(new float[]{
                    sizeX, -sizeY, sizeZ,
                    0, -sizeY, 0,
                    sizeX, -sizeY, 0
            });

            buffer.add(new float[]{
                    0, -sizeY, 0,
                    sizeX, -sizeY, sizeZ,
                    0, -sizeY, sizeZ
            });

            // front
            for (int i = 0; i < dimX; i++) {
                // lower triangle
                buffer.add(new float[]{
                        unit * i, heightMap[i][dimZ], sizeZ,
                        unit * i, -sizeY, sizeZ,
                        unit * (i + 1), -sizeY, sizeZ
                });

                // upper triangle
                buffer.add(new float[]{
                        unit * i, heightMap[i][dimZ], sizeZ,
                        unit * (i + 1), -sizeY, sizeZ,
                        unit * (i + 1), heightMap[i + 1][dimZ], sizeZ
                });
            }

            // back
            for (int i = 0; i < dimX; i++) {
                // lower triangle
                buffer.add(new float[]{
                        unit * (i + 1), -sizeY, 0,
                        unit * i, -sizeY, 0,
                        unit * i, heightMap[i][0], 0
                });

                // upper triangle
                buffer.add(new float[]{
                        unit * (i + 1), heightMap[i + 1][0], 0,
                        unit * (i + 1), -sizeY, 0,
                        unit * i, heightMap[i][0], 0
                });
            }

            // right
            for (int j = 1; j <= dimZ; j++) {
                // lower triangle
                buffer.add(new float[]{
                        sizeX, heightMap[dimX][j], unit * j,
                        sizeX, -sizeY, unit * j,
                        sizeX, -sizeY, unit * (j - 1)
                });

                // upper triangle
                buffer.add(new float[]{
                        sizeX, heightMap[dimX][j], unit * j,
                        sizeX, -sizeY, unit * (j - 1),
                        sizeX, heightMap[dimX][j - 1], unit * (j - 1)
                });
            }


            // left
            for (int j = 1; j <= dimZ; j++) {
                // lower triangle
                buffer.add(new float[]{
                        0, -sizeY, unit * (j - 1),
                        0, -sizeY, unit * j,
                        0, heightMap[0][j], unit * j
                });

                // upper triangle
                buffer.add(new float[]{
                        0, heightMap[0][j - 1], unit * (j - 1),
                        0, -sizeY, unit * (j - 1),
                        0, heightMap[0][j], unit * j
                });
            }

            return listToArray(buffer, 9);
        } else {
            // top
            for (int i = 0; i <= dimX; i++) {
                for (int j = 0; j <= dimZ; j++) {
                    // rows
                    if (i != dimX) {
                        buffer.add(new float[]{
                                unit * i, heightMap[i][j], unit * j,
                                unit * (i + 1), heightMap[i + 1][j], unit * j
                        });
                    }

                    // columns
                    if (j != dimZ) {
                        buffer.add(new float[]{
                                unit * i, heightMap[i][j], unit * j,
                                unit * i, heightMap[i][j + 1], unit * (j + 1)
                        });
                    }

                    // diagonals
                    if ((i != dimX) && (j != dimZ)) {
                        buffer.add(new float[]{
                                unit * i, heightMap[i][j], unit * j,
                                unit * (i + 1), heightMap[i + 1][j + 1], unit * (j + 1)
                        });
                    }
                }
            }

            // bottom
            buffer.add(new float[]{
                    0, -sizeY, 0,
                    sizeX, -sizeY, 0
            });

            buffer.add(new float[]{
                    sizeX, -sizeY, 0,
                    sizeX, -sizeY, sizeZ
            });

            buffer.add(new float[]{
                    sizeX, -sizeY, sizeZ,
                    0, -sizeY, sizeZ
            });

            buffer.add(new float[]{
                    0, -sizeY, sizeZ,
                    0, -sizeY, 0
            });

            // sides
            buffer.add(new float[]{
                    0, heightMap[0][0], 0,
                    0, -sizeY, 0
            });

            buffer.add(new float[]{
                    sizeX, heightMap[dimX][0],
                    0, sizeX, -sizeY, 0
            });

            buffer.add(new float[]{
                    0, heightMap[0][dimZ],
                    sizeZ, 0, -sizeY, sizeZ
            });

            buffer.add(new float[]{
                    sizeX, heightMap[dimX][dimZ],
                    sizeZ, sizeX, -sizeY, sizeZ
            });

            return listToArray(buffer, 6);
        }
    }

    public float[] getNormals() {
        List<float[]> buffer = new ArrayList<>();

        if (fill) {
            // top
            for (int i = 0; i < dimX * dimZ * 6; i++) {
                buffer.add(new float[]{0.0f, 1.0f, 0.0f});
            }

            // bottom
            for (int i = 0; i < 2; i++) {
                buffer.add(new float[]{0.0f, -1.0f, 0.0f});
            }

            // front
            for (int i = 0; i < dimX * 6; i++) {
                buffer.add(new float[]{0.0f, 0.0f, 1.0f});
            }

            // back
            for (int i = 0; i < dimX * 6; i++) {
                buffer.add(new float[]{0.0f, 0.0f, -1.0f});
            }

            // right
            for (int i = 0; i < dimZ * 6; i++) {
                buffer.add(new float[]{1.0f, 0.0f, 0.0f});
            }

            // left
            for (int i = 0; i < dimZ * 6; i++) {
                buffer.add(new float[]{-1.0f, 0.0f, 0.0f});
            }
        } else {
            // top
            for (int i = 0; i < (dimX * (dimZ + 1) + (dimX + 1) * dimZ + dimX * dimZ) * 3; i++) {
                buffer.add(new float[]{0.0f, 1.0f, 0.0f});
            }

            // bottom
            for (int i = 0; i < 8; i++) {
                buffer.add(new float[]{0.0f, -1.0f, 0.0f});
            }

            // sides
            for (int i = 0; i < 8; i++) {
                buffer.add(new float[]{0.0f, 1.0f, 0.0f});
            }
        }

        return listToArray(buffer, 3);
    }

    public int getMode() {
        if (fill) {
            return GLES20.GL_TRIANGLES;
        } else {
            return GLES20.GL_LINES;
        }
    }

    private float[] listToArray(List<float[]> buffer, int elemSize) {
        int bufferSize = buffer.size();
        float[] result = new float[bufferSize * elemSize];

        for (int i = 0; i < bufferSize; i++) {
            float[] elem = buffer.get(i);

            for (int j = 0; j < elemSize; j++) {
                result[i * elemSize + j] = elem[j];
            }
        }

        return result;
    }
}