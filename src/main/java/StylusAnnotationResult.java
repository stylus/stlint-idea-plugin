import utils.LintResult;

class StylusAnnotationResult {

    StylusAnnotationResult(StylusAnnotationInput input, LintResult result) {
        this.input = input;
        this.result = result;
    }

    StylusAnnotationResult(StylusAnnotationInput input, LintResult result, String fileLevel) {
        this.input = input;
        this.result = result;
        this.fileLevel = fileLevel;
    }

    final StylusAnnotationInput input;
    final LintResult result;
    String fileLevel;
}