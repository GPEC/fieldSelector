/*
 * parameter string, for communication between this applet, server and nuclei counter applet
 * 
 * use case:
 * 1. server pass param string to field selector applet
 * 2. field selector applet display all available field selection
 * 3. user click on field selector to select existing or new selection
 * 4. field selector applet send param string (includes ALL selection + focused/selected selection) to server
 * 5. server send nuclei selection param string to nuclei counter applet 
 * 6. nuclei counter applet activated and user starts count nuclei
 * 
 * 7. (WHEN) user click on the field selector applet to select a new or existing field
 * 8. field selector applet send message to nuclei counter applet to upload nuclei selection
 * 9. go to step 4 to 6
 * 
 * reminder note: the field selector is NOT responsible for making sure enough nuclei is counted.
 * 
 * 
 * parameter string format: (all numbers are in pixels, in coordinate system of ORIGINAL image i.e. not preview/lowres image)
 * [x-coordinate]x[y-coordinate]y[field diameter]pp[categorical staining level][viewing state flag][scoring state flag]_
 * 
 * viewing state flag
 * CURRENT = c
 * PREVIEW = p
 * NOT_CURRENT = n
 * 
 * scoring state flag
 * NOT_SCORED = o
 * SCORING = i
 * SCORED = s
 * 
 * e.g.
 * 3822x4856y4000pp0no_13474x4347y4000pp1no_34563x5981y4000pp2ns_46164x3418y4000pp4no_7299x1828y4000pp3cs
 * first selection @ x=3822, y=4856, negligible Ki67, field diameter=4000, not current viewing, not scored
 * second selection @ x=13474, y=4347, low Ki67, field diameter=4000, not current scoring, not scored
 * third selection @ x=34563, y=5981, medium Ki67, field diameter=4000, not current scoring, scored
 * 
 * the second selection is the current "scoring" field of view
 * 
 */
package ca.ubc.gpec.ia.fieldselector.model;

import ca.ubc.gpec.ia.fieldselector.model.FieldOfView.ScoringState;
import ca.ubc.gpec.ia.fieldselector.exception.FieldSelectionParamStringParseException;
import ca.ubc.gpec.ia.fieldselector.model.FieldOfView.Ki67State;
import ca.ubc.gpec.ia.fieldselector.model.FieldOfView.ViewingState;
import java.util.ArrayList;

/**
 *
 * @author samuelc
 */
public class FieldSelectionParamStringParser {

    public static final String DELIMITER = "_";
    public static final String TAG_X = "x";
    public static final String TAG_Y = "y";
    public static final String TAG_VIEWING_STATE_CURRENT = "c";
    public static final String TAG_VIEWING_STATE_PREVIEW = "p";
    public static final String TAG_VIEWING_STATE_NOT_CURRENT = "n";
    public static final String TAG_SCORING_STATE_NOT_SCORED = "o";
    public static final String TAG_SCORING_STATE_SCORING = "i";
    public static final String TAG_SCORING_STATE_SCORED = "s";
    public static final String TAG_KI67_PP = "pp";
    public static final int KI67_PP_LEVEL_HIGHEST = 4; // highest percent positive i.e. hotstpot
    public static final int KI67_PP_LEVEL_HIGH = 3; // high percent positive
    public static final int KI67_PP_LEVEL_MEDIUM = 2; // med percent positive
    public static final int KI67_PP_LEVEL_LOW = 1; // low percent positive
    public static final int KI67_PP_LEVEL_NEGLIGIBLE = 0; // negligible percent positive
    private ArrayList<FieldOfView> selections;

    /**
     * convert Ki67State to numeric code
     *
     * @param ki67State
     * @return
     */
    public static int ki67StateToNumericCode(Ki67State ki67State) {
        switch (ki67State) {
            case HOT_SPOT:
                return KI67_PP_LEVEL_HIGHEST;
            case HIGH:
                return KI67_PP_LEVEL_HIGH;
            case MEDIUM:
                return KI67_PP_LEVEL_MEDIUM;
            case LOW:
                return KI67_PP_LEVEL_LOW;
            case NEGLIGIBLE:
                return KI67_PP_LEVEL_NEGLIGIBLE;
            default:
                return -1; // unknown Ki67State
        }
    }

    /**
     * convert numeric code to Ki67State
     *
     * @param code
     * @return
     */
    public static Ki67State numericCodeToKi67State(int code) {
        switch (code) {
            case KI67_PP_LEVEL_HIGHEST:
                return Ki67State.HOT_SPOT;
            case KI67_PP_LEVEL_HIGH:
                return Ki67State.HIGH;
            case KI67_PP_LEVEL_MEDIUM:
                return Ki67State.MEDIUM;
            case KI67_PP_LEVEL_LOW:
                return Ki67State.LOW;
            case KI67_PP_LEVEL_NEGLIGIBLE:
                return Ki67State.NEGLIGIBLE;
            default:
                return null;
        }
    }

