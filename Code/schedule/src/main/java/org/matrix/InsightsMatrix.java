package org.matrix;

import java.io.Serializable;

public class InsightsMatrix implements Serializable {

    public static final long serialVersionUID = 42L;

    protected int _m = -1;
    protected int _n = -1;
    protected double[][] _data = null;
    protected boolean _valid = false;

    protected boolean _cholZero = false;
    protected boolean _cholPos = false;
    protected boolean _cholNeg = false;
    protected double[] _cholD = null;
    protected double[][] _cholL = null;

    public InsightsMatrix(double[][] data, boolean makeDeepCopy) {
        if (_valid = isValid2D(data)) {
            _m = data.length;
            _n = data[0].length;
            if (!makeDeepCopy) {
                _data = data;
            } else {
                _data = copy2DArray(data);
            }

        }
    }

    private static boolean isValid2D(double[][] matrix) {
        boolean result = true;
        if (matrix == null || matrix[0] == null || matrix[0].length == 0) {
            throw new RuntimeException("[InsightsMatrix][constructor] null data given");
        } else {
            int row = matrix.length;
            int col = matrix[0].length;
            for (int i = 1; i < row; ++i) {
                if (matrix[i] == null || matrix[i].length != col) {
                    result = false;
                }
            }
        }

        return result;
    }

    private static double[][] copy2DArray(double[][] source) {
        if (source == null) {
            return null;
        } else if (source.length == 0) {
            return new double[0][];
        }

        int row = source.length;
        double[][] target = new double[row][];
        for (int i = 0; i < row; i++) {
            if (source[i] == null) {
                target[i] = null;
            } else {
                int rowLength = source[i].length;
                target[i] = new double[rowLength];
                System.arraycopy(source[i], 0, target[i], 0, rowLength);
            }
        }
        return target;
    }

    public int getNumberOfRows() {
        return _m;
    }

    public int getNumberOfColumns() {
        return _n;
    }

    public double get(int i, int j) {
        return _data[i][j];
    }

    public void set(int i, int j, double val) {
        _data[i][j] = val;
    }

    public InsightsVector timesVector(InsightsVector v) {
        if (!_valid || !v._valid || _n != v._m) {
            throw new RuntimeException("[InsightsMatrix][timesVector] size mismatch");
        }
        double[] data = new double[_m];
        double dotProduc;
        for (int i = 0; i < _m; ++i) {
            InsightsVector rowVector = new InsightsVector(_data[i], false);
            dotProduc = rowVector.dot(v);
            data[i] = dotProduc;
        }
        return new InsightsVector(data, false);
    }

    private boolean computeCholeskyDecomposition(final double maxConditionNumber) {
        _cholD = new double[_m];
        _cholL = new double[_m][_n];
        int i;
        int j;
        int k;
        double val;
        double currentMax = -1;
        // Backward marching method
        for (j = 0; j < _n; ++j) {
            val = 0;
            for (k = 0; k < j; ++k) {
                val += _cholD[k] * _cholL[j][k] * _cholL[j][k];
            }
            double diagTemp = _data[j][j] - val;
            final int diagSign = (int) (Math.signum(diagTemp));
            switch (diagSign) {
                case 0:    // singular diagonal value detected
                    if (maxConditionNumber < -0.5) { // no bound on maximum condition number
                        _cholZero = true;
                        _cholL = null;
                        _cholD = null;
                        return false;
                    } else {
                        _cholPos = true;
                    }
                    break;
                case 1:
                    _cholPos = true;
                    break;
                case -1:
                    _cholNeg = true;
                    break;
            }
            if (maxConditionNumber > -0.5) {
                if (currentMax <= 0.0) { // this is the first time
                    if (diagSign == 0) {
                        diagTemp = 1.0;
                    }
                } else { // there was precedent
                    if (diagSign == 0) {
                        diagTemp = Math.abs(currentMax / maxConditionNumber);
                    } else {
                        if (Math.abs(diagTemp * maxConditionNumber) < currentMax) {
                            diagTemp = diagSign * Math.abs(currentMax / maxConditionNumber);
                        }
                    }
                }
            }
            _cholD[j] = diagTemp;
            if (Math.abs(diagTemp) > currentMax) {
                currentMax = Math.abs(diagTemp);
            }
            _cholL[j][j] = 1;
            for (i = j + 1; i < _m; ++i) {
                val = 0;
                for (k = 0; k < j; ++k) {
                    val += _cholD[k] * _cholL[j][k] * _cholL[i][k];
                }
                val = ((_data[i][j] + _data[j][i]) / 2 - val) / _cholD[j];
                _cholL[j][i] = val;
                _cholL[i][j] = val;
            }
        }
        return true;
    }

    public InsightsVector solveSPDIntoVector(InsightsVector b, final double maxConditionNumber) {
        if (!_valid || b == null || _n != b._m) {
            // invalid linear system
            throw new RuntimeException(
                "[InsightsMatrix][solveSPDIntoVector] invalid linear system");
        }
        if (_cholL == null) {
            // computing Cholesky Decomposition
            this.computeCholeskyDecomposition(maxConditionNumber);
        }
        if (_cholZero) {
            // singular matrix. returning null
            return null;
        }

        double[] y = new double[_m];
        double[] bt = new double[_n];
        int i;
        int j;
        for (i = 0; i < _m; ++i) {
            bt[i] = b._data[i];
        }
        double val;
        for (i = 0; i < _m; ++i) {
            val = 0;
            for (j = 0; j < i; ++j) {
                val += _cholL[i][j] * y[j];
            }
            y[i] = bt[i] - val;
        }
        for (i = _m - 1; i >= 0; --i) {
            val = 0;
            for (j = i + 1; j < _n; ++j) {
                val += _cholL[i][j] * bt[j];
            }
            bt[i] = y[i] / _cholD[i] - val;
        }
        return new InsightsVector(bt, false);
    }

    public InsightsMatrix computeAAT() {
        if (!_valid) {
            throw new RuntimeException("[InsightsMatrix][computeAAT] invalid matrix");
        }
        final double[][] data = new double[_m][_m];
        for (int i = 0; i < _m; ++i) {
            final double[] rowI = _data[i];
            for (int j = 0; j < _m; ++j) {
                final double[] rowJ = _data[j];
                double temp = 0;
                for (int k = 0; k < _n; ++k) {
                    temp += rowI[k] * rowJ[k];
                }
                data[i][j] = temp;
            }
        }
        return new InsightsMatrix(data, false);
    }
}
