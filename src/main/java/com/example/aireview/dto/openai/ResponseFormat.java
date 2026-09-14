package com.example.aireview.dto.openai;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ResponseFormat {

    private String type;

    public static ResponseFormat jsonObject() {
        return new ResponseFormat("json_object");
    }
}
