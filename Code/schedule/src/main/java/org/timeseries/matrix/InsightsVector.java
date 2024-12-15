package org.timeseries.matrix;

import java.io.Serializable;

public class InsightsVector implements Serializable {

    private static final long serialVersionUID = 43L;

    protected int _m = -1;
    protected double[] _data = null;
    protected boolean _valid = false;

    public InsightsVector(int m, double value) {
        if (m <= 0) {
            throw new RuntimeException("[InsightsVector] invalid size");
        } else {
            _data = new double[m];
            for (int j = 0; j < m; ++j) {
                _data[j] = value;
            }
            _m = m;
            _valid = true;
        }
    }

    public InsightsVector(double[] data, boolean deepCopy) {
        if (data == null || data.length == 0) {
            throw new RuntimeException("[InsightsVector] invalid data");
        } else {
            _m = data.length;
            if (deepCopy) {
                _data = new double[_m];
                System.arraycopy(data, 0, _data, 0, _m);
            } else {
                _data = data;
            }
            _valid = true;
        }
    }

    public double[] deepCopy() {
        double[] dataDeepCopy = new double[_m];
        System.arraycopy(_data, 0, dataDeepCopy, 0, _m);
        return dataDeepCopy;
    }

    public double get(int i) {
        if (!_valid) {
            throw new RuntimeException("[InsightsVector] invalid Vector");
        } else if (i >= _m) {
            throw new IndexOutOfBoundsException(
                String.format("[InsightsVector] Index: %d, Size: %d", i, _m));
        }
        return _data[i];
    }

    public int size() {
        if (!_valid) {
            throw new RuntimeException("[InsightsVector] invalid Vector");
        }

        return _m;
    }

    public void set(int i, double val) {
        if (!_valid) {
            throw new RuntimeException("[InsightsVector] invalid Vector");
        } else if (i >= _m) {
            throw new IndexOutOfBoundsException(
                String.format("[InsightsVector] Index: %d, Size: %d", i, _m));
        }
        _data[i] = val;
    }

    public double dot(InsightsVector vector) {
        if (!_valid || !vector._valid) {
            throw new RuntimeException("[InsightsVector] invalid Vector");
        } else if (_m != vector.size()) {
            throw new RuntimeException("[InsightsVector][dot] invalid vector size.");
        }

        double sumOfProducts = 0;
        for (int i = 0; i < _m; i++) {
            sumOfProducts += _data[i] * vector.get(i);
        }
        return sumOfProducts;
    }
}
