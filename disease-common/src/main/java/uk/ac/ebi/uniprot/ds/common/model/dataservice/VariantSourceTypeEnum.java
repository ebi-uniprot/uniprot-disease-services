package uk.ac.ebi.uniprot.ds.common.model.dataservice;

public enum VariantSourceTypeEnum {
    large_scale_study("large scale study"), uniprot("uniprot"), mixed("mixed");
    private String sourceName;
    VariantSourceTypeEnum(String sourceName) {
        this.sourceName = sourceName;
    }
    public static VariantSourceTypeEnum getVariantSourceTypeEnum(String sourceName) {
        VariantSourceTypeEnum result = null;
        for (VariantSourceTypeEnum item : VariantSourceTypeEnum.values()) {
            if (item.sourceName.equalsIgnoreCase(sourceName) || item.name().equalsIgnoreCase(sourceName)) {
                result = item;
                break;
            }
        }
        return result;
    }
}

