/*
 * exception encoutered when trying to parse FieldSelectionParamString
 */
package ca.ubc.gpec.ia.fieldselector.exception;

/**
 *
 * @author samuelc
 */
public class FieldSelectionParamStringParseException extends Exception {

	private static final long serialVersionUID = 1L;

	public FieldSelectionParamStringParseException(String msg) {
        super(msg);
    }
}
