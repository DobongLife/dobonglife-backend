package com.umust.dobonglife.infra.webclient.business.data;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

@Data
public class BusinessData {

    @JsonProperty("b_no")
    private String businessNumber;

    @JsonProperty("b_stt")
    private String businessStatus;

    @JsonProperty("b_stt_cd")
    private String businessStatusCode;

    @JsonProperty("tax_type")
    private String taxType;

    @JsonProperty("tax_type_cd")
    private String taxTypeCode;

    @JsonProperty("end_dt")
    private String endDate;

    @JsonProperty("utcc_yn")
    private String utccYn;

    @JsonProperty("tax_type_change_dt")
    private String taxTypeChangeDate;

    @JsonProperty("invoice_apply_dt")
    private String invoiceApplyDate;

    @JsonProperty("rbf_tax_type")
    private String rbfTaxType;

    @JsonProperty("rbf_tax_type_cd")
    private String rbfTaxTypeCode;
}
