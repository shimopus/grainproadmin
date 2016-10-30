package pro.grain.admin.service.error;

public class EntityConstrainViolation extends RuntimeException {
    private String objectName;
    private String fieldName;
    private String violationType;

    public EntityConstrainViolation(String objectName, String fieldName, String violationType) {
        this.objectName = objectName;
        this.fieldName = fieldName;
        this.violationType = violationType;
    }

    public String getObjectName() {
        return objectName;
    }

    public String getFieldName() {
        return fieldName;
    }

    public String getViolationType() {
        return violationType;
    }
}
