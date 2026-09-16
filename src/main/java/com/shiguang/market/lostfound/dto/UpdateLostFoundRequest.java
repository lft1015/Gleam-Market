package com.shiguang.market.lostfound.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.time.LocalDateTime;

@Data
public class UpdateLostFoundRequest {
    @NotBlank private String title;
    private String description;
    private String images;
    @NotBlank private String type;
    @NotBlank private String location;
    @NotBlank private String contact;
    @NotNull @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss") private LocalDateTime lostFoundTime;
}
