package com.example.flightsim.core.math;

/**
 * Immutable three-component vector.
 *
 * @param x first component
 * @param y second component
 * @param z third component
 */
public record Vector3(double x, double y, double z) {

    /** The zero vector. */
    public static final Vector3 ZERO = new Vector3(0.0, 0.0, 0.0);

    /**
     * Adds another vector.
     *
     * @param other vector to add
     * @return sum
     */
    public Vector3 plus(Vector3 other) {
        return new Vector3(x + other.x, y + other.y, z + other.z);
    }

    /**
     * Subtracts another vector.
     *
     * @param other vector to subtract
     * @return difference
     */
    public Vector3 minus(Vector3 other) {
        return new Vector3(x - other.x, y - other.y, z - other.z);
    }

    /**
     * Multiplies by a scalar.
     *
     * @param factor scalar
     * @return scaled vector
     */
    public Vector3 scale(double factor) {
        return new Vector3(x * factor, y * factor, z * factor);
    }

    /**
     * Dot product.
     *
     * @param other other vector
     * @return dot product
     */
    public double dot(Vector3 other) {
        return x * other.x + y * other.y + z * other.z;
    }

    /**
     * Cross product, {@code this × other}.
     *
     * @param other other vector
     * @return cross product
     */
    public Vector3 cross(Vector3 other) {
        return new Vector3(
                y * other.z - z * other.y,
                z * other.x - x * other.z,
                x * other.y - y * other.x);
    }

    /**
     * Euclidean length.
     *
     * @return magnitude
     */
    public double magnitude() {
        return Math.sqrt(dot(this));
    }

    /**
     * Unit vector in the same direction.
     *
     * @return normalised vector
     * @throws ArithmeticException if this is the zero vector
     */
    public Vector3 normalised() {
        double m = magnitude();
        if (m == 0.0) {
            throw new ArithmeticException("Cannot normalise the zero vector");
        }
        return scale(1.0 / m);
    }
}
