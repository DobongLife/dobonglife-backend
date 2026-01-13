package com.umust.dobonglife.global.common.webclient.business.data;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;
import lombok.Getter;

@Getter
@Data
public class BusinessData {

    @JsonProperty("b_no")
    private String businessNumber;  // 사업자번호

    @JsonProperty("b_stt")
    private String businessStatus;  // 예: "계속사업자"

    @JsonProperty("b_stt_cd")
    private String businessStatusCode;  // 예: "01" ← 우리가 사용할 값

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