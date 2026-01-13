package com.umust.dobonglife.global.common.webclient.business.parser;

import com.umust.dobonglife.global.common.webclient.business.data.BusinessData;
import com.umust.dobonglife.global.common.webclient.business.dto.response.BusinessStatusResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.util.Collections;
import java.util.List;
import java.util.Map;

@Component
@RequiredArgsConstructor
public class BusinessStatusParser {

    private final ObjectMapper objectMapper;

    public BusinessStatusResponse toDto(Map<String, Object> response) {
        if (response == null) {
            throw new IllegalStateException("응답이 null입니다.");
        }

        return objectMapper.convertValue(response, BusinessStatusResponse.class);
    }

    public List<BusinessData> parseData(Map<String, Object> response) {
        BusinessStatusResponse dto = toDto(response);
        if (dto.getData() == null) {
            return Collections.emptyList();
        }
        return dto.getData();
    }

    /** 편의 메서드 — 첫 번째 사업자의 상태 코드(b_stt_cd)만 추출 */
    public String extractStatusCode(Map<String, Object> response) {
        List<BusinessData> dataList = parseData(response);

        if (dataList.isEmpty()) {
            throw new IllegalStateException("data 필드가 비어있습니다.");
        }

        String code = dataList.get(0).getBusinessStatusCode();
        if (code == null || code.isBlank()) {
            throw new IllegalStateException("b_stt_cd 값을 찾을 수 없습니다.");
        }

        return code;
    }
}