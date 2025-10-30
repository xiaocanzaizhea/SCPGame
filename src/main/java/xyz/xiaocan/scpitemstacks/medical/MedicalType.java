package xyz.xiaocan.scpitemstacks.medical;

import lombok.Getter;

@Getter
public enum MedicalType {
    MEDICALBAG("medicalbag"),
    PAINKILLER("painkiller"),
    STIMULANT("stimulant");

    MedicalType(String id){
        this.id = id;
    }

    String id;

    public static MedicalType getMedicalType(String id){
        for (MedicalType medical:MedicalType.values()) {
            if(medical.id.equals(id))return medical;
        }
        return null;
    }
}
