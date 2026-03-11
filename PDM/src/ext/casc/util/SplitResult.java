package ext.casc.util;

import java.util.Arrays;

public  class SplitResult {
    private final String[] parameters;
    private final String delimiter;

    public SplitResult(String[] parameters, String delimiter) {
        this.parameters = parameters;
        this.delimiter = delimiter;
    }

    public String[] getParameters() {
        return parameters;
    }

    public String getDelimiter() {
        return delimiter;
    }

    @Override
    public String toString() {
        return "Parameters: " + Arrays.toString(parameters) +
                ", Delimiter: " + (delimiter != null ? delimiter : "none");
    }
}