    /**
     * constructor
     *
     * see above for example for inputParamString
     *
     * @param paramString
     * @throws FieldSelectionParamStringParseException
     */
    public FieldSelectionParamStringParser(String inputParamString) throws FieldSelectionParamStringParseException {
        String paramString = inputParamString;
        paramString = paramString == null ? "" : paramString.trim();
        selections = new ArrayList<FieldOfView>();

        // do parsing!!!
        if (paramString.length() == 0) {
            return; // no need to do anything else since there is no selection
        }

        for (String selectionString : paramString.split(DELIMITER)) {
            String[] temp = selectionString.split(TAG_X);
            if (temp.length != 2) {
                throw new FieldSelectionParamStringParseException("trying to get x value, parsing: (" + selectionString + ") within: " + paramString);
            }
            int x = Integer.parseInt(temp[0]);
            String[] temp2 = temp[1].split(TAG_Y);
            if (temp2.length != 2) {
                throw new FieldSelectionParamStringParseException("trying to get y value, parsing: (" + temp[1] + ") within: " + paramString);
            }
            int y = Integer.parseInt(temp2[0]);
            // figure out scoring state ... need to check this first because ASSUME scoring flag ALWAYS appear after viewing flag
            ScoringState scoringState = ScoringState.NOT_SCORED; // default
            if (temp2[1].endsWith(TAG_SCORING_STATE_SCORING)) {
                scoringState = ScoringState.SCORING;
                temp2[1] = temp2[1].substring(0, temp2[1].length() - TAG_SCORING_STATE_SCORING.length());
            } else if (temp2[1].endsWith(TAG_SCORING_STATE_SCORED)) {
                scoringState = ScoringState.SCORED;
                temp2[1] = temp2[1].substring(0, temp2[1].length() - TAG_SCORING_STATE_SCORED.length());
            } else if (temp2[1].endsWith(TAG_SCORING_STATE_NOT_SCORED)) {
                scoringState = ScoringState.NOT_SCORED;
                temp2[1] = temp2[1].substring(0, temp2[1].length() - TAG_SCORING_STATE_NOT_SCORED.length());
            }
            // figure out view state ... 
            ViewingState viewingState = ViewingState.NOT_CURRENT; // default
            if (temp2[1].endsWith(TAG_VIEWING_STATE_CURRENT)) {
                viewingState = ViewingState.CURRENT;
                temp2[1] = temp2[1].substring(0, temp2[1].length() - TAG_VIEWING_STATE_CURRENT.length());
            } else if (temp2[1].endsWith(TAG_VIEWING_STATE_PREVIEW)) {
                viewingState = ViewingState.PREVIEW;
                temp2[1] = temp2[1].substring(0, temp2[1].length() - TAG_VIEWING_STATE_PREVIEW.length());
            } else if (temp2[1].endsWith(TAG_VIEWING_STATE_NOT_CURRENT)) {
                viewingState = ViewingState.NOT_CURRENT;
                temp2[1] = temp2[1].substring(0, temp2[1].length() - TAG_VIEWING_STATE_NOT_CURRENT.length());
            }

            String[] temp3 = temp2[1].split(TAG_KI67_PP);
            int diameter = Integer.parseInt(temp3[0]);
            Ki67State ki67State;
            switch (Integer.parseInt(temp3[1])) {
                case KI67_PP_LEVEL_HIGHEST:
                    ki67State = Ki67State.HOT_SPOT;
                    break;
                case KI67_PP_LEVEL_HIGH:
                    ki67State = Ki67State.HIGH;
                    break;
                case KI67_PP_LEVEL_MEDIUM:
                    ki67State = Ki67State.MEDIUM;
                    break;
                case KI67_PP_LEVEL_LOW:
                    ki67State = Ki67State.LOW;
                    break;
                default:
                    ki67State = Ki67State.NEGLIGIBLE;
                    break;
            }
            selections.add(new FieldOfView(x, y, diameter, viewingState, scoringState, ki67State));
        }
    }

    /**
     * constructor
     *
     * @param selections
     */
    public FieldSelectionParamStringParser(ArrayList<FieldOfView> selections) {
        this.selections = selections;
    }

    /**
     * generate fieldSelectionParamString
     *
     * @return
     */
    public String generateFieldSelectionParamString() {
        String result = "";
        for (FieldOfView field : selections) {
            result = result + field.getX() + TAG_X + field.getY() + TAG_Y + field.getDiamter() + TAG_KI67_PP;
            // figure out Ki67 state
            result = result + ki67StateToNumericCode(field.getKi67State());

            // figure out viewing state flag
            switch (field.getViewingState()) {
                case CURRENT:
                    result = result + TAG_VIEWING_STATE_CURRENT;
                    break;
                case PREVIEW:
                    result = result + TAG_VIEWING_STATE_PREVIEW;
                    break;
                case NOT_CURRENT:
                    result = result + TAG_VIEWING_STATE_NOT_CURRENT;
                    break;
                default:
                    break;
            }
            // figure out scoring state flag
            switch (field.getScoringState()) {
                case NOT_SCORED:
                    result = result + TAG_SCORING_STATE_NOT_SCORED;
                    break;
                case SCORING:
                    result = result + TAG_SCORING_STATE_SCORING;
                    break;
                case SCORED:
                    result = result + TAG_SCORING_STATE_SCORED;
                    break;
                default:
                    break;
            }
            result = result + DELIMITER;
        }
        return result.length() == 0 ? "" : result.substring(0, result.length() - DELIMITER.length()); // need to get rid of the last DELIMITER
    }

    /**
     * return current scoring selection
     *
     * returns null if no current scoring selection or selection is empty
     *
     * @return
     */
    public FieldOfView getCurrentScoringSelection() {
        for (FieldOfView fieldOfView : selections) {
            if (fieldOfView.isCurrentViewing()) {
                return fieldOfView;
            }
        }
        return null; // no current scoring selection or selection is empty
    }

    /**
     * return list of all scored selection
     *
     * return empty array if none of the selections are scored or no selection
     * at all.
     *
     * @return
     */
    public ArrayList<FieldOfView> getScoredSelections() {
        ArrayList<FieldOfView> result = new ArrayList<FieldOfView>();
        for (FieldOfView fieldOfView : selections) {
            if (fieldOfView.isScored()) {
                result.add(fieldOfView);
            }
        }
        return result;
    }

    /**
     * return array list of selections
     *
     * @return
     */
    public ArrayList<FieldOfView> getAllSelections() {
        return selections;
    }
}
